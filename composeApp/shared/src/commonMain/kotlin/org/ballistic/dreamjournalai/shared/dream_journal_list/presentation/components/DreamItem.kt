package org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.components


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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream.Companion.dreamBackgroundImages
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.background_during_day
import dreamjournalai.composeapp.shared.generated.resources.baseline_cached_24
import dreamjournalai.composeapp.shared.generated.resources.baseline_star_24
import dreamjournalai.composeapp.shared.generated.resources.beautiful_lighthouse
import dreamjournalai.composeapp.shared.generated.resources.false_awakening_icon
import dreamjournalai.composeapp.shared.generated.resources.lighthouse_vector
import dreamjournalai.composeapp.shared.generated.resources.nightmare_icon
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.BrighterWhite
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Green
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Purple
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.SkyBlue
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Yellow
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DreamItem(
    modifier: Modifier = Modifier,
    dream: Dream,
    scope: CoroutineScope,
    hasBorder: Boolean = false,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    val imageResId =
        if (dream.backgroundImage >= 0 && dream.backgroundImage < dreamBackgroundImages.size) {
            dreamBackgroundImages[dream.backgroundImage]
        } else {
            Res.drawable.background_during_day
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
            .background(LightBlack.copy(alpha = 0.8f))
            .combinedClickable(
                onClick = {
                    if (!isLongPressTriggered) {
                        onClick()
                    }
                },
                onLongClick = {
                    isLongPressTriggered = true
                    scope.launch {
                        if (isLongPressTriggered) {
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
                val generatedImage = if (dream.generatedImage != "") {
                    dream.generatedImage
                } else {
                    null
                }

                val chosenBackground = imageResId

                if (generatedImage != null) {
                    CoilImage(
                        imageModel = { generatedImage },
                        modifier = Modifier.fillMaxSize().shimmerEffect(),
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            contentDescription = "Dream Image"
                        )
                    )
                } else {
                    Image(
                        painter = painterResource(chosenBackground),
                        modifier = Modifier.fillMaxSize().shimmerEffect(),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Dream Image"
                    )
                }
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
                    color = BrighterWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee()
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = dream.content,
                    style = typography.bodySmall,
                    fontSize = 13.sp,
                    color = White,
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
                    Icon(
                        painter = painterResource(Res.drawable.baseline_star_24),
                        tint = Yellow,
                        contentDescription = "Favorite",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(bottom = 4.dp),
                    )
                }

                if (dream.isNightmare) {
                    Icon(
                        painter = painterResource(Res.drawable.nightmare_icon),
                        tint = RedOrange,
                        contentDescription = "Nightmare",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(bottom = 4.dp),
                    )
                }

                if (dream.isRecurring) {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_cached_24),
                        tint = Green,
                        contentDescription = "Recurring",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(bottom = 4.dp),
                    )
                }

                if (dream.falseAwakening) {
                    Icon(
                        painter = painterResource(Res.drawable.false_awakening_icon),
                        tint = Purple,
                        contentDescription = "Day Dream",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(bottom = 4.dp),
                    )
                }

                if (dream.isLucid) {
                    Icon(
                        painter = painterResource(Res.drawable.lighthouse_vector),
                        tint = SkyBlue,
                        contentDescription = "Lucid",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(bottom = 4.dp),
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




