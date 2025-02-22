package org.ballistic.dreamjournalai.shared.dream_tools.presentation.interpret_dreams_screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.interpret_vector
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
import org.ballistic.dreamjournalai.shared.core.components.TypewriterText
import org.ballistic.dreamjournalai.shared.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.shared.dream_tools.domain.DreamTools
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.components.DreamToolScreenWithNavigateUpTopBar
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.InterpretDreamsDetailScreen(
    imageID: DrawableResource,
    imagePath: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    bottomPadding: Dp,
    onNavigate: () -> Unit,
    navigateUp: () -> Unit
) {

    Scaffold(
        topBar = {
            DreamToolScreenWithNavigateUpTopBar(
                title = "Interpret Dreams",
                navigateUp = navigateUp,
                onEvent = { }//TODO: TRIGGER VIBRATION
            )
        },
        containerColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .dynamicBottomNavigationPadding()
                .padding(top = it.calculateTopPadding(), bottom = bottomPadding)
                .padding(16.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = LightBlack.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Image(
                    painter = painterResource(imageID),
                    contentDescription = "Mass Interpretation Tool",
                    modifier = Modifier
                        .aspectRatio(16 / 9f)
                        .fillMaxWidth()
                        .sharedElement(
                            rememberSharedContentState(key = "image/$imagePath"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                tween(500)
                            }
                        )
                        .clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = DreamTools.AnalyzeDreams.title,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                TypewriterText(
                    text = "Interpretation tool for multiple dreams. This allows you to select " +
                            "multiple dreams and analyze them together. You can find common themes, " +
                            "emotions, and symbols in your dreams.",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                    delay = 550,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onNavigate()
                },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedOrange.copy(
                        alpha = 0.8f
                    )
                ),
            ) {
                Image(
                    painter = painterResource(Res.drawable.interpret_vector),
                    contentDescription = "Random Dream",
                    modifier = Modifier.size(40.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Select Dreams",
                    modifier = Modifier
                        .padding(8.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(Res.drawable.interpret_vector),
                    contentDescription = "Random Dream",
                    modifier = Modifier.size(40.dp),
                    colorFilter = ColorFilter.tint(Color.Transparent)
                )
            }
        }
    }
}
