package org.ballistic.dreamjournalai.dream_tools.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.ballistic.dreamjournalai.dream_tools.presentation.components.DreamToolsGrid
import org.ballistic.dreamjournalai.dream_tools.presentation.components.DreamToolsScreenTopBar
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@Composable
fun DreamToolsScreen(
    onNavigate: (String) -> Unit,
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
            modifier = Modifier
                .padding(it)
                .navigationBarsPadding()
                .fillMaxSize()
        )
    }
}