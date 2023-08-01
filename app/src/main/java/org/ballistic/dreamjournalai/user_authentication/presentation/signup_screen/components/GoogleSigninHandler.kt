package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.events.LoginEvent
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel.LoginViewModelState

@Composable
fun GoogleSignInHandler(
    loginViewModelState: LoginViewModelState,
    onLoginEvent: (LoginEvent) -> Unit
) {
    val scope = rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credentials =
                        loginViewModelState.oneTapClient?.getSignInCredentialFromIntent(result.data)
                    val googleIdToken = credentials?.googleIdToken
                    val googleCredentials =
                        GoogleAuthProvider.getCredential(googleIdToken, null)
                    scope.launch {
                        onLoginEvent(LoginEvent.SignInWithGoogle(googleCredentials))
                    }
                } catch (it: ApiException) {
                    print(it)
                }
            }
        }

    fun launch(signInResult: BeginSignInResult) {
        val intent =
            IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }

    OneTapSignIn(launch = {
        launch(it)
    }, loginViewModelState = loginViewModelState)
}