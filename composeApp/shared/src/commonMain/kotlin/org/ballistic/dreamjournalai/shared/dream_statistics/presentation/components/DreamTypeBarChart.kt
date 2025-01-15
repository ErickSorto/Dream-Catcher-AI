package org.ballistic.dreamjournalai.shared.dream_statistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.bar.BulletGraphs
import io.github.koalaplot.core.bar.DefaultVerticalBarPlotEntry
import io.github.koalaplot.core.bar.DefaultVerticalBarPosition
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import org.ballistic.dreamjournalai.shared.dream_statistics.presentation.viewmodel.DreamStatisticScreenState
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun DreamChartBarChart(dreamStatisticScreenState: DreamStatisticScreenState) {
    // If there's no dream data, skip
    if (dreamStatisticScreenState.dreams.isEmpty()) return

    // 1) The dream type labels + data:
    val dreamTypeLabels = listOf(
        "Lucid", "Normal", "Nightmare",
        "Favorite", "Recurring", "False Awakening"
    )
    val dataValues = listOf(
        dreamStatisticScreenState.totalLucidDreams,
        dreamStatisticScreenState.totalNormalDreams,
        dreamStatisticScreenState.totalNightmares,
        dreamStatisticScreenState.totalFavoriteDreams,
        dreamStatisticScreenState.totalRecurringDreams,
        dreamStatisticScreenState.totalFalseAwakenings
    )

    // 2) Build Koala Plot vertical bar entries
    //    Each bar is a "DefaultVerticalBarPlotEntry"
    val barEntries: List<DefaultVerticalBarPlotEntry<Float, Float>> = dataValues.mapIndexed { index, count ->
        // xValue = index + 0.5f so bars center on integer ticks
        DefaultVerticalBarPlotEntry(
            x = (index + 0.5f),
            y = DefaultVerticalBarPosition(0f, count.toFloat())
        )
    }

    // 3) Determine axis ranges
    val xRange = 0f..(dreamTypeLabels.size.toFloat()) // e.g. 0..6
    val maxY = (dataValues.maxOrNull() ?: 0).coerceAtLeast(1)
    val yRange = 0f..(maxY.toFloat() + 1f)

    // 4) Colors for each bar
    val barColors = listOf(
        Color(0xFFEF5350),
        Color(0xFFAB47BC),
        Color(0xFF42A5F5),
        Color(0xFFFFA726),
        Color(0xFF26A69A),
        Color(0xFF66BB6A),
    )

    // 5) Compose layout
    Box(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(LightBlack.copy(alpha = 0.8f))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Dream Types",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            // 6) ChartLayout from KoalaPlot
            ChartLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                // 7) XYGraph sets up axis models + styling
                XYGraph(
                    xAxisModel = FloatLinearAxisModel(
                        range = xRange,
                        minimumMajorTickIncrement = 1f
                    ),
                    yAxisModel = FloatLinearAxisModel(
                        range = yRange,
                        minimumMajorTickIncrement = 1f
                    ),
                    // label logic for X axis
                    xAxisLabels = { it: Long ->
                        val i = it.toInt()
                        if (i in dreamTypeLabels.indices) {
                            Text(dreamTypeLabels[i], color = Color.White)
                        }
                    },
                    // label logic for Y axis
                    yAxisLabels = { yVal: Long ->
                        Text(yVal.toInt().toString(), color = Color.White)
                    }
                ) {
                    // 8) The new DSL for vertical bars: "verticalBarPlot"
                    Plot(
                        data = listOf(barEntries),  // We pass a list of series, here just 1
                        barWidth = 0.6f
                    ) {
                        // "bar" is a scope to define each bar
                        bar { seriesIndex, index, entry ->
                            val color = barColors[index % barColors.size]
                            // entry: DefaultVerticalBarPlotEntry
                            // seriesIndex is 0 here since we have only 1 series
                            DefaultVerticalBar(
                                brush = SolidColor(color),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                // optional hover
                            }
                        }
                    }
                }
            }
        }
    }

    BulletGraphs {
        bullet(FloatLinearAxisModel(0f..300f)) {
            label {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = KoalaPlotTheme.sizes.gap)
                ) {
                    Text("Revenue 2005 YTD", textAlign = TextAlign.End)
                    Text("(US $ in thousands)", textAlign = TextAlign.End, style = MaterialTheme.typography.labelSmall)
                }
            }
            axis { labels { Text("${it.toInt()}") } }
            comparativeMeasure(260f)
            featuredMeasureBar(275f)
            ranges(0f, 200f, 250f, 300f)
        }
    }
}

