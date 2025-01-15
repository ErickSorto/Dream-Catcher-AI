package org.ballistic.dreamjournalai.shared.core.data
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.revenuecat.purchases.kmp.PurchasesDelegate
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.PurchasesError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object RevenueCatManager {

    private val _customerInfoFlow = MutableStateFlow<CustomerInfo?>(null)
    val customerInfoFlow = _customerInfoFlow.asStateFlow()

    fun initialize(apiKey: String, appUserId: String? = null) {
        val configuration = PurchasesConfiguration.Builder(
            apiKey = apiKey
        )
            .apiKey(apiKey)
            .build()

        Purchases.configure(configuration)

        // Set up a delegate to listen for customer info updates
        Purchases.sharedInstance.delegate = object : PurchasesDelegate {
            override fun onCustomerInfoUpdated(customerInfo: CustomerInfo) {
                _customerInfoFlow.value = customerInfo
                // Handle any additional logic when customer info updates
            }

            override fun onPurchasesUpdated(customerInfo: CustomerInfo, error: PurchasesError?) {
                if (error == null) {
                    _customerInfoFlow.value = customerInfo
                    // Handle successful purchase
                } else {
                    // Handle purchase error
                }
            }
        }
    }

    suspend fun getCustomerInfo(): CustomerInfo? {
        return Purchases.sharedInstance.customerInfo.await()
    }

    suspend fun logIn(appUserId: String): CustomerInfo? {
        return Purchases.sharedInstance.logIn(appUserId).await().customerInfo
    }

    suspend fun logOut(): CustomerInfo? {
        return Purchases.sharedInstance.logOut().await().customerInfo
    }

    suspend fun restorePurchases() {
        Purchases.sharedInstance.restorePurchases().await()
    }
}