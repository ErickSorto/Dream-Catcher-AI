package org.ballistic.dreamjournalai.core.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun Modifier.dynamicBottomNavigationPadding(): Modifier {
    val orientation = LocalContext.current.resources.configuration.orientation

    return if (orientation == 1) {
        this
    } else {
        this.navigationBarsPadding()
    }
}