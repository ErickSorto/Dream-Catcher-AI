package org.ballistic.dreamjournalai.feature_dream.presentation.about_me_screen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.feature_dream.presentation.about_me_screen.components.DreamAboutMeScreenTopBar
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@Composable
fun AboutMeScreen(
    mainScreenViewModelState: MainScreenViewModelState
) {
    val animationDuration = 5000
    val showButton = remember { mutableStateOf(false) }
    val openUrlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { /* No result handling needed in this case */ }

    Scaffold(
        topBar = {
            DreamAboutMeScreenTopBar(mainScreenViewModelState = mainScreenViewModelState)
        },
        containerColor = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .navigationBarsPadding(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorResource(id = R.color.dark_blue).copy(alpha = 0.6f),
                                colorResource(id = R.color.dark_blue).copy(alpha = 0.9f),
                                colorResource(id = R.color.dark_blue).copy(alpha = 1f),
                            ),
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp, 16.dp, 16.dp, 0.dp)
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .shadow(8.dp)
                        .clickable {
                            openUrlLauncher.launch(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.buymeacoffee.com/ericksorto")
                                )
                            )
                        }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.erick_image),
                        contentDescription = "Erick",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                Spacer(Modifier.width(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    TypewriterText(
                        text = "\"Hi, I'm Erick Sorto, an Android developer who loves creating apps! I spent 6 months developing this app, learning and working hard." +
                                " Support my passion projects by buying me a burrito, and thank you for your appreciation!\"",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Start,
                        animationDuration = animationDuration,
                        onAnimationComplete = { showButton.value = true }
                    )
                }
            }
            AnimatedVisibility(
                visible = showButton.value,
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
            ) {
                Spacer(Modifier.height(16.dp))

                Button(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.RedOrange)
                    ),
                    onClick = {
                        val url = "https://ko-fi.com/ericksorto"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        openUrlLauncher.launch(intent)
                    }
                ) {
                    Text(
                        text = "Buy me a burrito!",
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Image(
                        painter = rememberAsyncImagePainter(
                            R.drawable.burrito_icon
                        ),
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }

}