package org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components

import androidx.compose.runtime.Composable
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModelState
import org.ballistic.dreamjournalai.shared.core.Resource

@Composable
fun LogIn(
    loginViewModelState: LoginViewModelState,
    showErrorMessage: (errorMessage: String?) -> Unit,
) {
    when(val signInResponse = loginViewModelState.signInResponse.value) {
        is Resource.Loading<*> -> ProgressBar()
        is Resource.Success<*> -> Unit
        is Resource.Error<*> -> signInResponse.apply {}
    }
}