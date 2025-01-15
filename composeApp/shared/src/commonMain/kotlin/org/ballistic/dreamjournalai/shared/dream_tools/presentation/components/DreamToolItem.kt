package org.ballistic.dreamjournalai.shared.dream_tools.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun DreamToolItem(
    title: String,
    icon: DrawableResource,
    description: String,
    enabled: Boolean = true,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(16f / 13f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = LightBlack.copy(
                alpha = 0.8f
            )
        ),
        enabled = enabled
    ) {
        val alphaIfDisabled = if (enabled) 1f else 0.4f
        val textColorIfDisabled = if (enabled) Color.White else Color.Gray
        val titleIfDisabled = if (enabled) title else "Coming soon.."
        Image(
            painter = painterResource(icon),
            contentDescription = description,
            modifier = modifier
                .alpha(alphaIfDisabled)
                .aspectRatio(16 / 9f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            titleIfDisabled,
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee()
                .padding(),
            style = typography.labelMedium,
            color = textColorIfDisabled,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}