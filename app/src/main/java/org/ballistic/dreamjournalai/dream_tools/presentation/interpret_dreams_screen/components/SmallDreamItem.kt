package org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.components

import android.os.Vibrator
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.yml.charts.common.extensions.isNotNull
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.util.VibrationUtil
import org.ballistic.dreamjournalai.dream_journal_list.dream_list_screen.components.shimmerEffect
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallDreamItem(
    modifier: Modifier = Modifier,
    dream: Dream = Dream(),
    isDeleted: Boolean = false,
    vibrator: Vibrator,
    imageSize: Dp,
    onClick: () -> Unit
) {
    val imageResId =
        if (dream.backgroundImage >= 0 && dream.backgroundImage < Dream.dreamBackgroundImages.size && !isDeleted) {
            Dream.dreamBackgroundImages[dream.backgroundImage]
        } else {
            R.drawable.baseline_question_mark_24
        }

    val title = if (isDeleted) {
        "Deleted Dream"
    } else {
        dream.title
    }


    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(colorResource(id = R.color.white).copy(alpha = 0.2f))
            .clickable {
                VibrationUtil.triggerVibration(vibrator)
                onClick()
            }
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp, 8.dp, 0.dp, 8.dp)
                    .size(size = imageSize)
                    .background(Color.Transparent)
                    .shadow(4.dp, CircleShape, true)
            ) {
                val model = if (dream.generatedImage.isNotBlank() && dream.generatedImage.isNotNull()) {
                    dream.generatedImage
                } else {
                    imageResId
                }
                val painter = rememberAsyncImagePainter(
                    model,
                    filterQuality = FilterQuality.Low
                )
                val painterState = painter.state
                val modifierImage = if (painterState is AsyncImagePainter.State.Loading) {
                    Modifier.shimmerEffect()
                } else {
                    Modifier
                }
                if (isDeleted) {
                    Icon(
                        painter = painterResource(imageResId),
                        contentDescription = "Deleted Dream Icon Question Mark",
                        modifier = Modifier.fillMaxSize().background(Color.LightGray),
                        tint = colorResource(id = R.color.RedOrange)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model
                        ),
                        contentDescription = "Color",
                        contentScale = ContentScale.Crop,
                        modifier = modifierImage.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))


            Text(
                text = title,
                style = typography.titleSmall,
                color = colorResource(id = R.color.brighter_white),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .basicMarquee()
                    .padding(end = 12.dp)
            )

            if (dream.isFavorite) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_star_24),
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .size(24.dp)
                )
            }

            if (dream.isLucid) {
                Image(
                    painter = painterResource(id = R.drawable.lighthouse_vector),
                    contentDescription = "Lucid",
                    modifier = Modifier
                        .size(24.dp)
                )
            }
            if (dream.isFavorite || dream.isLucid) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
