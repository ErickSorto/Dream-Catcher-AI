package org.ballistic.dreamjournalai.dream_onboarding.presentation

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components.AnonymousButton
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components.ObserveLoginState
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components.SignInGoogleButton
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components.SignupLoginLayout
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.events.LoginEvent
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.events.SignupEvent
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModelState
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.SignupViewModelState
import java.security.MessageDigest //TODO: Find alternative to MessageDigest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
@ExperimentalAnimationApi
@Composable
fun OnboardingScreen(
    loginViewModelState: LoginViewModelState,
    signupViewModelState: SignupViewModelState,
    navigateToDreamJournalScreen: () -> Unit,
    onLoginEvent: (LoginEvent) -> Unit,
    onSignupEvent: (SignupEvent) -> Unit,
    onDataLoaded: () -> Unit,
) {
    val isUserAnonymous = loginViewModelState.isUserAnonymous
    val isUserLoggedIn = loginViewModelState.isLoggedIn
    val isEmailVerified = loginViewModelState.isEmailVerified
    val showLoginLayout = remember { mutableStateOf(false) }
    val isSplashScreenClosed = remember { mutableStateOf(false) }
    val titleText = remember { mutableStateOf("Welcome Dreamer!") }
    val visible = remember { mutableStateOf(true) }
    val transition = updateTransition(visible.value, label = "")
    val showSubheader = remember { mutableStateOf(false) }
    val isLoading = loginViewModelState.isLoading
    val scope = CoroutineScope(Dispatchers.Main)
    val context = LocalContext.current

    val onClick: () -> Unit = {
        scope.launch {
            try {
                val credentialManager = androidx.credentials.CredentialManager.create(context)
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
            } catch (e: GoogleIdTokenParsingException) {
                // Specific exception from parsing the Google ID token
                Log.d("AccountSettingsScreen", "GoogleIdTokenParsingException: ${e.message}")
                onLoginEvent(LoginEvent.ToggleLoading(false))
            } catch (e: GetCredentialCancellationException) {
                // Specific exception when the user cancels the sign-in process
                Log.d(
                    "AccountSettingsScreen",
                    "GetCredentialCancellationException: Sign-in cancelled by the user."
                )
                // Optionally, you could also invoke a cancellation event or update UI here
                onLoginEvent(LoginEvent.ToggleLoading(false))
            } catch (e: Exception) {
                // A general exception catch, if you need to ensure no crash for any other exception
                Log.e("AccountSettingsScreen", "Exception: An unexpected error occurred.", e)
                onLoginEvent(LoginEvent.ToggleLoading(false))
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(1000)
        onDataLoaded()
        isSplashScreenClosed.value = true
    }

    LaunchedEffect(Unit) {
        onLoginEvent(LoginEvent.BeginAuthStateListener)
    }


    ObserveLoginState(
        isLoggedIn = isUserLoggedIn,
        isEmailVerified = isEmailVerified,
        isUserAnonymous = isUserAnonymous,
        navigateToDreamJournalScreen = navigateToDreamJournalScreen,
    )

    Box {
        CoilImage(
            imageModel = {R.drawable.blue_lighthouse},
            modifier = Modifier.fillMaxSize(),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
            )
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = signupViewModelState.snackBarHostState.value)
            SnackbarHost(hostState = loginViewModelState.snackBarHostState.value)
        },
        containerColor = Color.Transparent
    ) { it ->

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 64.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .background(
                        color = colorResource(id = R.color.light_black).copy(alpha = 0.7f),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                //invisible text font 32.sp and 16 padding filler
                Text(
                    text = "Dream Journal AI",
                    modifier = Modifier.padding(16.dp),
                    style = TextStyle(
                        color = Color.Transparent,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                if (isSplashScreenClosed.value){
                    TypewriterText(
                        text = if (visible.value) titleText.value else "Dream Journal AI",
                        modifier = Modifier.padding(16.dp),
                        style = TextStyle(
                            color = transition.animateColor(label = "") { if (it) Color.White else Color.Transparent }.value,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        animationDuration = 5000,
                        onAnimationComplete = {
                            scope.launch {
                                if (!showLoginLayout.value) {
                                    loginViewModelState.isLoginLayout.value = true
                                }
                                showLoginLayout.value = true
                                delay(1000)  // Delay for 1 second
                                visible.value = !visible.value
                                if (visible.value) {
                                    titleText.value = "Dream Journal AI"
                                    showSubheader.value = true
                                }
                            }
                        }
                    )
                }

            }
            Spacer(modifier = Modifier.weight(1f))
            if (showLoginLayout.value) {
                SignupLoginLayout(
                    loginViewModelState = loginViewModelState,
                    signupViewModelState = signupViewModelState,
                    onLoginEvent = { onLoginEvent(it) },
                    onSignupEvent = { onSignupEvent(it) },
                )
            }

            Spacer(modifier = Modifier.weight(1f))

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
                        onSignupEvent(SignupEvent.AnonymousSignIn)
                    },
                    isEnabled = !isLoading
                )
            }
            Spacer(modifier = Modifier.padding(16.dp))
        }
    }
}
