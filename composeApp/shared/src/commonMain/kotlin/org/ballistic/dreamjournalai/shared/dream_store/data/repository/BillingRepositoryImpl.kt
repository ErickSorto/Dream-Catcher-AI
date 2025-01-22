package org.ballistic.dreamjournalai.shared.dream_store.data.repository

import co.touchlab.kermit.Logger
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesDelegate
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Offerings
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PurchasesError
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.models.StoreTransaction
import org.ballistic.dreamjournalai.shared.dream_store.domain.repository.BillingRepository

private val logger = Logger.withTag("RevenueCatBillingRepositoryImpl")

class RevenueCatBillingRepositoryImpl: BillingRepository {
    init {
        Purchases.sharedInstance.delegate = object : PurchasesDelegate {
            override fun onCustomerInfoUpdated(customerInfo: CustomerInfo) {
                logger.d { "CustomerInfo updated: $customerInfo" }
            }

            override fun onPurchasePromoProduct(
                product: StoreProduct,
                startPurchase: ((PurchasesError, Boolean) -> Unit, (StoreTransaction, CustomerInfo) -> Unit) -> Unit
            ) {
                logger.d { "Purchase promo product initiated for $product" }
            }
        }
    }

    override fun fetchOfferings(
        onError: (PurchasesError) -> Unit,
        onSuccess: (Offerings) -> Unit
    ) {
        logger.d { "Fetching offerings..." }
        Purchases.sharedInstance.getOfferings(
            onError = { error ->
                logger.e { "Error fetching offerings: $error" }
                onError(error)
            },
            onSuccess = { offerings ->
                logger.d { "Offerings fetched successfully: $offerings" }
                onSuccess(offerings)
            }
        )
    }

    override fun fetchProducts(
        productIds: List<String>,
        onError: (PurchasesError) -> Unit,
        onSuccess: (List<StoreProduct>) -> Unit
    ) {
        logger.d { "Fetching products: $productIds" }
        Purchases.sharedInstance.getProducts(
            productIds,
            onError = { error ->
                logger.e { "Error fetching products: $error" }
                onError(error)
            },
            onSuccess = { storeProducts ->
                logger.d { "Products fetched successfully: $storeProducts" }
                onSuccess(storeProducts)
            }
        )
    }

    override fun purchaseProduct(
        product: StoreProduct,
        onError: (PurchasesError, userCancelled: Boolean) -> Unit,
        onSuccess: (StoreTransaction, CustomerInfo) -> Unit
    ) {
        logger.d { "Initiating purchase for product: $product" }
        Purchases.sharedInstance.purchase(
            storeProduct = product,
            onError = { error, userCancelled ->
                logger.e { "Error purchasing product $product: $error, UserCancelled: $userCancelled" }
                onError(error, userCancelled)
            },
            onSuccess = { storeTransaction, customerInfo ->
                logger.d { "Purchase successful: $storeTransaction, CustomerInfo: $customerInfo" }
                onSuccess(storeTransaction, customerInfo)
            }
        )
    }

    override fun purchasePackage(
        packageToPurchase: Package,
        onError: (PurchasesError, Boolean) -> Unit,
        onSuccess: (StoreTransaction, CustomerInfo) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun restorePurchases(
        onError: (PurchasesError) -> Unit,
        onSuccess: (CustomerInfo) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}