package org.ballistic.dreamjournalai.shared.dream_tools.presentation.interpret_dreams_screen.components

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.baseline_question_mark_24
import dreamjournalai.composeapp.shared.generated.resources.baseline_star_24
import dreamjournalai.composeapp.shared.generated.resources.lighthouse_vector
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.BrighterWhite
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.components.shimmerEffect
import org.jetbrains.compose.resources.painterResource

@Composable
fun SmallDreamItem(
    modifier: Modifier = Modifier,
    dream: Dream = Dream(),
    isDeleted: Boolean = false,
    imageSize: Dp,
    onClick: () -> Unit
) {
    val imageResId =
        if (dream.backgroundImage >= 0 && dream.backgroundImage < Dream.dreamBackgroundImages.size && !isDeleted) {
            Dream.dreamBackgroundImages[dream.backgroundImage]
        } else {
            Res.drawable.baseline_question_mark_24
        }

    val title = if (isDeleted) {
        "Deleted Dream"
    } else {
        dream.title
    }


    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(White.copy(alpha = 0.2f))
            .clickable {
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
                val model = if (dream.generatedImage.isNotEmpty()) {
                    dream.generatedImage
                } else {
                    imageResId
                }

                if (isDeleted) {
                    Icon(
                        painter = painterResource(imageResId),
                        contentDescription = "Deleted Dream Icon Question Mark",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                        tint = RedOrange
                    )
                } else {
                    CoilImage(
                        imageModel = {
                            model
                        },
                        modifier = Modifier.fillMaxSize().shimmerEffect(),
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))


            Text(
                text = title,
                style = typography.titleSmall,
                color = BrighterWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .basicMarquee()
                    .padding(end = 12.dp)
            )

            if (dream.isFavorite) {
                Image(
                    painter = painterResource(Res.drawable.baseline_star_24),
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .size(24.dp)
                )
            }

            if (dream.isLucid) {
                Image(
                    painter = painterResource(Res.drawable.lighthouse_vector),
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
