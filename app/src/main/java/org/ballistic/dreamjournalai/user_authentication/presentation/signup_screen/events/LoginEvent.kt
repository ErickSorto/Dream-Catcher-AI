package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.events

import com.google.firebase.auth.AuthCredential

sealed class LoginEvent {
    data class SignInWithGoogle(val googleCredential: AuthCredential) : LoginEvent()
    data class LoginWithEmailAndPassword(val email: String, val password: String) : LoginEvent()
    data class SendPasswordResetEmail(val email: String) : LoginEvent()
    data object ReloadUser : LoginEvent()
    object SignOut : LoginEvent()
    data class RevokeAccess(val password: String?) : LoginEvent()
    data class EnteredLoginEmail(val email: String) : LoginEvent()
    data class EnteredLoginPassword(val password: String) : LoginEvent()
    data class EnteredForgotPasswordEmail(val email: String) : LoginEvent()
    data object ToggleLoading: LoginEvent()

    data object UserAccountStatus : LoginEvent()
}