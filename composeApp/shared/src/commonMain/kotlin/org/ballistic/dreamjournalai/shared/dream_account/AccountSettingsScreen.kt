package org.ballistic.dreamjournalai.shared.dream_account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import com.mmk.kmpauth.google.GoogleUser
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.ic_google_logo
import org.ballistic.dreamjournalai.shared.core.Constants.SIGN_IN_WITH_GOOGLE
import org.ballistic.dreamjournalai.shared.core.components.TypewriterText
import org.ballistic.dreamjournalai.shared.dream_account.components.DreamAccountSettingsScreenTopBar
import org.ballistic.dreamjournalai.shared.dream_account.components.LogoutDeleteLayout
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components.AnonymousButton
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components.ObserveLoginState
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components.ObserverLogoutDeleteState
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components.SignupLoginLayout
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.events.LoginEvent
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.events.SignupEvent
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModelState
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.viewmodel.SignupViewModelState
import org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.SkyBlue
import org.jetbrains.compose.resources.painterResource


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
                            color = LightBlack.copy(alpha = 0.7f),
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

                MyGoogleSignInButton(
                    onGotToken = { googleIdToken ->
                        // 1) Build dev.gitlive credential
                        val googleCredential = dev.gitlive.firebase.auth.GoogleAuthProvider.credential(
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
                                color = RedOrange.copy(alpha = 0.8f),
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


@Composable
fun MyGoogleSignInButton(
    onGotToken: (String) -> Unit,
    onError: (String) -> Unit = {},
    isLoading: Boolean
) {
    // This uses the "non-Firebase" KMPAuth container
    GoogleButtonUiContainer(
        onGoogleSignInResult = { googleUser: GoogleUser? ->
            // googleUser will be null if sign-in failed or user cancelled
            if (googleUser != null) {
                val token = googleUser.idToken
                onGotToken(token)
            } else {
                onError("Google sign-in cancelled or failed.")
            }
        }
    ) {
        SignInGoogleButton(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            isVisible = true,
            isEnabled = isLoading,
            onClick = { this.onClick() }
        )
    }
}

@Composable
fun SignInGoogleButton(
    modifier: Modifier,
    isVisible: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(initialOffsetX = { 1000 }),
            exit = slideOutHorizontally { -1000 }
        ) {
            Button(
                modifier = Modifier.padding().fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SkyBlue
                ),
                enabled = isEnabled,
                onClick = onClick
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_google_logo),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = SIGN_IN_WITH_GOOGLE,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }
    }
}