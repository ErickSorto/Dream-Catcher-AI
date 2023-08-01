package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel.LoginViewModelState
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel.SignupViewModelState

@Composable
fun ObserveLoginState(
    loginViewModelState: LoginViewModelState,
    signupViewModelState: SignupViewModelState,
    navigateToDreamJournalScreen: () -> Unit,
    isUserAnonymousAlready: Boolean = false
) {
    val isLoggedIn = loginViewModelState.isLoggedIn
    val isEmailVerified = loginViewModelState.isEmailVerified

    val isUserAnonymous = signupViewModelState.isUserAnonymous
    val isEmailVerified2 = signupViewModelState.isEmailVerified
    val isLoggedIn2 = signupViewModelState.isLoggedIn

    //login state observer
    LaunchedEffect((isLoggedIn && isEmailVerified) || isUserAnonymous) {
        if ((isLoggedIn &&
                    isEmailVerified) || isUserAnonymous) {
            navigateToDreamJournalScreen()
        }
    }

    //anonymous user observer
    if(!isUserAnonymousAlready) {
        LaunchedEffect(loginViewModelState.isUserAnonymous) {
            if (loginViewModelState.isUserAnonymous) {
                navigateToDreamJournalScreen()
            }
        }
    }


    //signup state observer
    LaunchedEffect(isEmailVerified2, isLoggedIn2) {
        if (isLoggedIn2 && isEmailVerified2) {
            navigateToDreamJournalScreen()
        }
    }
}