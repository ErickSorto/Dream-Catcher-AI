package org.ballistic.dreamjournalai.shared.dream_statistics.presentation.components

import androidx.compose.runtime.Composable
import org.ballistic.dreamjournalai.shared.dream_statistics.presentation.viewmodel.DreamStatisticScreenState


@Composable
expect fun TopSixDreamWordPieChart(
    dreamStatisticScreenState: DreamStatisticScreenState
)
