package org.ballistic.dreamjournalai.feature_dream.presentation.account_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.feature_dream.presentation.account_settings.components.DreamAccountSettingsScreenTopBar
import org.ballistic.dreamjournalai.feature_dream.presentation.account_settings.components.LogoutDeleteLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components.GoogleSignInHandler
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components.ObserveLoginState
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components.ObserverLogoutDeleteState
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components.SignupLoginLayout
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.events.LoginEvent
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.events.SignupEvent
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel.LoginViewModelState
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel.SignupViewModelState

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
    val animationDisplay = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onLoginEvent(LoginEvent.UserAccountStatus)
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
        modifier = Modifier.padding()
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
                loginViewModelState = loginViewModelState,
                signupViewModelState = signupViewModelState,
                isUserAnonymousAlready = true,
                navigateToDreamJournalScreen = navigateToDreamJournalScreen
            )

            ObserverLogoutDeleteState(loginViewModelState = loginViewModelState) {
                navigateToOnboardingScreen()
            }

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
                            color = colorResource(id = R.color.dark_blue).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                ) {
                    Text(
                        text = "Log in to save your dreams! \n" +
                                "You are currently using a guest account.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }

                SignupLoginLayout(
                    loginViewModelState = loginViewModelState,
                    signupViewModelState = signupViewModelState,
                    onLoginEvent = { onLoginEvent(it) },
                    onSignupEvent = { onSignupEvent(it) },
                    onAnimationComplete = {
                         animationDisplay.value = true                    },
                )

                if(
                   animationDisplay.value
                ){
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(
                                color = colorResource(id = R.color.RedOrange).copy(alpha = 0.5f),
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

                GoogleSignInHandler(
                    loginViewModelState = loginViewModelState,
                    onLoginEvent = { onLoginEvent(it) }
                )
            }
        }
    }
}