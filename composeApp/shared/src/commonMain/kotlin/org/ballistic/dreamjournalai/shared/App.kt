package org.ballistic.dreamjournalai.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import org.ballistic.dreamjournalai.shared.navigation.MainGraph
import org.ballistic.dreamjournalai.shared.theme.DreamJournalAITheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App(
    onSplashFinished: () -> Unit = {}
) {

    Logger.d { "Kermit Testing!" }

    DreamJournalAITheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                val navController = rememberNavController()
                //  val screen by splashViewModel.state
                MainGraph(
                    navController = navController,
                    onDataLoaded = onSplashFinished
                )
            }
    }
}

