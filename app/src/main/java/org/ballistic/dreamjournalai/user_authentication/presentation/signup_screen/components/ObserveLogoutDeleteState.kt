package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel.LoginViewModelState

@Composable
fun ObserverLogoutDeleteState(
    loginViewModelState: LoginViewModelState,
    navigateToLoginScreen: () -> Unit,
) {

    Log.d("ObserverLogoutDeleteState", "ObserverLogoutDeleteState: ${loginViewModelState.isLoggedIn}")
    val isLoggedIn = loginViewModelState.isLoggedIn

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn && !loginViewModelState.isUserAnonymous) {
            navigateToLoginScreen()
        }
    }
}