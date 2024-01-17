package org.ballistic.dreamjournalai.dream_store.presentation.store_screen

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ballistic.dreamjournalai.dream_store.domain.repository.BillingRepository
import org.ballistic.dreamjournalai.user_authentication.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class StoreScreenViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _storeScreenViewModelState = MutableStateFlow(
        StoreScreenViewModelState(
            authRepository
        )
    )
    val storeScreenViewModelState: StateFlow<StoreScreenViewModelState> = _storeScreenViewModelState

    private val productIds = listOf("dream_token_100", "dream_tokens_500")


    fun onEvent(event: StoreEvent) {
        when (event) {
            is StoreEvent.Buy100DreamTokens -> {
                viewModelScope.launch {
                    purchaseDreamTokens(event.activity, "dream_token_100")
                }
            }

            is StoreEvent.Buy500DreamTokens -> {
                viewModelScope.launch {
                    purchaseDreamTokens(event.activity, "dream_tokens_500")
                }
            }

            is StoreEvent.ToggleLoading -> {
                _storeScreenViewModelState.update {
                    it.copy(isBillingClientLoading = event.isLoading)
                }
            }
        }
    }

    private suspend fun queryProductDetails(): List<ProductDetails> {
        val productList = productIds.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.SkuType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        return withContext(Dispatchers.IO) {
            billingRepository.queryProductDetails(params)
        }
    }

    private suspend fun purchaseDreamTokens(activity: Activity, productId: String) {
        val productDetailsList = queryProductDetails()
        val productDetails = productDetailsList.find { it.productId == productId }

        productDetails?.let {
            billingRepository.initiatePurchaseFlow(activity, it)
        }
        delay(3000)

        _storeScreenViewModelState.update {
            it.copy(isBillingClientLoading = false)
        }
    }
}

data class StoreScreenViewModelState(
    val authRepository: AuthRepository,
    val isBillingClientLoading: Boolean = false,
    val isUserAnonymous: StateFlow<Boolean> = authRepository.isUserAnonymous,
    val dreamTokens: StateFlow<Int> = authRepository.dreamTokens,
)