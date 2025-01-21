package org.ballistic.dreamjournalai.shared.dream_onboarding.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.blue_lighthouse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.core.components.TypewriterText
import org.ballistic.dreamjournalai.shared.dream_account.MyGoogleSignInButton
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components.AnonymousButton
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components.ObserveLoginState
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components.SignupLoginLayout
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.events.LoginEvent
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.events.SignupEvent
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModelState
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.viewmodel.SignupViewModelState
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.jetbrains.compose.resources.painterResource
import kotlin.uuid.ExperimentalUuidApi


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
        Image(
            painter = painterResource(Res.drawable.blue_lighthouse),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = "Dream Journal AI",
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
                        color = LightBlack.copy(alpha = 0.7f),
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

            MyGoogleSignInButton(
                onGotToken = { googleIdToken ->
                    // 1) Build dev.gitlive credential
                    val googleCredential = GoogleAuthProvider.credential(
                        idToken = googleIdToken,
                        accessToken = null
                    )
                    // 2) Call your existing KMM logic
                    onLoginEvent(LoginEvent.SignInWithGoogle(googleCredential))
                },
                onError = { errorMsg ->
                    // Show a snackbar, set isLoading=false, etc.
                    onLoginEvent(LoginEvent.ToggleLoading(false))
                    println("Google sign-in error: $errorMsg")
                },
                isLoading = !isLoading
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
