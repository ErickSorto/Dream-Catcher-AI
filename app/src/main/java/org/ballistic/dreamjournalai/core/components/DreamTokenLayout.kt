package org.ballistic.dreamjournalai.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@Composable
fun DreamTokenLayout(
    mainScreenViewModelState: MainScreenViewModelState
) {
    val token = mainScreenViewModelState.dreamTokens.collectAsStateWithLifecycle().value
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.5f)),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.drawable.dream_token),
            contentDescription = "DreamToken",
            modifier = Modifier
                .size(35.dp)
                .padding(8.dp, 4.dp, 0.dp, 4.dp),
        )
        AnimatedContent(targetState = token, label = "") { it
            Text(
                modifier = Modifier.padding(4.dp, 4.dp, 8.dp, 4.dp),
                text = token.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
            )
        }

    }
}