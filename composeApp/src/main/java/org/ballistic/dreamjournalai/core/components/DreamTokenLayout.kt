package org.ballistic.dreamjournalai.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import org.ballistic.dreamjournalai.R

@Composable
fun DreamTokenLayout(
    totalDreamTokens: Int,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                colorResource(id = R.color.black).copy(alpha = 0.3f)
            ),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        CoilImage(
            imageModel =  { R.drawable.dream_token },
            modifier = Modifier
                .size(40.dp)
                .padding(4.dp, 4.dp, 0.dp, 4.dp),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Fit,
            )
        )
        AnimatedContent(targetState = totalDreamTokens, label = "") { totalDreamTokens ->
            TypewriterText(
                modifier = Modifier.padding(4.dp, 4.dp, 8.dp, 4.dp),
                text = totalDreamTokens.toString(),
                style = MaterialTheme.typography.titleMedium,
                animationDuration = 250,
            )
        }
    }
}