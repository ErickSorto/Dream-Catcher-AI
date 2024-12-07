package org.ballistic.dreamjournalai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import org.ballistic.dreamjournalai.navigation.MainGraph
import org.ballistic.dreamjournalai.ui.theme.DreamCatcherAITheme
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {

    private var keepSplashOpened = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }

        setContent {
                DreamCatcherAITheme {
                    KoinAndroidContext {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val navController = rememberNavController()
                            //  val screen by splashViewModel.state
                            MainGraph(
                                navController = navController,
                            ) { keepSplashOpened = false }
                        }
                    }
                }
        }
    }
}
