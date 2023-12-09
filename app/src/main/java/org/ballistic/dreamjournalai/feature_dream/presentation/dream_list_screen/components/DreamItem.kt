package org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream.Companion.dreamBackgroundImages


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DreamItem(
    dream: Dream,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    onDeleteClick: () -> Unit
) {
    val imageResId = if (dream.backgroundImage in dreamBackgroundImages) {
        dream.backgroundImage
    } else {
        R.drawable.background_during_day
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White.copy(alpha = 0.2f))

    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(60.dp)
                    .shadow(16.dp, CircleShape, true, Color.Black.copy(alpha = 0.8f))
                    .clip(CircleShape)
                    .background(Color.Transparent)

            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageResId),
                    contentDescription = "Color",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(0.dp, 16.dp, 0.dp, 16.dp)

            ) {
                Text(
                    text = dream.title,
                    style = typography.titleLarge,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = dream.content,
                    style = typography.bodyLarge,
                    color = Color.Black,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                //isFavorite

                if (dream.isLucid) {
                    Image(
                        painter = painterResource(id = R.drawable.lighthouse_vector),
                        contentDescription = "Lucid",
                        modifier = Modifier
                            .size(24.dp),
                        alignment = Alignment.TopCenter

                    )
                }

                if (dream.isFavorite) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_star_24),
                        contentDescription = "Favorite",
                        modifier = Modifier
                            .size(24.dp),
                        alignment = Alignment.Center
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
                    alignment = Alignment.BottomCenter
                )
            }
        }
    }
}
