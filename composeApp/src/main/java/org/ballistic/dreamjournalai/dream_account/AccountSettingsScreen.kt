package org.ballistic.dreamjournalai.dream_account

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.dream_account.components.DreamAccountSettingsScreenTopBar
import org.ballistic.dreamjournalai.dream_account.components.LogoutDeleteLayout
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components.AnonymousButton
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components.ObserveLoginState
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components.ObserverLogoutDeleteState
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components.SignInGoogleButton
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components.SignupLoginLayout
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.events.LoginEvent
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.events.SignupEvent
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModelState
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.SignupViewModelState
import org.ballistic.dreamjournalai.dream_main.presentation.viewmodel.MainScreenViewModelState
import java.security.MessageDigest //TODO: Expect/Actual
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
@Composable
fun AccountSettingsScreen(
    loginViewModelState: LoginViewModelState,
    signupViewModelState: SignupViewModelState,
    mainScreenViewModelState: MainScreenViewModelState,
    onLoginEvent: (LoginEvent) -> Unit = {},
    onSignupEvent: (SignupEvent) -> Unit = {},
    navigateToOnboardingScreen: () -> Unit = {},
    navigateToDreamJournalScreen: () -> Unit = {}
) {
    val isLoading = loginViewModelState.isLoading
    val isUserAnonymous = loginViewModelState.isUserAnonymous
    val isEmailVerified = loginViewModelState.isEmailVerified
    val isUserLoggedIn = loginViewModelState.isLoggedIn
    val animationDisplay = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val onClick: () -> Unit = {
        scope.launch {
            val credentialManager = CredentialManager.create(context)
            val rawNonce = Uuid.random().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .setNonce(hashedNonce)  // Use the generated nonce
                .build()


            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption).build()

            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )

                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken
                onLoginEvent(
                    LoginEvent.SignInWithGoogle(
                        GoogleAuthProvider.getCredential(
                            googleIdToken,
                            null
                        )
                    )
                )
                onLoginEvent(LoginEvent.ToggleLoading(false))
                navigateToDreamJournalScreen()
            } catch (e: GoogleIdTokenParsingException) {
                // Specific exception from parsing the Google ID token
                Log.d("AccountSettingsScreen", "GoogleIdTokenParsingException: ${e.message}")
            } catch (e: GetCredentialCancellationException) {
                // Specific exception when the user cancels the sign-in process
                Log.d(
                    "AccountSettingsScreen",
                    "GetCredentialCancellationException: Sign-in cancelled by the user."
                )
                // Optionally, you could also invoke a cancellation event or update UI here
                // onLoginEvent(LoginEvent.SignInCancelled)
                onLoginEvent(LoginEvent.ToggleLoading(false))
            } catch (e: Exception) {
                // A general exception catch, if you need to ensure no crash for any other exception
                Log.e("AccountSettingsScreen", "Exception: An unexpected error occurred.", e)
                onLoginEvent(LoginEvent.ToggleLoading(false))
            }
        }
    }

    Scaffold(
        topBar = {
            DreamAccountSettingsScreenTopBar(mainScreenViewModelState = mainScreenViewModelState)
        },
        snackbarHost = {
            SnackbarHost(hostState = signupViewModelState.snackBarHostState.value)
            SnackbarHost(hostState = loginViewModelState.snackBarHostState.value)
        },
        containerColor = Color.Transparent,
        modifier = Modifier
            .navigationBarsPadding()
            .padding(bottom = 64.dp)
    ) {
        if (loginViewModelState.isEmailVerified &&
            loginViewModelState.isLoggedIn &&
            !loginViewModelState.isUserAnonymous
        ) {
            LogoutDeleteLayout(
                loginViewModelState = loginViewModelState,
                onLoginEvent = onLoginEvent
            )
        } else {
            ObserveLoginState(
                isLoggedIn = isUserLoggedIn,
                isEmailVerified = isEmailVerified,
                isUserAnonymous = isUserAnonymous,
                isUserAnonymousAlready = true,
                navigateToDreamJournalScreen = navigateToDreamJournalScreen,
            )

            ObserverLogoutDeleteState(
                isLoggedIn = isUserLoggedIn,
                isAnonymous = isUserAnonymous,
                navigateToLoginScreen = navigateToOnboardingScreen
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(it)
                    .navigationBarsPadding()
                    .fillMaxSize()
                    .padding(),
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(
                            color = colorResource(id = R.color.light_black).copy(alpha = 0.7f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                ) {
                    Text(
                        text = "Log in to save your dreams! \n" +
                                "You are currently using a guest account.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                    )
                }

                SignupLoginLayout(
                    loginViewModelState = loginViewModelState,
                    signupViewModelState = signupViewModelState,
                    onLoginEvent = { onLoginEvent(it) },
                    onSignupEvent = { onSignupEvent(it) },
                    onAnimationComplete = {
                        animationDisplay.value = true
                    },
                )

                SignInGoogleButton(
                    onClick = {
                        onLoginEvent(LoginEvent.ToggleLoading(true))
                        onClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 32.dp, start = 16.dp, end = 16.dp),
                    isVisible = true,
                    isEnabled = !isLoading
                )

                if (!isUserAnonymous) {
                    AnonymousButton(
                        modifier = Modifier
                            .padding(bottom = 8.dp, top = 8.dp, start = 16.dp, end = 16.dp),
                        isVisible = true,
                        onClick = {
                            onLoginEvent(LoginEvent.ToggleLoading(true))
                            onSignupEvent(SignupEvent.AnonymousSignIn)
                        },
                        isEnabled = !isLoading
                    )
                }

                if (
                    animationDisplay.value
                ) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(
                                color = colorResource(id = R.color.RedOrange).copy(alpha = 0.8f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        TypewriterText(
                            text = "Warning: Guest accounts are deleted after 30 days of inactivity.",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}