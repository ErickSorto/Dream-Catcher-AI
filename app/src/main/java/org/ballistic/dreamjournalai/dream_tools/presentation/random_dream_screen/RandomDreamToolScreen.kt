package org.ballistic.dreamjournalai.dream_tools.presentation.random_dream_screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_tools.presentation.components.DreamToolScreenWithNavigateUpTopBar
import org.ballistic.dreamjournalai.navigation.Screens

@Composable
fun RandomDreamToolScreen(
    randomDreamToolScreenState: RandomDreamToolScreenState,
    onEvent: (RandomToolEvent) -> Unit,
    navigateTo: (String) -> Unit,
    navigateUp: () -> Unit
) {
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
                navigateUp = navigateUp
            )
        },
        containerColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Text(
                text = "Reading a random dream is good for lucid dreaming and analyzing dreams because it " +
                        "helps you remember your dreams and it helps you to see patterns in your dreams.",
                modifier = Modifier.padding(8.dp),
                color = Color.White,
                textAlign = TextAlign.Center
            )

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
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Text(
                    text = "Random Dream",
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}