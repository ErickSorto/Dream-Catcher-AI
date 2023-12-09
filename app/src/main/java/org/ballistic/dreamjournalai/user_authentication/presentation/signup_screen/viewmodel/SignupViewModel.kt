package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.user_authentication.domain.repository.AuthRepository
import org.ballistic.dreamjournalai.user_authentication.domain.repository.SignUpResponse
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.events.SignupEvent
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignupViewModelState())
    val state: StateFlow<SignupViewModelState> = _state.asStateFlow()

    fun onEvent(event: SignupEvent) = viewModelScope.launch {
        when (event) {
            is SignupEvent.SignUpWithEmailAndPassword -> {
                signUpWithEmailAndPassword(event.email, event.password)
            }

            is SignupEvent.AnonymousSignIn -> {
                anonymousSignIn()
            }

            is SignupEvent.EnteredSignUpEmail -> {
                _state.value = _state.value.copy(
                    signUpEmail = event.email
                )
            }

            is SignupEvent.EnteredSignUpPassword -> {
                _state.value = _state.value.copy(
                    signUpPassword = event.password
                )
            }
        }
    }

    private fun <T> handleResource(
        resourceFlow: Flow<Resource<T>>,
        transform: (T) -> Job,
        errorTransform: (String) -> SignupViewModelState,
        loadingTransform: () -> SignupViewModelState
    ) = resourceFlow.onEach { resource ->
        when (resource) {
            is Resource.Loading -> _state.value = loadingTransform()
            is Resource.Success -> resource.data?.let { transform(it) }
            is Resource.Error -> _state.value = errorTransform(resource.message ?: "Error")
        }
    }.launchIn(viewModelScope)

    private suspend fun signUpWithEmailAndPassword(email: String, password: String) {
        if (!checkSignUpFields()) return
        handleResource(
            resourceFlow = repo.firebaseSignUpWithEmailAndPassword(email, password),
            transform = { result ->
                viewModelScope.launch {
                    _state.value.snackBarHostState.value.showSnackbar(
                        result,
                        duration = SnackbarDuration.Long,
                        actionLabel = "dismiss"
                    )
                }
            },
            errorTransform = { error ->
                _state.value.copy(
                    signUp = MutableStateFlow(
                        SignUpState(
                            error = error
                        )
                    )
                )
            },
            loadingTransform = { _state.value.copy(signUp = MutableStateFlow(SignUpState(isLoading = true))) }
        )
    }

    private suspend fun anonymousSignIn() {
        handleResource(
            resourceFlow = repo.anonymousSignIn(),
            transform = { authResult ->
                viewModelScope.launch {
                    Log.d("SignupViewModel", "anonymousSignIn: $authResult")
                    Log.d("SignupViewModel", "anonymousSignIn: ${repo.currentUser}")
                    Log.d("SignupViewModel", "anonymousSignIn: ${authResult.user?.isAnonymous}")
                    _state.update {
                        it.copy(
                            isUserAnonymous = authResult.user?.isAnonymous ?: false,
                            isLoggedIn = authResult.user != null,
                            isUserExist = authResult.user != null,
                        )
                    }
                }
            },
            errorTransform = { error -> _state.value.copy(login = MutableStateFlow(LoginState(error = error))) },
            loadingTransform = { _state.value.copy(login = MutableStateFlow(LoginState(isLoading = true))) }
        )
    }

    private suspend fun checkSignUpFields(): Boolean {
        _state.value.snackBarHostState.value.currentSnackbarData?.dismiss()
        return when {
            _state.value.signUpEmail.isEmpty() || _state.value.signUpPassword.isEmpty() -> {
                state.value.snackBarHostState.value.showSnackbar(
                    "Email or password is empty",
                    duration = SnackbarDuration.Short,
                    actionLabel = "dismiss"
                )
                false
            }

            !_state.value.signUpEmail.contains("@") || !_state.value.signUpEmail.contains(".") -> {
                state.value.snackBarHostState.value.showSnackbar(
                    "Email is not in correct format",
                    duration = SnackbarDuration.Short,
                    actionLabel = "dismiss"
                )
                false
            }

            _state.value.signUpPassword.length < 6 -> {
                state.value.snackBarHostState.value.showSnackbar(
                    "Password must be at least 6 characters",
                    duration = SnackbarDuration.Short,
                    actionLabel = "dismiss"
                )
                false
            }

            else -> {
                true
            }
        }
    }

    private fun checkUserAccountStatus() = viewModelScope.launch {
        _state.value = _state.value.copy(
            user = repo.currentUser,
            isUserAnonymous = repo.isUserAnonymous.value,
            isLoggedIn = repo.isLoggedIn.value,
            isEmailVerified = repo.isEmailVerified.value,
            isUserExist = repo.isUserExist.value,
        )
    }

}

data class SignupViewModelState(
    val user: StateFlow<FirebaseUser?> = MutableStateFlow(null),
    val loginEmail: String = "",
    val loginPassword: String = "",
    val signUpEmail: String = "",
    val signUpPassword: String = "",
    val forgotPasswordEmail: String = "",
    val oneTapClient: SignInClient? = null,
    val isLoginLayout: MutableState<Boolean> = mutableStateOf(false),
    val isSignUpLayout: MutableState<Boolean> = mutableStateOf(false),
    val isForgotPasswordLayout: MutableState<Boolean> = mutableStateOf(false),
    val signUpResponse: MutableState<Resource<SignUpResponse>> = mutableStateOf(Resource.Success()),
    val sendEmailVerificationResponse: MutableState<Resource<Boolean>> = mutableStateOf(Resource.Success()),
    val login: StateFlow<LoginState> = MutableStateFlow(LoginState()),
    val signUp: StateFlow<SignUpState> = MutableStateFlow(SignUpState()),
    val emailVerification: StateFlow<VerifyEmailState> = MutableStateFlow(VerifyEmailState()),
    val revokeAccess: StateFlow<RevokeAccessState> = MutableStateFlow(RevokeAccessState()),
    val isUserExist: Boolean = false,
    val isEmailVerified: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isUserAnonymous: Boolean = false,
    val snackBarHostState: MutableState<SnackbarHostState> = mutableStateOf(SnackbarHostState()),
)

data class VerifyEmailState(
    val verified: Boolean = false,
    val sent: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = ""
)

data class SignUpState(
    val signUp: String? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)
