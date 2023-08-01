package org.ballistic.dreamjournalai.feature_dream.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenView
import org.ballistic.dreamjournalai.navigation.MainGraph
import org.ballistic.dreamjournalai.ui.theme.DreamCatcherAITheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    var keepSplashOpened = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            DreamCatcherAITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    //  val screen by splashViewModel.state
                    MainGraph(
                        navController = navController,
                        onDataLoaded = { keepSplashOpened = false },
                        optionalDelay = keepSplashOpened
                    )
                }
            }
        }
    }
}
