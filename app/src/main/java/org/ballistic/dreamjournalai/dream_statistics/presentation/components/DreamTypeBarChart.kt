package org.ballistic.dreamjournalai.dream_statistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
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
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_statistics.presentation.viewmodel.DreamStatisticScreenState
import kotlin.math.ceil

@Composable
fun DreamChartBarChart(
    dreamStatisticScreenState: DreamStatisticScreenState,
) {
    if (dreamStatisticScreenState.dreams.isEmpty()) {
        return
    }
    val dreamTypeLabels =
        listOf("Lucid", "Normal", "Nightmare", "Favorite", "Recurring", "False Awakening")
    val barColors =
        listOf(
            colorResource(R.color.white).copy(alpha = .8f).toArgb(),
        )


    val maxY = mutableIntStateOf(0)
    if (dreamStatisticScreenState.dreams.isNotEmpty()) {
        val entries = listOf(
            entryOf(0, dreamStatisticScreenState.totalLucidDreams),
            entryOf(1, dreamStatisticScreenState.totalNormalDreams),
            entryOf(2, dreamStatisticScreenState.totalNightmares),
            entryOf(3, dreamStatisticScreenState.totalFavoriteDreams),
            entryOf(4, dreamStatisticScreenState.totalRecurringDreams),
            entryOf(5, dreamStatisticScreenState.totalFalseAwakenings),
        )
        val maxValue = entries.maxOf { it.y }

        // Determine the suitable interval based on the maximum value
        val interval = when {
            maxValue <= 6 -> 1   // Interval of 1 for small numbers
            else -> ceil(maxValue / 4).toInt() // Aim for about 4 intervals, adjust as needed
        }


        maxY.value = interval * ceil(maxValue / interval).toInt()


        val yTicks = maxY.value / interval + 1  // +1 to include the tick at 0


        val horizontalAxisValueFormatter =
            AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                dreamTypeLabels.getOrNull(value.toInt()) ?: ""
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
                .padding(start = 12.dp, end = 12.dp, bottom = 40.dp)
                .imePadding()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    colorResource(id = R.color.light_black).copy(alpha = 0.8f)
                )
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Dream Types",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colorResource(id = R.color.white)
                    ).copy(fontWeight = FontWeight.Normal),
                )
                Chart(
                    chart = columnChart(
                        columns = listColumn,
                        axisValuesOverrider = remember {
                            object : AxisValuesOverrider<ChartEntryModel> {
                                override fun getMaxY(model: ChartEntryModel) =
                                    maxY.value.toFloat()
                            }
                        }
                    ),
                    chartModelProducer = ChartEntryModelProducer(remember {
                        entries
                    }),
                    //max 6 y axis labels
                    startAxis = rememberStartAxis(itemPlacer = remember {
                        AxisItemPlacer.Vertical.default(
                            maxItemCount = yTicks
                        )
                    }),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = horizontalAxisValueFormatter,
                        labelRotationDegrees = 90f,
                        sizeConstraint = Axis.SizeConstraint.TextWidth("False Awakening.")
                    ),
                    marker = MarkerComponent(
                        guideline = null,
                        indicator = null,
                        label = TextComponent.Builder().build(),
                    ),
                    modifier = Modifier
                        .padding(16.dp, 0.dp, 16.dp, 16.dp)
                        .height(350.dp),
                    isZoomEnabled = false,
                )
            }
        }
    }
}
