package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import org.ballistic.dreamjournalai.R

@Composable
fun AnonymousButton(
    modifier: Modifier,
    isVisible: MutableState<Boolean>,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = isVisible.value,
            enter = slideInHorizontally(initialOffsetX = { 1000 }),
            exit = slideOutHorizontally { -1000 }
        ) {
            Button(
                modifier = Modifier.padding().fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.dark_blue)
                ),
                onClick = onClick
            ) {
                Image(
                    painter = painterResource(
                        R.drawable.anonymous_icon
                    ),
                    modifier = Modifier.size(32.dp),
                    contentDescription = null
                )
                Text(
                    text = "Guest Account",
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 18.sp,
                    color = colorResource(R.color.white)
                )
            }
        }
    }
}
