package org.ballistic.dreamjournalai.shared

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.mikhailovskii.inappreview.InAppReviewDelegate
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

object Static {
    init {
        System.setProperty("kotlin-logging-to-android-native", "true")
    }
}
private val static = Static

private val logger = KotlinLogging.logger {

}

class MainActivity : ComponentActivity() {


    private var keepSplashOpened = true

    private val inAppReviewManager: InAppReviewDelegate by lazy {
        getKoin().get<InAppReviewDelegate> { parametersOf(this) }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        logger.debug { "onCreate" }
        setContent {
            App(
                onSplashFinished = {
                    keepSplashOpened = false
                }
            )
        }
    }

    private fun showInAppReview() {
        lifecycleScope.launch {
            inAppReviewManager.requestInAppReview().collect { reviewCode ->
                // handle the result
            }
        }
    }
}
