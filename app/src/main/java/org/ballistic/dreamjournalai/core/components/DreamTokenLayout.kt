package org.ballistic.dreamjournalai.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import org.ballistic.dreamjournalai.R

@Composable
fun DreamTokenLayout(
    totalDreamTokens: Int,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                colorResource(id = R.color.black).copy(alpha = 0.3f)
            ),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.drawable.dream_token),
            contentDescription = "DreamToken",
            modifier = Modifier
                .size(40.dp)
                .padding(4.dp, 4.dp, 0.dp, 4.dp),
        )
        AnimatedContent(targetState = totalDreamTokens, label = "") { totalDreamTokens ->
            TypewriterText(
                modifier = Modifier.padding(4.dp, 4.dp, 8.dp, 4.dp),
                text = totalDreamTokens.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                animationDuration = 250,
            )
        }
    }
}