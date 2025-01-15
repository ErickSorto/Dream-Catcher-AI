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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.anonymous_icon
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.DarkBlue
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.jetbrains.compose.resources.painterResource

@Composable
fun AnonymousButton(
    modifier: Modifier,
    isVisible: Boolean,
    onClick: () -> Unit,
    isEnabled : Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
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
                    containerColor = DarkBlue
                ),
                onClick = onClick,
                enabled = isEnabled
            ) {
                Image(
                    painter = painterResource(
                        Res.drawable.anonymous_icon
                    ),
                    modifier = Modifier.size(32.dp),
                    contentDescription = null
                )
                Text(
                    text = "Guest Account",
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 18.sp,
                    color = White
                )
            }
        }
    }
}
