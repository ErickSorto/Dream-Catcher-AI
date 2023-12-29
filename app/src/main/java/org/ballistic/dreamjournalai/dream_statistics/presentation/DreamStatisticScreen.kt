package org.ballistic.dreamjournalai.dream_statistics.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import org.ballistic.dreamjournalai.R
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
        listOf(
            colorResource(R.color.white).copy(alpha = .8f).toArgb(),
        )

    Column(Modifier.fillMaxSize()) {
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
                sizeConstraint = Axis.SizeConstraint.TextWidth("False Awakening.")
            }

            val listColumn =
                currentChartStyle.columnChart.columns.mapIndexed { index, originalComponent ->
                    LineComponent(
                        color = barColors[index % barColors.size],
                        thicknessDp = 8f,
                        shape = originalComponent.shape,
                        dynamicShader = originalComponent.dynamicShader,
                        margins = originalComponent.margins,
                        strokeWidthDp = originalComponent.strokeWidthDp,
                        strokeColor = originalComponent.strokeColor
                    )
                }

            Box(
                modifier = Modifier
                    .padding(8.dp, 32.dp, 8.dp, 4.dp)
                    .imePadding()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        colorResource(id = R.color.light_black).copy(alpha = 0.8f)
                    )
                    .fillMaxWidth()
            ) {
                Chart(
                    chart = columnChart(
                        columns = listColumn,
                        axisValuesOverrider = AxisValuesOverrider.fixed(
                            minY = 0f,
                            maxY = entries.maxOf {
                                if (it.y % 2 == 0f) it.y else it.y + 1f
                            }
                        )
                    ),
                    chartModelProducer = chartEntryModelProducer,
                    //max 6 y axis labels
                    startAxis = rememberStartAxis().apply {
                        itemPlacer =
                            AxisItemPlacer.Vertical.default(
                                5,
                            )
                    },
                    bottomAxis = bottomAxis,
                    marker = MarkerComponent(
                        guideline = null,
                        indicator = null,
                        label = TextComponent.Builder().build(),
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                        .height(350.dp),
                    isZoomEnabled = false,
                )
            }
        }
    }
}
