package org.ballistic.dreamjournalai.feature_dream.presentation

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.ballistic.dreamjournalai.navigation.MainGraph
import org.ballistic.dreamjournalai.ui.theme.DreamCatcherAITheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var keepSplashOpened = true

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Create your custom animation.
            val fadeOut = ObjectAnimator.ofFloat(splashScreenView, View.ALPHA, 1f, 0f)
            fadeOut.duration = 300
            fadeOut.interpolator = AnticipateInterpolator()
            fadeOut.doOnEnd {
                splashScreenView.visibility = View.GONE
            }
            fadeOut.start()
        }

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
                    ) { keepSplashOpened = false }
                }
            }
        }
    }
}
