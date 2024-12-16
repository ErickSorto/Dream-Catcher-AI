package org.ballistic.dreamjournalai.shared.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay


@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = Color.White,
    animationDuration: Int = 3000,
    onAnimationComplete: () -> Unit = {},
    delay: Int = 0
) {
    val typedText = remember { mutableStateOf(AnnotatedString("")) }
    val animatedIndex = remember { Animatable(0f) }
    val hasDelayed = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(text) {
        if (!hasDelayed.value) {
            delay(delay.toLong())
            hasDelayed.value = true
        }
        animatedIndex.snapTo(0f)
        typedText.value = AnnotatedString("")
        animatedIndex.animateTo(
            targetValue = text.length.toFloat(),
            animationSpec = tween(animationDuration),
            block = {
                typedText.value = AnnotatedString(text.substring(0, animatedIndex.value.toInt()))
            }
        )
        onAnimationComplete()
    }

    Text(
        text = typedText.value,
        modifier = modifier,
        textAlign = textAlign,
        style = style,
        color = color
    )
}
