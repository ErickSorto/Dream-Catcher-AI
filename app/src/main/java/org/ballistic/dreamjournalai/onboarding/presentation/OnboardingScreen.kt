package org.ballistic.dreamjournalai.onboarding.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components.GoogleSignInHandler
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components.ObserveLoginState
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components.SignupLoginLayout
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.events.LoginEvent
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.events.SignupEvent
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel.LoginViewModelState
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel.SignupViewModelState

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun OnboardingScreen(
    loginViewModelState: LoginViewModelState,
    signupViewModelState: SignupViewModelState,
    navigateToDreamJournalScreen: () -> Unit,
    onLoginEvent: (LoginEvent) -> Unit,
    onSignupEvent: (SignupEvent) -> Unit,
    onDataLoaded: () -> Unit,
) {
    val showLoginLayout = remember { mutableStateOf(false) }
    val titleText = remember { mutableStateOf("Welcome Dreamer!") }
    val visible = remember { mutableStateOf(true) }
    val transition = updateTransition(visible.value, label = "")
    val showSubheader = remember { mutableStateOf(false) }

    val scope = CoroutineScope(Dispatchers.Main)

    LaunchedEffect(key1 = Unit) {
        onLoginEvent(LoginEvent.UserAccountStatus)
    }

    LaunchedEffect(key1 = Unit) {
        delay(1500)
        onDataLoaded()
    }

    ObserveLoginState(
        loginViewModelState = loginViewModelState,
        signupViewModelState = signupViewModelState,
        navigateToDreamJournalScreen = navigateToDreamJournalScreen
    )

    Box {
        Image(
            painter = painterResource(R.drawable.blue_lighthouse),
            contentDescription = "background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = signupViewModelState.snackBarHostState.value)
            SnackbarHost(hostState = loginViewModelState.snackBarHostState.value)
        },
        containerColor = Color.Transparent
    ) {
        it
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 64.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .background(
                        color = colorResource(id = R.color.dark_blue).copy(alpha = 0.5f),
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
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
                                if(!showLoginLayout.value) {
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
                    if (showSubheader.value) {
                        TypewriterText(
                            text = "Light the way to your unconscious. Paint, explore," +
                                    " and understand your dreams with the help of AI",
                            modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp),
                        )
                    }
                }
            }

            if(showLoginLayout.value){
                SignupLoginLayout(
                    loginViewModelState = loginViewModelState,
                    signupViewModelState = signupViewModelState,
                    onLoginEvent = { onLoginEvent(it) },
                    onSignupEvent = { onSignupEvent(it) },
                )
            }

            GoogleSignInHandler(
                loginViewModelState = loginViewModelState,
                onLoginEvent = { onLoginEvent(it) }
            )
        }
    }
}
