package org.ballistic.dreamjournalai.dream_statistics.presentation


import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import org.ballistic.dreamjournalai.dream_statistics.StatisticEvent
import org.ballistic.dreamjournalai.dream_statistics.presentation.viewmodel.DreamStatisticScreenState

@Composable
fun DreamStatisticScreen(
    dreamStatisticScreenState: DreamStatisticScreenState,
    onEvent: (StatisticEvent) -> Unit
) {
    LaunchedEffect(key1 = Unit){
        onEvent(StatisticEvent.LoadDreams)
    }

    //public fun entryOf(x: Float, y: Float): FloatEntry = FloatEntry(x, y)
    //the y should be the highest number of the map
    val chartEntryModelProducer = ChartEntryModelProducer(
        listOf(
            entryOf(0, dreamStatisticScreenState.totalLucidDreams),
            entryOf(1, dreamStatisticScreenState.totalNormalDreams),
            entryOf(2, dreamStatisticScreenState.totalNightmares),
            entryOf(3, dreamStatisticScreenState.totalFavoriteDreams),
            entryOf(4, dreamStatisticScreenState.totalRecurringDreams),
            entryOf(5, dreamStatisticScreenState.totalFalseAwakenings),
        )
    )

    Chart(
        chart = columnChart(),
        chartModelProducer = chartEntryModelProducer,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(),
        modifier = Modifier.padding(16.dp),
        isZoomEnabled = false,
    )


}