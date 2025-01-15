package org.ballistic.dreamjournalai.shared.core.components
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.content.res.Configuration

@Composable
actual fun Modifier.dynamicBottomNavigationPadding(): Modifier {
    val orientation = LocalContext.current.resources.configuration.orientation
    return if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        this
    } else {
        this.navigationBarsPadding()
    }
}
