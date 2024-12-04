package org.ballistic.dreamjournalai.dream_store.domain.repository

import android.app.Activity
import com.android.billingclient.api.*

interface BillingRepository {
    suspend fun queryProductDetails(params: QueryProductDetailsParams): List<ProductDetails>
    suspend fun initiatePurchaseFlow(activity: Activity, productDetails: ProductDetails)
    suspend fun handlePurchase(purchase: Purchase): Boolean
    suspend fun consumePurchase(purchase: Purchase): Boolean
    fun getPurchaseListener(): ((Purchase) -> Unit)?
}
