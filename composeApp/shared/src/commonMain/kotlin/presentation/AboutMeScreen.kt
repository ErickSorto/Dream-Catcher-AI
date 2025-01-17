package presentation

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.burrito_icon
import dreamjournalai.composeapp.shared.generated.resources.erick_image
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.DarkBlue
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
import org.ballistic.dreamjournalai.shared.core.components.TypewriterText
import org.ballistic.dreamjournalai.shared.dream_main.domain.MainScreenEvent
import org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel.MainScreenViewModelState
import org.jetbrains.compose.resources.painterResource
import presentation.components.DreamAboutMeScreenTopBar

@Composable
fun AboutMeScreen(
    mainScreenViewModelState: MainScreenViewModelState,
    onMainScreenEvent: (MainScreenEvent) -> Unit,
) {
    val animationDuration = 5000
    val showButton = remember { mutableStateOf(false) }

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
                                DarkBlue.copy(alpha = 0.6f),
                                DarkBlue.copy(alpha = 0.9f),
                                DarkBlue.copy(alpha = 1f),
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
                            onMainScreenEvent(MainScreenEvent.OpenStoreLink)
                        }
                ) {
                    Image(
                        painter = painterResource(Res.drawable.erick_image),
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

//            AnimatedVisibility(
//                visible = showButton.value,
//                modifier = Modifier
//                    .fillMaxWidth(.8f)
//                    .align(Alignment.BottomCenter)
//                    .padding(bottom = 32.dp),
//            ) {
//                Spacer(Modifier.height(16.dp))
//
//                Button(
//                    modifier = Modifier.padding(16.dp),
//                    shape = RoundedCornerShape(6.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = RedOrange
//                    ),
//                    onClick = {
//                        val url = "https://ko-fi.com/ericksorto"
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                        openUrlLauncher.launch(intent)
//                    }
//                ) {
//                    Text(
//                        text = "Buy me a burrito!",
//                        modifier = Modifier.padding(start = 8.dp),
//                        fontSize = 18.sp,
//                        color = Color.White
//                    )
//                    Image(
//                        painter = painterResource(
//                            Res.drawable.burrito_icon
//                        ),
//                        contentDescription = null,
//                        modifier = Modifier.padding(start = 8.dp)
//                    )
//                }
//            }
        }
    }
}
