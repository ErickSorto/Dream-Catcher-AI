package org.ballistic.dreamjournalai.dream_statistics.presentation


import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import org.ballistic.dreamjournalai.dream_statistics.StatisticEvent
import org.ballistic.dreamjournalai.dream_statistics.presentation.viewmodel.DreamStatisticScreenState

@Composable
fun DreamStatisticScreen(
    dreamStatisticScreenState: DreamStatisticScreenState,
    onEvent: (StatisticEvent) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        onEvent(StatisticEvent.LoadDreams)
    }
    val dreamTypeLabels =
        listOf("Lucid", "Normal", "Nightmare", "Favorite", "Recurring", "False Awakening")
    val barColors =
        listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan)


    if (dreamStatisticScreenState.dreams.isNotEmpty()) {
        val entries = listOf(
            entryOf(0, dreamStatisticScreenState.totalLucidDreams),
            entryOf(1, dreamStatisticScreenState.totalNormalDreams),
            entryOf(2, dreamStatisticScreenState.totalNightmares),
            entryOf(3, dreamStatisticScreenState.totalFavoriteDreams),
            entryOf(4, dreamStatisticScreenState.totalRecurringDreams),
            entryOf(5, dreamStatisticScreenState.totalFalseAwakenings),
        )
        val chartEntryModelProducer = ChartEntryModelProducer(
            entries
        )
        val horizontalAxisValueFormatter =
            AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                dreamTypeLabels.getOrNull(value.toInt()) ?: ""
            }
        val bottomAxis = rememberBottomAxis().apply {
            valueFormatter = horizontalAxisValueFormatter
            labelRotationDegrees = 90f
            sizeConstraint = Axis.SizeConstraint.TextWidth("False Awakening")
        }

        Chart(
            chart = columnChart(),
            chartModelProducer = chartEntryModelProducer,
            startAxis = rememberStartAxis(),
            bottomAxis = bottomAxis,
            marker = MarkerComponent(
                guideline = null,
                indicator = null,
                label = TextComponent.Builder().build(),
            ),
            modifier = Modifier
                .padding(32.dp)
                .height(300.dp),
            isZoomEnabled = false,
        )
    }
}
