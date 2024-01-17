package org.ballistic.dreamjournalai.dream_store.data.repository

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.ballistic.dreamjournalai.dream_store.domain.repository.BillingRepository

class BillingRepositoryImpl(
    val billingClient: BillingClient
) : BillingRepository {
    private var purchaseListener: ((Purchase) -> Unit)? = null

    init {
        connect()
        purchaseListener = { purchase ->
            CoroutineScope(Dispatchers.IO).launch {
                handlePurchase(purchase)
            }
        }
    }

    override fun getPurchaseListener(): ((Purchase) -> Unit)? {
        return purchaseListener
    }

    private fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Query existing purchases
                    billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { _, purchases ->
                        purchases.forEach { purchase ->
                            CoroutineScope(Dispatchers.IO).launch {
                                handlePurchase(purchase)
                            }
                        }
                    }
                } else {
                    // Handle the error case.
                }
            }

            override fun onBillingServiceDisconnected() {
                // Retry to connect to the billing service.
                connect()
            }
        })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun queryProductDetails(params: QueryProductDetailsParams): List<ProductDetails> =
        suspendCancellableCoroutine { continuation ->
            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(productDetailsList, onCancellation = { })
                } else {
                    continuation.resumeWith(Result.failure(Exception(billingResult.debugMessage)))
                }
            }
        }

    override suspend fun initiatePurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        withContext(Dispatchers.Main) {
            billingClient.launchBillingFlow(activity, billingFlowParams)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun handlePurchase(purchase: Purchase): Boolean =
        suspendCancellableCoroutine { continuation ->

            val userId = Firebase.auth.currentUser?.uid
            Log.d("handlePurchase", "Current user UID: $userId")
            suspend fun verifyPurchaseOnServer(purchase: Purchase): Boolean {
                val dreamTokens = when (purchase.skus.firstOrNull()) {
                    "dream_token_100" -> 100
                    "dream_tokens_500" -> 500
                    else -> 0
                }

                if (dreamTokens == 0) return false

                // Use Firebase Functions SDK to call your handlePurchaseVerification function
                val firebaseFunctions = Firebase.functions
                val data = hashMapOf(
                    "purchaseToken" to purchase.purchaseToken,
                    "purchaseTime" to purchase.purchaseTime,
                    "orderId" to purchase.orderId,
                    "userId" to userId,
                    "dreamTokens" to dreamTokens
                )
                Log.d(
                    "handlePurchase",
                    "purchaseToken: ${purchase.purchaseToken}, purchaseTime: ${purchase.purchaseTime}, orderId: ${purchase.orderId}, userId: $userId, dreamTokens: $dreamTokens"
                )
                val response =
                    firebaseFunctions.getHttpsCallable("handlePurchaseVerification").call(data)
                        .await()
                return (response.data as? Map<*, *>)?.get("success") as? Boolean ?: false
            }

            CoroutineScope(Dispatchers.IO).launch {
                val isPurchaseValid = verifyPurchaseOnServer(purchase)
                if (isPurchaseValid) {
                    val isConsumed = consumePurchase(purchase)

                    // Acknowledge the purchase
                    if (isConsumed) {
                        acknowledgePurchase(purchase)
                    }

                    continuation.resume(isConsumed, onCancellation = { })
                } else {
                    continuation.resume(false, onCancellation = { })
                }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun acknowledgePurchase(purchase: Purchase): Boolean =
        suspendCancellableCoroutine { continuation ->
            val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(true, onCancellation = { })
                } else {
                    continuation.resume(false, onCancellation = { })
                }
            }
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun consumePurchase(purchase: Purchase): Boolean =
        suspendCancellableCoroutine { continuation ->
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.consumeAsync(consumeParams) { billingResult, _ ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(true, onCancellation = { })
                } else {
                    continuation.resume(false, onCancellation = { })
                }
            }
        }
}