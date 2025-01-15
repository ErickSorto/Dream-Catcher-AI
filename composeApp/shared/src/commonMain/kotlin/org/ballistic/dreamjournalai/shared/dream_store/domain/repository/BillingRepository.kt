package org.ballistic.dreamjournalai.shared.dream_store.domain.repository

import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Offerings
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PurchasesError
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.models.StoreTransaction

interface BillingRepository {

    /**
     * Fetches your configured Offerings from RevenueCat (if you use Offerings).
     * Will call onSuccess with the retrieved Offerings, or onError on failure.
     */
    fun fetchOfferings(
        onError: (PurchasesError) -> Unit,
        onSuccess: (Offerings) -> Unit
    )

    /**
     * Fetches StoreProducts for the given list of product IDs (if you prefer to
     * not use Offerings, or want to fetch them manually).
     */
    fun fetchProducts(
        productIds: List<String>,
        onError: (PurchasesError) -> Unit,
        onSuccess: (List<StoreProduct>) -> Unit
    )

    /**
     * Purchases a StoreProduct. Use this if you have a `StoreProduct` from `fetchProducts(...)`.
     */
    fun purchaseProduct(
        product: StoreProduct,
        onError: (PurchasesError, userCancelled: Boolean) -> Unit,
        onSuccess: (StoreTransaction, CustomerInfo) -> Unit
    )

    /**
     * (Optional) Purchases a `Package` from Offerings. Use this if you prefer the Offerings system.
     */
    fun purchasePackage(
        packageToPurchase: Package,
        onError: (PurchasesError, userCancelled: Boolean) -> Unit,
        onSuccess: (StoreTransaction, CustomerInfo) -> Unit
    )

    /**
     * Restores the user’s previous purchases (common on iOS, optional on Android).
     */
    fun restorePurchases(
        onError: (PurchasesError) -> Unit,
        onSuccess: (CustomerInfo) -> Unit
    )
}
