package org.ballistic.dreamjournalai.dream_journal_list.dream_list_screen.components


import android.os.Vibrator
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.util.VibrationUtil
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream.Companion.dreamBackgroundImages


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DreamItem(
    modifier: Modifier = Modifier,
    dream: Dream,
    vibrator: Vibrator,
    scope: CoroutineScope,
    hasBorder: Boolean = false,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    val imageResId =
        if (dream.backgroundImage >= 0 && dream.backgroundImage < dreamBackgroundImages.size) {
            dreamBackgroundImages[dream.backgroundImage]
        } else {
            R.drawable.background_during_day
        }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val borderThickness = infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val shimmerBrush = shimmerBrush(infiniteTransition)
    val glowColor = Color.White

    val chosenModifier = if (hasBorder) {
        modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                clip = false,
                ambientColor = glowColor
            )
            .border(
            width = borderThickness.value.dp,
            brush = shimmerBrush,
            shape = RoundedCornerShape(8.dp)
        )
    } else {
        modifier
    }
    var isLongPressTriggered by remember { mutableStateOf(false) }
    Box(
        modifier = chosenModifier
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(id = R.color.light_black).copy(alpha = 0.8f))
            .combinedClickable(
                onClick = {
                    if (!isLongPressTriggered) {
                        VibrationUtil.triggerVibration(vibrator)
                        onClick()
                    }
                },
                onLongClick = {
                    isLongPressTriggered = true
                    scope.launch {
                        if (isLongPressTriggered) {
                            VibrationUtil.triggerVibration(vibrator)
                            onDeleteClick()
                        }
                        isLongPressTriggered = false // Reset after handling
                    }
                }
            )
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .padding(12.dp, 12.dp, 4.dp, 12.dp)
                    .size(118.dp)
                    .background(Color.Transparent)
                    .shadow(4.dp, RoundedCornerShape(8.dp), true)

            ) {
                val model = if (dream.generatedImage != "") {
                    dream.generatedImage
                } else {
                    imageResId
                }
                val painter = rememberAsyncImagePainter(
                    model,
                    filterQuality = FilterQuality.High
                )
                val painterState = painter.state
                val modifierImage = if (painterState is AsyncImagePainter.State.Loading) {
                    Modifier.shimmerEffect()
                } else {
                    Modifier
                }
                Image(
                    painter = rememberAsyncImagePainter(
                        model
                    ),
                    contentDescription = "Color",
                    contentScale = ContentScale.Crop,
                    modifier = modifierImage.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(0.dp, 12.dp, 0.dp, 12.dp)
            ) {
                Text(
                    text = dream.title,
                    style = typography.titleSmall,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.brighter_white),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee()
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = dream.content,
                    style = typography.bodySmall,
                    fontSize = 13.sp,
                    color = colorResource(id = R.color.white),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier
                    .padding(0.dp, 8.dp, 8.dp, 8.dp)
                    .fillMaxHeight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (dream.isFavorite) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_star_24),
                        contentDescription = "Favorite",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(bottom = 4.dp),
                        alignment = Alignment.Center
                    )
                }

                if (dream.isNightmare) {
                    Image(
                        painter = painterResource(id = R.drawable.nightmare_icon),
                        contentDescription = "Nightmare",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(bottom = 4.dp),
                        alignment = Alignment.Center
                    )
                }

                if (dream.isRecurring) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_cached_24),
                        contentDescription = "Recurring",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(bottom = 4.dp),
                        alignment = Alignment.Center
                    )
                }

                if (dream.falseAwakening) {
                    Image(
                        painter = painterResource(id = R.drawable.false_awakening_icon),
                        contentDescription = "Day Dream",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(bottom = 4.dp),
                        alignment = Alignment.Center
                    )
                }

                if (dream.isLucid) {
                    Image(
                        painter = painterResource(id = R.drawable.lighthouse_vector),
                        contentDescription = "Lucid",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(bottom = 4.dp),
                        alignment = Alignment.TopCenter

                    )
                }
            }
        }
    }
}

@Composable
fun shimmerBrush(transition: InfiniteTransition): Brush {
    val translateAnim = transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    return Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.6f),
            Color.White.copy(alpha = 0.9f),
            Color.White,
            Color.White.copy(alpha = 0.9f),
            Color.White.copy(alpha = 0.6f)
        ),
        start = Offset(translateAnim.value - 300f, translateAnim.value - 300f),
        end = Offset(translateAnim.value + 300f, translateAnim.value + 300f)
    )
}


@Composable
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "")

    // Animate the shimmer's offset
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(), // Start from the left beyond the component
        targetValue = 2 * size.width.toFloat(),   // End to the right beyond the component
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFBEBEBE), // Light gray for the edges
                Color(0xFFF8F8F8), // Very light gray, nearly white for the center
                Color(0xFFBEBEBE), // Light gray for the edges
            ),
            start = Offset(startOffsetX, 0f),        // Start position of the gradient
            end = Offset(startOffsetX + size.width, 0f) // End position of the gradient
        )
    ).onGloballyPositioned {
        size = it.size  // Update the size when the layout changes
    }
}




