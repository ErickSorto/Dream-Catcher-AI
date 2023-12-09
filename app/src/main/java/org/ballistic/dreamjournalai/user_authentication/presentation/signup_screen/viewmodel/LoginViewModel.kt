package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.user_authentication.domain.repository.*
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.events.LoginEvent
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: AuthRepository,
    signInClient: SignInClient
) : ViewModel() {

    private val _state = MutableStateFlow(
        LoginViewModelState(
            oneTapClient = signInClient,
            isLoggedIn = repo.isLoggedIn.value,
            isUserAnonymous = repo.isUserAnonymous.value,
        )
    )
    val state: StateFlow<LoginViewModelState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) = viewModelScope.launch {

        when (event) {
            is LoginEvent.OneTapSignIn -> {
                oneTapSignIn()
            }
            is LoginEvent.SignInWithGoogle -> {
                signInWithGoogle(event.googleCredential)
            }
            is LoginEvent.LoginWithEmailAndPassword -> {
                loginWithEmailAndPassword(event.email, event.password)
            }
            is LoginEvent.SendPasswordResetEmail -> {
                sendPasswordResetEmail(event.email)
            }
            is LoginEvent.EnteredLoginEmail -> {
                _state.update { it.copy(loginEmail = event.email) }
            }
            is LoginEvent.EnteredLoginPassword -> {
                _state.update { it.copy(loginPassword = event.password) }
            }
            is LoginEvent.EnteredForgotPasswordEmail -> {
                _state.update { it.copy(forgotPasswordEmail = event.email) }
            }
            is LoginEvent.ReloadUser -> {
                reloadUser()
            }
            is LoginEvent.SignOut -> {
                signOut()
            }
            is LoginEvent.RevokeAccess -> {
                revokeAccess(event.password)
            }
            is LoginEvent.UserAccountStatus -> {
                checkUserAccountStatus()
            }
        }
    }

    private fun <T> handleResource(
        resourceFlow: Flow<Resource<T>>,
        transform: (T) -> LoginViewModelState,
        errorTransform: (String) -> LoginViewModelState,
        loadingTransform: () -> LoginViewModelState
    ) = resourceFlow.onEach { resource ->
        when (resource) {
            is Resource.Loading -> _state.value = loadingTransform()
            is Resource.Success -> _state.value = resource.data?.let { transform(it) }!!
            is Resource.Error -> _state.value = errorTransform(resource.message ?: "Error")
        }
    }.launchIn(viewModelScope)

    private fun oneTapSignIn() = viewModelScope.launch {
        _state.value.oneTapSignInResponse.value = Resource.Loading()
        val response = repo.oneTapSignInWithGoogle()
        _state.value.oneTapSignInResponse.value = response
    }

    private suspend fun signInWithGoogle(googleCredential: AuthCredential) {
        handleResource(
            resourceFlow = repo.firebaseSignInWithGoogle(googleCredential),
            transform = {

                viewModelScope.launch {
                    repo.transferDreamsFromAnonymousToPermanent(
                        it.first.user?.uid ?: "", it.second?: "")
                    }

                _state.value.copy(
                    isEmailVerified = it.first.user?.isEmailVerified ?: false,
                    isLoggedIn = true,
                    isUserExist = it.first.user != null,
                    signInWithGoogleResponse = MutableStateFlow(Resource.Success(it))
                )
            },
            errorTransform = { error ->
                _state.value.copy(
                    login = MutableStateFlow(LoginState(error = error)),
                    signInWithGoogleResponse = MutableStateFlow(Resource.Error(error))
                )
            },
            loadingTransform = {
                _state.value.copy(
                    login = MutableStateFlow(LoginState(isLoading = true)),
                    signInWithGoogleResponse = MutableStateFlow(Resource.Loading())
                )
            }
        )
    }

    private suspend fun loginWithEmailAndPassword(email: String, password: String) {
        handleResource(
            resourceFlow = repo.firebaseSignInWithEmailAndPassword(email, password),
            transform = {
                _state.value.copy(
                    isLoggedIn = true,
                    isUserExist = true,
                    isEmailVerified = true) },
            errorTransform = { error -> _state.value.copy(login = MutableStateFlow(LoginState(error = error))) },
            loadingTransform = { _state.value.copy(login = MutableStateFlow(LoginState(isLoading = true))) }
        )
    }

    private suspend fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        _state.value =
            _state.value.copy(sendPasswordResetEmailResponse = mutableStateOf(Resource.Loading()))
        _state.value = _state.value.copy(
            sendPasswordResetEmailResponse = mutableStateOf(
                repo.sendPasswordResetEmail(email)
            )
        )
    }

    private suspend fun reloadUser() = viewModelScope.launch {
        _state.value = _state.value.copy(reloadUserResponse = mutableStateOf(Resource.Loading()))
        _state.value =
            _state.value.copy(reloadUserResponse = mutableStateOf(Resource.Success(repo.reloadFirebaseUser())))
    }

    private fun signOut() = viewModelScope.launch {
        repo.signOut()
        Log.d("LoginViewModel", "signOut called")
        _state.value = _state.value.copy(isLoggedIn = false)
        Log.d("LoginViewModel", "signOut completed! logged in:${_state.value.isLoggedIn}")
    }

    private suspend fun revokeAccess(password: String?) {
        handleResource(
            resourceFlow = repo.revokeAccess(password),
            transform = {
                _state.value.copy(
                    revokeAccess = MutableStateFlow(
                        RevokeAccessState(isRevoked = true)
                    ),
                    isLoggedIn = false,
                )
            },
            errorTransform = { error ->
                _state.value.copy(
                    revokeAccess = MutableStateFlow(
                        RevokeAccessState(error = error)
                    )
                )
            },
            loadingTransform = {
                _state.value.copy(
                    revokeAccess = MutableStateFlow(
                        RevokeAccessState(
                            isLoading = true
                        )
                    )
                )
            }
        )
    }

    private fun checkUserAccountStatus() = viewModelScope.launch {
        _state.update { it.copy(
            user = repo.currentUser.value,
            isLoggedIn = repo.isLoggedIn.value,
            isEmailVerified = repo.isEmailVerified.value,
            isUserAnonymous = repo.isUserAnonymous.value,
            isUserExist = repo.isUserExist.value) }
    }
}

data class LoginViewModelState(
    val loginEmail: String = "",
    val loginPassword: String = "",
    val forgotPasswordEmail: String = "",
    val oneTapClient: SignInClient? = null,
    val signInWithGoogleResponse: MutableStateFlow<Resource<Pair<AuthResult, String?>>> = MutableStateFlow(Resource.Loading()),
    val isLoginLayout: MutableState<Boolean> = mutableStateOf(true),
    val isSignUpLayout: MutableState<Boolean> = mutableStateOf(false),
    val isForgotPasswordLayout: MutableState<Boolean> = mutableStateOf(false),
    val oneTapSignInResponse: MutableState<OneTapSignInResponse> = mutableStateOf(Resource.Success()),
    val signInResponse: MutableState<Resource<SignInResponse>> = mutableStateOf(Resource.Success()),
    val sendPasswordResetEmailResponse: MutableState<SendPasswordResetEmailResponse> = mutableStateOf(
        Resource.Success()
    ),
    val reloadUserResponse: MutableState<Resource<ReloadUserResponse>> = mutableStateOf(Resource.Success()),
    val login: StateFlow<LoginState> = MutableStateFlow(LoginState()),
    val revokeAccess: StateFlow<RevokeAccessState> = MutableStateFlow(RevokeAccessState()),
    val user: FirebaseUser? = null,
    val isUserExist: Boolean = false,
    val isEmailVerified:Boolean = false,
    val isLoggedIn: Boolean = false,
    val isUserAnonymous: Boolean = false,
    val snackBarHostState: MutableState<SnackbarHostState> = mutableStateOf(SnackbarHostState()),
)

data class RevokeAccessState(
    val isRevoked: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = ""
)

data class LoginState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = ""
)
