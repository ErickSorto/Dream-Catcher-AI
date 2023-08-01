package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.events

sealed class SignupEvent {
    data class SignUpWithEmailAndPassword(val email: String, val password: String) : SignupEvent()
    object SendEmailVerification : SignupEvent()
    data class EnteredSignUpEmail(val email: String) : SignupEvent()
    data class EnteredSignUpPassword(val password: String) : SignupEvent()
    object AnonymousSignIn : SignupEvent()
}