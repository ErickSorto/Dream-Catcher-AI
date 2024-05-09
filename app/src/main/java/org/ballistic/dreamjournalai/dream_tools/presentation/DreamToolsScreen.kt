package org.ballistic.dreamjournalai.dream_tools.presentation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.ballistic.dreamjournalai.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.dream_tools.presentation.components.DreamToolsGrid
import org.ballistic.dreamjournalai.dream_tools.presentation.components.DreamToolsScreenTopBar
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DreamToolsScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigate: (Int, String) -> Unit,
    mainScreenViewModelState: MainScreenViewModelState,
) {
    Scaffold(
        topBar = {
            DreamToolsScreenTopBar(mainScreenViewModelState = mainScreenViewModelState)
        },
        containerColor = Color.Transparent,
    ) {
        DreamToolsGrid(
            onNavigate = onNavigate,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier
                .padding(it)
                .dynamicBottomNavigationPadding()
                .fillMaxSize()
        )
    }
}