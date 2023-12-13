package org.ballistic.dreamjournalai.dream_dictionary.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import org.ballistic.dreamjournalai.R

@Composable
fun DictionaryWordItem(
    word: String,
    isUnlocked: Boolean,
    cost: Int,
    modifier: Modifier = Modifier,
    onWordClick: (isUnlocked: Boolean) -> Unit = {}
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isUnlocked) colorResource(id = R.color.dark_blue).copy(alpha = 0.6f) else colorResource(id = R.color.dark_blue).copy(alpha = 0.5f))
            .fillMaxWidth()
            .clickable {
                onWordClick(isUnlocked)
            }
    ) {
        Column(
            modifier = Modifier .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isUnlocked) colorResource(id = R.color.brighter_white) else colorResource(id = R.color.brighter_white).copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            if (isUnlocked) {
                Text(
                    text = "Tap to view word",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.brighter_white),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tap to unlock",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(id = R.color.brighter_white),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    DictionaryCostLabel(cost = cost)
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