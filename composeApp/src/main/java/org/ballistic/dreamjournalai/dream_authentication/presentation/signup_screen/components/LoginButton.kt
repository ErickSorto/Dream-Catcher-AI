package org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.Constants
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.events.LoginEvent
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModelState

@Composable
fun LoginButton(
    modifier: Modifier,
    onLoginEvent: (LoginEvent) -> Unit,
    loginViewModelState: LoginViewModelState,
    isVisible: MutableState<Boolean>
) {
    val keyboard = LocalSoftwareKeyboardController.current
    AnimatedVisibility(
        visible = isVisible.value,
        enter = slideInHorizontally(initialOffsetX = { 1000 }),
        exit = slideOutHorizontally { -1000 }
    ) {
        Button(
            modifier = modifier
                .fillMaxWidth(.5f),
            onClick = {
                keyboard?.hide()
                onLoginEvent(
                    LoginEvent.LoginWithEmailAndPassword(
                        loginViewModelState.loginEmail,
                        loginViewModelState.loginPassword
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.lighter_yellow)
            )
        ) {
            Text(
                text = Constants.LOGIN,
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }
}