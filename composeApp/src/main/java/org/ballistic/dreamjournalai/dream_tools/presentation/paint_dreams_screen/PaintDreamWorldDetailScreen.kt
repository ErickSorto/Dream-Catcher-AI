package org.ballistic.dreamjournalai.dream_tools.presentation.paint_dreams_screen

import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.dream_tools.domain.DreamTools
import org.ballistic.dreamjournalai.dream_tools.presentation.components.DreamToolScreenWithNavigateUpTopBar

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PaintDreamWorldDetailScreen(
    imageID: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    bottomPadding: Dp,
    onNavigate: () -> Unit,
    navigateUp: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)

    Scaffold(
        topBar = {
            DreamToolScreenWithNavigateUpTopBar(
                title = "Visualize Dream World",
                navigateUp = navigateUp,
                vibrator = vibrator
            )
        },
        containerColor = Color.Transparent,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .dynamicBottomNavigationPadding()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = bottomPadding
                )
                .padding(16.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dream Visualization Section
            Column(
                modifier = Modifier
                    .background(
                        color = colorResource(id = R.color.light_black).copy(alpha = 0.85f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = imageID),
                    contentDescription = "Dream Visualization Tool",
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
                        .clip(RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = DreamTools.DREAM_WORLD.title,
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(12.dp))
                TypewriterText(
                    text = "Visualize your dream worlds to gain deeper insights. " +
                            "Select and compile multiple dreams to create a comprehensive visual map. " +
                            "Identify and understand recurring themes, patterns, and symbols within your dreamscape.",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                    delay = 500,
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Optional: Add a visualization preview or interactive component
                // For example, a placeholder for a dream map or chart
                // Replace with actual visualization components as needed
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            color = colorResource(id = R.color.light_black).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Dream Map Preview",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Button Section
            Button(
                onClick = {
                    onNavigate()
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.RedOrange).copy(
                        alpha = 0.9f
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_brush_24),
                    contentDescription = "Visualize Dreams",
                    modifier = Modifier.size(36.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Create Dream Map",
                    modifier = Modifier
                        .weight(1f),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(12.dp))
                Image(
                    painter = painterResource(id = R.drawable.baseline_brush_24),
                    contentDescription = "Visualize Dreams",
                    modifier = Modifier.size(36.dp),
                    colorFilter = ColorFilter.tint(Color.Transparent)
                )
            }
        }
    }
}
