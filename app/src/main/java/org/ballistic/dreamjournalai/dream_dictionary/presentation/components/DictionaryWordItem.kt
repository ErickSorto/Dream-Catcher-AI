package org.ballistic.dreamjournalai.dream_dictionary.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_dictionary.presentation.viewmodel.DictionaryWord

@Composable
fun DictionaryWordItem(
    modifier: Modifier = Modifier,
    wordItem: DictionaryWord,
    onWordClick: () -> Unit = {},
    playAnimation: Boolean = true  // Added to control the animation
) {
    // Scale animation
    val animatedProgress = remember { Animatable(initialValue = 0.8f) }
    if (playAnimation) {
        LaunchedEffect(key1 = wordItem) {
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        }
    }

    val animatedModifier = if (playAnimation) {
        modifier
            .graphicsLayer(
                scaleX = animatedProgress.value,
                scaleY = animatedProgress.value
            )
    } else {
        modifier
    }

    // Rest of your composable code remains the same
    Box(
        modifier = animatedModifier
            .padding(8.dp, 8.dp, 8.dp, 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (wordItem.isUnlocked) colorResource(id = R.color.light_black).copy(alpha = 0.8f) else colorResource(
                    id = R.color.light_black
                ).copy(alpha = 0.2f)
            )
            .fillMaxWidth()
            .clickable {
                onWordClick()
            }
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = wordItem.word,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (wordItem.isUnlocked) colorResource(id = R.color.brighter_white) else colorResource(
                    id = R.color.brighter_white
                ).copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 24.dp, horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            if (wordItem.isUnlocked) {
                Text(
                    text = "View Word",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.brighter_white),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Unlock",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(id = R.color.brighter_white),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    DictionaryCostLabel(cost = wordItem.cost)
                }
            }
        }
    }
}

@Composable
fun DictionaryCostLabel(
    cost: Int,
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.5f)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.drawable.dream_token),
            contentDescription = "DreamToken",
            modifier = Modifier
                .size(35.dp)
                .padding(8.dp, 4.dp, 0.dp, 4.dp),
        )

        Text(
            modifier = Modifier.padding(4.dp, 4.dp, 8.dp, 4.dp),
            text = "$cost",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
            color = Color.White
        )
    }
}