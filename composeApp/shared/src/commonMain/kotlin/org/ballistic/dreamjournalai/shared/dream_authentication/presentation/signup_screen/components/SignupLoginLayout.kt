package org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.events.LoginEvent
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.events.SignupEvent
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModelState
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.viewmodel.SignupViewModelState

@Composable
fun SignupLoginLayout(
    loginViewModelState: LoginViewModelState,
    signupViewModelState: SignupViewModelState,
    onLoginEvent: (LoginEvent) -> Unit = {},
    onSignupEvent: (SignupEvent) -> Unit = {},
    onAnimationComplete: () -> Unit = {},
) {

    Column(
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 0.dp)
            .navigationBarsPadding(),
    ) {

        SignupLoginTabLayout(loginViewModelState = loginViewModelState)
        when {
            loginViewModelState.isLoginLayout.value -> {
                LoginLayout(
                    loginViewModelState = loginViewModelState,
                    onLoginEvent = {
                        onLoginEvent(it)
                    },
                    onAnimationComplete = onAnimationComplete
                )
            }

            loginViewModelState.isForgotPasswordLayout.value -> {
                ForgotPasswordLayout(
                    loginViewModelState = loginViewModelState,
                ) {
                    onLoginEvent(it)
                }
            }

            loginViewModelState.isSignUpLayout.value -> {
                SignupLayout(
                    signupViewModelState = signupViewModelState,
                    onSignupEvent = {
                        onSignupEvent(it)
                    },
                )
            }
        }
    }
}