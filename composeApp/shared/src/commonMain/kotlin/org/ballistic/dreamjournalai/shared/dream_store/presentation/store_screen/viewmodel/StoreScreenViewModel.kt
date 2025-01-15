package org.ballistic.dreamjournalai.shared.dream_store.presentation.store_screen.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.kmp.models.StoreProduct
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.functions.functions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.ballistic.dreamjournalai.shared.core.Resource
import org.ballistic.dreamjournalai.shared.dream_authentication.domain.repository.AuthRepository
import org.ballistic.dreamjournalai.shared.dream_store.domain.StoreEvent
import org.ballistic.dreamjournalai.shared.dream_store.domain.repository.BillingRepository

class StoreScreenViewModel(
    private val billingRepository: BillingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _storeScreenViewModelState = MutableStateFlow(StoreScreenViewModelState())
    val storeScreenViewModelState: StateFlow<StoreScreenViewModelState> = _storeScreenViewModelState

    private val productIds = listOf("dream_token_100", "dream_tokens_500")
    private var authStateJob: Job? = null
    private val scope = viewModelScope

    init {
        onEvent(StoreEvent.GetDreamTokens)

        val user = Firebase.auth.currentUser
        _storeScreenViewModelState.update {
            it.copy(isUserAnonymous = user?.isAnonymous == true)
        }

        beginAuthStateListener()
    }

    // region: Auth State Listener
    private fun beginAuthStateListener() {
        if (authStateJob != null) return
        authStateJob = viewModelScope.launch {
            Firebase.auth.authStateChanged.collect { user ->
                _storeScreenViewModelState.update { currentState ->
                    currentState.copy(isUserAnonymous = user?.isAnonymous == true)
                }
            }
        }
    }

    private fun stopAuthStateListener() {
        authStateJob?.cancel()
        authStateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAuthStateListener()
    }
    // endregion

    fun onEvent(event: StoreEvent) {
        when (event) {
            is StoreEvent.Buy100DreamTokens -> {
                viewModelScope.launch {
                    purchaseDreamTokens("dream_token_100")
                }
            }
            is StoreEvent.Buy500DreamTokens -> {
                viewModelScope.launch {
                    purchaseDreamTokens("dream_tokens_500")
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
                                // TODO: Handle error
                            }
                            is Resource.Loading -> {
                                // TODO: Handle loading
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Fetch the desired StoreProduct from RevenueCat, purchase it, then do server verification
     * for each product ID in the transaction (just in case there's more than one).
     */
    private suspend fun purchaseDreamTokens(productId: String) {
        // Mark as loading
        _storeScreenViewModelState.update { it.copy(isBillingClientLoading = true) }

        // 1) Fetch store products from RC
        val storeProducts = fetchStoreProducts()
        val storeProduct = storeProducts.find { it.id == productId }

        if (storeProduct != null) {
            // 2) Purchase
            billingRepository.purchaseProduct(
                product = storeProduct,
                onError = { error, userCancelled ->
                    _storeScreenViewModelState.update {
                        it.copy(isBillingClientLoading = false)
                    }
                    // handle error or cancellation
                },
                onSuccess = { storeTransaction, customerInfo ->
                    // Possibly multiple productIds in a single transaction
                    scope.launch(Dispatchers.IO) {
                        var anyValid = false

                        storeTransaction.productIds.forEach { purchasedId ->
                            val verified = verifyPurchaseOnServer(
                                productId = purchasedId,
                                transactionId = storeTransaction.transactionId.orEmpty(),
                                purchaseTime = storeTransaction.purchaseTime
                            )
                            if (verified) {
                                anyValid = true
                            }
                        }

                        withContext(Dispatchers.Main) {
                            _storeScreenViewModelState.update {
                                it.copy(isBillingClientLoading = false)
                            }
                            // If you want to handle "all valid" vs. "some invalid", you can adjust logic
                            // e.g. show success if `anyValid`, otherwise show error
                        }
                    }
                }
            )
        } else {
            // Product not found
            _storeScreenViewModelState.update {
                it.copy(isBillingClientLoading = false)
            }
        }
    }

    private suspend fun fetchStoreProducts(): List<StoreProduct> =
        suspendCancellableCoroutine { cont ->
            billingRepository.fetchProducts(
                productIds = productIds,
                onError = {
                    cont.resume(emptyList()) {}
                },
                onSuccess = { storeProducts ->
                    cont.resume(storeProducts) {}
                }
            )
        }

    /**
     * Example server verification method that accepts productId, transactionId, purchaseTime.
     * If you only expect single-product transactions, you can keep it simple,
     * but this shows how to handle multiple IDs if needed.
     */
    private suspend fun verifyPurchaseOnServer(
        productId: String,
        transactionId: String,
        purchaseTime: Long
    ): Boolean {
        val userId = Firebase.auth.currentUser?.uid ?: return false

        // Award tokens based on productId
        val tokensToAward = when (productId) {
            "dream_token_100" -> 100
            "dream_tokens_500" -> 500
            else -> 0
        }
        if (tokensToAward == 0) return false

        val data = hashMapOf(
            "purchaseToken" to transactionId,  // or "purchaseToken"
            "purchaseTime" to purchaseTime,
            "orderId" to transactionId,
            "userId" to userId,
            "dreamTokens" to tokensToAward
        )

        val response = Firebase.functions
            .httpsCallable("handlePurchaseVerification")
            .invoke(data)

        return (response.data() as? Map<*, *>)?.get("success") as? Boolean == true
    }
}


data class StoreScreenViewModelState(
    val isBillingClientLoading: Boolean = false,
    val isUserAnonymous: Boolean = false,
    val dreamTokens: Int = 0
)