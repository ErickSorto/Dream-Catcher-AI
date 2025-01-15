package org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun ObserveLoginState(
    isLoggedIn: Boolean,
    isEmailVerified: Boolean,
    isUserAnonymous: Boolean,
    navigateToDreamJournalScreen: () -> Unit,
    isUserAnonymousAlready: Boolean = false
) {
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && isEmailVerified) {
            delay(250)
            navigateToDreamJournalScreen()
        }
    }

    //anonymous user observer
    if(!isUserAnonymousAlready) {
        LaunchedEffect(isUserAnonymous) {
            if (isUserAnonymous) {
                navigateToDreamJournalScreen()
            }
        }
    }
}