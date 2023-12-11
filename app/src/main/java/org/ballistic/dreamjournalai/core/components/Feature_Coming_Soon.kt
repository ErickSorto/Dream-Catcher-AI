package org.ballistic.dreamjournalai.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ballistic.dreamjournalai.R

@Composable
fun FeatureComingSoonScreen(
    paddingValues: PaddingValues,
    onNavigateToAboutMeScreen: () -> Unit
) {
    val animationDuration = 5000
    val showButton = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .background(
                    color = colorResource(id = R.color.dark_blue).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                ),
        ) {
            TypewriterText(
                text = "This feature is being actively worked on and will be available soon! Stay tuned!",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                animationDuration = animationDuration,
                onAnimationComplete = { showButton.value = true }
            )
        }
        AnimatedVisibility(
            visible = showButton.value,
            modifier = Modifier
                .fillMaxWidth(.8f)
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Button(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.RedOrange) // Replace with your desired color
                ),
                onClick = { onNavigateToAboutMeScreen() }
            ) {
                Text(
                    text = "About Me",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}