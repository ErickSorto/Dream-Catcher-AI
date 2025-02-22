package org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.ic_google_logo
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.SkyBlue
import org.ballistic.dreamjournalai.shared.core.Constants.SIGN_IN_WITH_GOOGLE
import org.jetbrains.compose.resources.painterResource

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
                    painter = painterResource(
                        Res.drawable.ic_google_logo
                    ),
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
