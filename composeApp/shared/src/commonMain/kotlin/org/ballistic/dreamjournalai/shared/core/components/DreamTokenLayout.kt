package org.ballistic.dreamjournalai.shared.core.components

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.dream_token
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Black
import org.jetbrains.compose.resources.painterResource

@Composable
fun DreamTokenLayout(
    totalDreamTokens: Int,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Black.copy(alpha = 0.3f)
            ),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(Res.drawable.dream_token),
            contentDescription = "Dream Token",
            modifier = Modifier
                .size(40.dp)
                .padding(4.dp, 4.dp, 0.dp, 4.dp),
            contentScale = ContentScale.Fit,
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