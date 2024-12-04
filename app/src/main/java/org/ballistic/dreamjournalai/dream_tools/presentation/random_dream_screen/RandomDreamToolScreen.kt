package org.ballistic.dreamjournalai.dream_tools.presentation.random_dream_screen

import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.dream_tools.domain.event.RandomToolEvent
import org.ballistic.dreamjournalai.dream_tools.domain.DreamTools
import org.ballistic.dreamjournalai.dream_tools.presentation.components.DreamToolScreenWithNavigateUpTopBar
import org.ballistic.dreamjournalai.navigation.Screens

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RandomDreamToolScreen(
    randomDreamToolScreenState: RandomDreamToolScreenState,
    imageID: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    bottomPadding: Dp,
    onEvent: (RandomToolEvent) -> Unit,
    navigateTo: (String) -> Unit,
    navigateUp: () -> Unit
) {

    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    LaunchedEffect(Unit) {
        onEvent(RandomToolEvent.GetDreams)
    }

    // In your composable function
    LaunchedEffect(key1 = randomDreamToolScreenState) {
        snapshotFlow { randomDreamToolScreenState.randomDream }
            .collect { randomDream ->
                randomDream?.let {
                    navigateTo(
                        Screens.AddEditDreamScreen.route +
                                "?dreamId=${it.id}&dreamImageBackground=${it.backgroundImage}"
                    )
                }
            }
    }

    Scaffold(
        topBar = {
            DreamToolScreenWithNavigateUpTopBar(
                title = "Random Dream",
                navigateUp = navigateUp,
                vibrator = vibrator
            )
        },
        containerColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .dynamicBottomNavigationPadding()
                .padding(top = it.calculateTopPadding(), bottom = bottomPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = colorResource(id = R.color.light_black).copy(alpha = 0.8f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()
            ) {
                CoilImage(
                    imageModel = { imageID },
                    modifier = Modifier
                        .aspectRatio(16 / 9f)
                        .fillMaxWidth()
                        .sharedElement(
                            rememberSharedContentState(key = "image/$imageID"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                tween(500)
                            }
                        )
                        .clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)),
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop
                    ),
                )
                Text(
                    text = DreamTools.RandomDreamPicker.title,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                TypewriterText(
                    text = "Reading a random dream is important because it sharpens your ability to " +
                            "recall dreams and reveals underlying patterns, crucial for insightful " +
                            "dream analysis and mastering lucid dreaming.",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    style = typography.bodyMedium,
                    delay = 550,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onEvent(RandomToolEvent.GetRandomDream)
                },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.RedOrange).copy(
                        alpha = 0.8f
                    )
                ),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_casino_24),
                    contentDescription = "Random Dream",
                    modifier = Modifier.size(40.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Random Dream",
                    modifier = Modifier
                        .padding(8.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.baseline_casino_24),
                    contentDescription = "Random Dream",
                    modifier = Modifier.size(40.dp),
                    colorFilter = ColorFilter.tint(Color.Transparent)
                )
            }
        }
    }
}
