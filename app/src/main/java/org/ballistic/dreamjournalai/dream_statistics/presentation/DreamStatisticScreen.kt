package org.ballistic.dreamjournalai.dream_statistics.presentation


import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.dream_statistics.StatisticEvent
import org.ballistic.dreamjournalai.dream_statistics.presentation.components.DreamChartBarChart
import org.ballistic.dreamjournalai.dream_statistics.presentation.components.DreamStatisticScreenTopBar
import org.ballistic.dreamjournalai.dream_statistics.presentation.components.TopFiveDreamWordPieChart
import org.ballistic.dreamjournalai.dream_statistics.presentation.viewmodel.DreamStatisticScreenState
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.ArcRotationAnimation
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@Composable
fun DreamStatisticScreen(
    dreamStatisticScreenState: DreamStatisticScreenState,
    mainScreenViewModelState: MainScreenViewModelState,
    onEvent: (StatisticEvent) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    LaunchedEffect(key1 = Unit) {
        onEvent(StatisticEvent.LoadDreams)
    }

    Scaffold(
        topBar = {
            DreamStatisticScreenTopBar(mainScreenViewModelState = mainScreenViewModelState)
        },
        containerColor = Color.Transparent,
    ) {
        if (dreamStatisticScreenState.dreams.isEmpty() ||
            dreamStatisticScreenState.isDreamWordFilterLoading){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(it)
                    .navigationBarsPadding()
                    .fillMaxSize()
            ) {
                ArcRotationAnimation(infiniteTransition)
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState()),
            ) {
                TopFiveDreamWordPieChart(dreamStatisticScreenState = dreamStatisticScreenState)
                DreamChartBarChart(dreamStatisticScreenState = dreamStatisticScreenState)
            }
        }
    }
}
