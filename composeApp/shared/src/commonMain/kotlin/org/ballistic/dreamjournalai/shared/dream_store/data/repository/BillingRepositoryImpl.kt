package org.ballistic.dreamjournalai.shared.dream_store.data.repository

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesDelegate
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Offerings
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PurchasesError
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.models.StoreTransaction
import org.ballistic.dreamjournalai.shared.dream_store.domain.repository.BillingRepository


class RevenueCatBillingRepositoryImpl: BillingRepository {
    init {
        // If you want to do something whenever purchases update (e.g., award tokens),
        // set a delegate:
        Purchases.sharedInstance.delegate = object : PurchasesDelegate {
            override fun onCustomerInfoUpdated(customerInfo: CustomerInfo) {
                // Called whenever entitlements or purchases change
            }

            override fun onPurchasePromoProduct(
                product: StoreProduct,
                startPurchase: ((PurchasesError, Boolean) -> Unit, (StoreTransaction, CustomerInfo) -> Unit) -> Unit
            ) {
                // Typically not needed unless you handle iOS promotional in-app purchases
            }
        }
    }

    override fun fetchOfferings(
        onError: (PurchasesError) -> Unit,
        onSuccess: (Offerings) -> Unit
    ) {
        Purchases.sharedInstance.getOfferings(
            onError = { error ->
                onError(error)
            },
            onSuccess = { offerings ->
                onSuccess(offerings)
            }
        )
    }

    override fun fetchProducts(
        productIds: List<String>,
        onError: (PurchasesError) -> Unit,
        onSuccess: (List<StoreProduct>) -> Unit
    ) {
        Purchases.sharedInstance.getProducts(
            productIds,
            onError = { error ->
                onError(error)
            },
            onSuccess = { storeProducts ->
                onSuccess(storeProducts)
            }
        )
    }

    override fun purchaseProduct(
        product: StoreProduct,
        onError: (PurchasesError, userCancelled: Boolean) -> Unit,
        onSuccess: (StoreTransaction, CustomerInfo) -> Unit
    ) {
        Purchases.sharedInstance.purchase(
            storeProduct = product,
            onError = { error, userCancelled ->
                onError(error, userCancelled)
            },
            onSuccess = { storeTransaction, customerInfo ->
                onSuccess(storeTransaction, customerInfo)
            }
        )
    }

    override fun purchasePackage(
        packageToPurchase: Package,
        onError: (PurchasesError, userCancelled: Boolean) -> Unit,
        onSuccess: (StoreTransaction, CustomerInfo) -> Unit
    ) {
        Purchases.sharedInstance.purchase(
            packageToPurchase,
            onError = { error, userCancelled ->
                onError(error, userCancelled)
            },
            onSuccess = { storeTransaction, customerInfo ->
                onSuccess(storeTransaction, customerInfo)
            }
        )
    }

    override fun restorePurchases(
        onError: (PurchasesError) -> Unit,
        onSuccess: (CustomerInfo) -> Unit
    ) {
        Purchases.sharedInstance.restorePurchases(
            onError = { error ->
                onError(error)
            },
            onSuccess = { customerInfo ->
                onSuccess(customerInfo)
            }
        )
    }
}