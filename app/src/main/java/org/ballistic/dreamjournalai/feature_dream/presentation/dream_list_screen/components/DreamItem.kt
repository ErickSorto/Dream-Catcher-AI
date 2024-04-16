package org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream.Companion.dreamBackgroundImages

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DreamItem(
    dream: Dream,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val imageResId = if (dream.backgroundImage >= 0 && dream.backgroundImage < dreamBackgroundImages.size) {
        dreamBackgroundImages[dream.backgroundImage]  // Use backgroundImage as an index to fetch the actual drawable resource ID
    } else {
        R.drawable.background_during_day  // Fallback default drawable resource ID
    }


    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(id = R.color.light_black).copy(alpha = 0.8f))
            .clickable {
                onClick()
            }
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
                    .size(75.dp)
                    .background(Color.Transparent)
                    .shadow(4.dp, RoundedCornerShape(8.dp), true)


            ) {
                val painter = rememberAsyncImagePainter(
                    dream.generatedImage ?: imageResId,
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
                        dream.generatedImage ?: imageResId
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
                    color = colorResource(id = R.color.brighter_white),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee()
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = dream.content,
                    style = typography.bodySmall,
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
                            .size(24.dp)
                            .padding(bottom = 4.dp),
                        alignment = Alignment.Center
                    )
                }

                if (dream.isLucid) {
                    Image(
                        painter = painterResource(id = R.drawable.lighthouse_vector),
                        contentDescription = "Lucid",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(bottom = 4.dp),
                        alignment = Alignment.TopCenter

                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.baseline_delete_24),
                    contentDescription = "Delete",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onDeleteClick()
                        },
                    alignment = Alignment.BottomCenter,
                )
            }
        }
    }

}

@Composable
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF505050),
                Color(0xFF8F8B8B),
                Color(0xFF505050),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}


