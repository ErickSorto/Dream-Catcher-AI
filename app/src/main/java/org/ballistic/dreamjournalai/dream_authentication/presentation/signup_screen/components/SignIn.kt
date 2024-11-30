package org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModelState

@Composable
fun LogIn(
    loginViewModelState: LoginViewModelState,
    showErrorMessage: (errorMessage: String?) -> Unit,
) {
    when(val signInResponse = loginViewModelState.signInResponse.value) {
        is Resource.Loading -> ProgressBar()
        is Resource.Success -> Unit
        is Resource.Error -> signInResponse.apply {
            LaunchedEffect(Unit) {
                print("SignIn: $message")
                showErrorMessage(message)
            }
        }
    }
}