package org.ballistic.dreamjournalai.dream_store.data.repository

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
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
                    // Prepare the parameters for the query
                    val params = QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP) // Use INAPP for in-app products, SUBS for subscriptions
                        .build()

                    // Query existing purchases with the new method
                    billingClient.queryPurchasesAsync(params) { billingResult2, purchasesList ->
                        if (billingResult2.responseCode == BillingClient.BillingResponseCode.OK) {
                            purchasesList.forEach { purchase ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    handlePurchase(purchase)
                                }
                            }
                        } else {
                            // Handle any errors
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
                    continuation.resume(productDetailsList) { _, _, _ -> }
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
            val quantity = purchase.quantity  // Ensure this field is available in your Purchase object or fetched appropriately.

            suspend fun verifyPurchaseOnServer(purchase: Purchase): Boolean {
                val baseTokens = when (purchase.products[0]) {
                    "dream_token_100" -> 100
                    "dream_tokens_500" -> 500
                    else -> 0
                }

                val dreamTokens = baseTokens * quantity  // Multiply base tokens by the quantity purchased

                if (dreamTokens == 0) return false

                val data = hashMapOf(
                    "purchaseToken" to purchase.purchaseToken,
                    "purchaseTime" to purchase.purchaseTime,
                    "orderId" to purchase.orderId,
                    "userId" to userId,
                    "dreamTokens" to dreamTokens
                )

                val response =
                    Firebase.functions.getHttpsCallable("handlePurchaseVerification").call(data)
                        .await()
                return (response.data as? Map<*, *>)?.get("success") as? Boolean ?: false
            }

            CoroutineScope(Dispatchers.IO).launch {
                val isPurchaseValid = verifyPurchaseOnServer(purchase)
                if (isPurchaseValid) {
                    val isConsumed = consumePurchase(purchase)
                    if (isConsumed) {
                        acknowledgePurchase(purchase)
                    }
                    continuation.resume(isConsumed) { _, _, _ -> }
                } else {
                    continuation.resume(false) { _, _, _ -> }
                }
            }
        }


    private suspend fun acknowledgePurchase(purchase: Purchase): Boolean =
        suspendCancellableCoroutine { continuation ->
            val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(true) { _, _, _ -> }
                } else {
                    continuation.resume(false) { _, _, _ -> }
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
                    continuation.resume(true) { _, _, _ -> }
                } else {
                    continuation.resume(false) { _, _, _ -> }
                }
            }
        }
}