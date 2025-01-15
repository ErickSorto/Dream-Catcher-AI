package org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ObserverLogoutDeleteState(
    isLoggedIn: Boolean,
    isAnonymous: Boolean,
    navigateToLoginScreen: () -> Unit,
) {

    LaunchedEffect(isLoggedIn) {

        if (!isLoggedIn && !isAnonymous) {
            navigateToLoginScreen()
        }
    }

}