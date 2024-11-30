package org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ObserveLoginState(
    isLoggedIn: Boolean,
    isEmailVerified: Boolean,
    isUserAnonymous: Boolean,
    navigateToDreamJournalScreen: () -> Unit,
    isUserAnonymousAlready: Boolean = false
) {
    //login state observer
    Log.d("ObserveLoginState", "isLoggedIn: $isLoggedIn")
    Log.d("ObserveLoginState", "isEmailVerified: $isEmailVerified")
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && isEmailVerified) {
            Log.d("ObserveLoginState", "user is logged in and email is verified now navigating to dream journal screen")
            navigateToDreamJournalScreen()
        }
    }

    //anonymous user observer
    if(!isUserAnonymousAlready) {
        LaunchedEffect(isUserAnonymous) {
            if (isUserAnonymous) {
                Log.d("ObserveLoginState", "user is anonymous now navigating to dream journal screen")
                navigateToDreamJournalScreen()
            }
        }
    }
}