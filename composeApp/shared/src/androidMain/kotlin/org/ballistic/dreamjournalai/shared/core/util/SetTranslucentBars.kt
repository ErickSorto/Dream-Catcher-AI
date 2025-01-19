package org.ballistic.dreamjournalai.shared.core.util

import android.app.Activity
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

@Composable
actual fun setTranslucentBars(darkTheme: Boolean) {
    val view = LocalView.current
    val context = LocalContext.current
    if (!view.isInEditMode) {
        SideEffect {
            (context as Activity).window.run {
                statusBarColor = Color.Magenta.copy(alpha = 0f).toArgb()
                navigationBarColor = Color(0xFFF1B763).copy(alpha = 0.1f).toArgb()
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            }
            ViewCompat.getWindowInsetsController(view)?.run {
                isAppearanceLightStatusBars = darkTheme
                isAppearanceLightNavigationBars =
                    !darkTheme  // Set navigation bar icons to contrast with the background
            }
        }
    }
}