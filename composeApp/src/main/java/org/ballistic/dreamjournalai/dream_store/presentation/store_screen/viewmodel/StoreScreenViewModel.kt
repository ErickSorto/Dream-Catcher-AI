package org.ballistic.dreamjournalai.dream_store.presentation.store_screen.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.dream_store.domain.repository.BillingRepository
import org.ballistic.dreamjournalai.dream_store.domain.StoreEvent
import org.ballistic.dreamjournalai.dream_authentication.domain.repository.AuthRepository

class StoreScreenViewModel(
    private val billingRepository: BillingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _storeScreenViewModelState = MutableStateFlow(StoreScreenViewModelState())
    val storeScreenViewModelState: StateFlow<StoreScreenViewModelState> = _storeScreenViewModelState

    private val productIds = listOf("dream_token_100", "dream_tokens_500")

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        Log.d("LoginViewModel", "AuthStateListener called, user: $user")
        _storeScreenViewModelState.update {
            it.copy(
                isUserAnonymous = user?.isAnonymous == true,
            )
        }
    }

    init {
        val user = FirebaseAuth.getInstance().currentUser
        onEvent(StoreEvent.GetDreamTokens)
        _storeScreenViewModelState.update {
            it.copy(
                isUserAnonymous = user?.isAnonymous == true,
            )
        }
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

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

            is StoreEvent.GetDreamTokens -> {
                viewModelScope.launch {
                    authRepository.addDreamTokensFlowListener().collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                _storeScreenViewModelState.update {
                                    it.copy(dreamTokens = resource.data?.toInt() ?: 0)
                                }
                            }

                            is Resource.Error -> {
                                //TODO: Handle error
                            }
                            is Resource.Loading -> {
                                //TODO: Handle loading
                            }
                        }
                    }
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

    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
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
    val isBillingClientLoading: Boolean = false,
    val isUserAnonymous: Boolean = false,
    val dreamTokens: Int = 0
)