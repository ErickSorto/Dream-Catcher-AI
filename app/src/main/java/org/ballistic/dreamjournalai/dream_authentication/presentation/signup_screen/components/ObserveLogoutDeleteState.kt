package org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components

import android.util.Log
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
            Log.d("ObserverLogoutDeleteState", "User is not logged in, navigating to login screen")
            navigateToLoginScreen()
        }
    }

}