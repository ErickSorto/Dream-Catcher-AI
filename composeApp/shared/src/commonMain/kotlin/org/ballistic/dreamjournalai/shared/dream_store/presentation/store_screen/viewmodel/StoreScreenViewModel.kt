package org.ballistic.dreamjournalai.shared.dream_store.presentation.store_screen.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.models.StoreTransaction
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.functions.functions
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.Serializable
import org.ballistic.dreamjournalai.shared.core.Resource
import org.ballistic.dreamjournalai.shared.dream_authentication.domain.repository.AuthRepository
import org.ballistic.dreamjournalai.shared.dream_store.domain.StoreEvent
import org.ballistic.dreamjournalai.shared.dream_store.domain.repository.BillingRepository

private val logger = Logger.withTag("StoreScreenViewModel")

class StoreScreenViewModel(
    private val billingRepository: BillingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {



    private val _storeScreenViewModelState = MutableStateFlow(StoreScreenViewModelState())
    val storeScreenViewModelState: StateFlow<StoreScreenViewModelState> = _storeScreenViewModelState

    private val productIds = listOf("dream_token_100", "dream_tokens_500")
    private var authStateJob: Job? = null

    init {
        logger.d { "Initializing StoreScreenViewModel" }

        onEvent(StoreEvent.GetDreamTokens)

        val user = Firebase.auth.currentUser
        _storeScreenViewModelState.update {
            it.copy(isUserAnonymous = user?.isAnonymous == true)
        }

        beginAuthStateListener()
    }

    private fun beginAuthStateListener() {
        if (authStateJob != null) return
        authStateJob = viewModelScope.launch {
            logger.d { "Starting auth state listener" }
            Firebase.auth.authStateChanged.collect { user ->
                logger.d { "Auth state changed: isAnonymous=${user?.isAnonymous}" }
                _storeScreenViewModelState.update { currentState ->
                    currentState.copy(isUserAnonymous = user?.isAnonymous == true)
                }
            }
        }
    }

    private fun stopAuthStateListener() {
        logger.d { "Stopping auth state listener" }
        authStateJob?.cancel()
        authStateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAuthStateListener()
    }

    fun onEvent(event: StoreEvent) {
        logger.d { "Event received: $event" }
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
                    logger.d { "Fetching dream tokens..." }
                    authRepository.addDreamTokensFlowListener().collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                logger.d { "Dream tokens updated: ${resource.data}" }
                                _storeScreenViewModelState.update {
                                    it.copy(dreamTokens = resource.data?.toInt() ?: 0)
                                }
                            }
                            is Resource.Error -> {
                                logger.e { "Error fetching dream tokens: ${resource.message}" }
                            }
                            is Resource.Loading -> {
                                logger.d { "Loading dream tokens..." }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun purchaseDreamTokens(productId: String) {
        logger.d { "Starting purchase flow for productId: $productId" }
        _storeScreenViewModelState.update { it.copy(isBillingClientLoading = true) }

        val storeProducts = fetchStoreProducts()
        val storeProduct = storeProducts.find { it.id == productId }

        if (storeProduct != null) {
            logger.d { "Product found: $storeProduct" }
            billingRepository.purchaseProduct(
                product = storeProduct,
                onError = { error, userCancelled ->
                    logger.e { "Purchase failed for $productId: $error, userCancelled=$userCancelled" }
                    _storeScreenViewModelState.update { it.copy(isBillingClientLoading = false) }
                },
                onSuccess = { storeTransaction, customerInfo ->
                    logger.d { "Purchase successful: Transaction=$storeTransaction" }
                    viewModelScope.launch {
                        handlePurchaseVerification(storeTransaction)
                    }
                }
            )
        } else {
            logger.e { "Product not found for productId: $productId" }
            _storeScreenViewModelState.update { it.copy(isBillingClientLoading = false) }
        }
    }

    private suspend fun handlePurchaseVerification(storeTransaction: StoreTransaction) {
        logger.d { "Verifying single-item purchase." }

        // 1) Get the first (and only) product ID
        val productId = storeTransaction.productIds.firstOrNull()
        if (productId == null) {
            logger.e { "No productId found in storeTransaction!" }
            _storeScreenViewModelState.update { it.copy(isBillingClientLoading = false) }
            return
        }

        // 2) Determine how many tokens to award for this product
        val tokensToAward = when (productId) {
            "dream_token_100"  -> 100
            "dream_tokens_500" -> 500
            else -> {
                logger.e { "Invalid productId: $productId" }
                _storeScreenViewModelState.update { it.copy(isBillingClientLoading = false) }
                return
            }
        }

        // 3) Verify the purchase on your server
        val verified = verifyPurchaseOnServer(
            productId = productId,
            transactionId = storeTransaction.transactionId.orEmpty(),
            purchaseTime = storeTransaction.purchaseTime
        )

        // 4) If verified, award tokens
        if (verified) {
            logger.d { "Purchase verified for $productId, awarding $tokensToAward tokens" }
            _storeScreenViewModelState.update { state ->
                state.copy(dreamTokens = state.dreamTokens + tokensToAward)
            }
        } else {
            logger.e { "Purchase verification failed for $productId" }
        }

        // 5) Stop loading indicator
        _storeScreenViewModelState.update { it.copy(isBillingClientLoading = false) }
    }


    private suspend fun fetchStoreProducts(): List<StoreProduct> =
        suspendCancellableCoroutine { cont ->
            logger.d { "Fetching store products..." }
            billingRepository.fetchProducts(
                productIds = productIds,
                onError = { error ->
                    logger.e { "Error fetching products: $error" }
                    cont.resume(emptyList()) {}
                },
                onSuccess = { storeProducts ->
                    logger.d { "Products fetched successfully: $storeProducts" }
                    cont.resume(storeProducts) { cause, _, _ -> }
                }
            )
        }

    private suspend fun verifyPurchaseOnServer(
        productId: String,
        transactionId: String,
        purchaseTime: Long
    ): Boolean {
        val userId = Firebase.auth.currentUser?.uid ?: return false

        // Determine tokens for this one product
        val tokensToAward = when (productId) {
            "dream_token_100"  -> 100
            "dream_tokens_500" -> 500
            else -> 0
        }
        if (tokensToAward == 0) {
            logger.e { "Invalid productId: $productId" }
            return false
        }

        val data = hashMapOf(
            "purchaseToken" to transactionId,
            "purchaseTime" to purchaseTime,
            "orderId" to transactionId,
            "userId" to userId,
            "dreamTokens" to tokensToAward
        )

        return try {
            val response = Firebase.functions
                .httpsCallable("handlePurchaseVerification")
                .invoke(data)

            // Deserialize the response
            val verificationResponse = response.data<VerificationResponse>()
            logger.d {
                "Server verification response: success=${verificationResponse.success}, " +
                        "tokens=$tokensToAward"
            }
            verificationResponse.success
        } catch (e: Exception) {
            logger.e { "Server verification failed: $e" }
            false
        }
    }


}


data class StoreScreenViewModelState(
    val isBillingClientLoading: Boolean = false,
    val isUserAnonymous: Boolean = false,
    val dreamTokens: Int = 0
)

@Serializable
data class VerificationResponse(
    val success: Boolean
)