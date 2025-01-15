package org.ballistic.dreamjournalai.shared.dream_statistics.presentation.components

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.Shape
import org.ballistic.dreamjournalai.shared.dream_statistics.presentation.viewmodel.DreamStatisticScreenState
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import kotlin.math.ceil

@Composable
actual fun DreamChartBarChart(dreamStatisticScreenState: DreamStatisticScreenState) {
    if (dreamStatisticScreenState.dreams.isEmpty()) {
        return
    }

    val dreamTypeLabels =
        listOf("Lucid", "Normal", "Nightmare", "Favorite", "Recurring", "False Awakening")
    val labelListKey = ExtraStore.Key<List<String>>()

    // Map data to the dream type labels
    val data = listOf(
        dreamStatisticScreenState.totalLucidDreams,
        dreamStatisticScreenState.totalNormalDreams,
        dreamStatisticScreenState.totalNightmares,
        dreamStatisticScreenState.totalFavoriteDreams,
        dreamStatisticScreenState.totalRecurringDreams,
        dreamStatisticScreenState.totalFalseAwakenings
    )

    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(data) {
        modelProducer.tryRunTransaction {
            columnSeries {
                // Generate a single series with all data points
                series(
                    x = data.indices.map { it.toFloat() }, // Generate x values as indices converted to Float
                    y = data.map { it.toFloat() }           // Convert data points to Float for the y values
                )
            }
            updateExtras {
                it[labelListKey] = dreamTypeLabels
            } // Updating labels for use in the axis
        }
    }

    // Compute the maximum Y value dynamically
    val maxY = remember { mutableIntStateOf(0) }
    val maxValue = data.maxOrNull() ?: 0
    val interval = when {
        maxValue <= 6 -> 1
        else -> ceil(maxValue / 4.0).toInt()
    }
    maxY.intValue = (interval * ceil(maxValue.toDouble() / interval).toInt())

    // Determine the number of ticks on the Y-axis
    val yTicks = maxY.intValue / interval + 1

    // Set up the bottom axis with custom labels from extras
    val bottomAxis = rememberBottomAxis(
        label = rememberAxisLabelComponent(
            color = Color.White,
        ),
        valueFormatter = { x, chartValues, _ ->
            chartValues.model.extraStore[labelListKey][x.toInt()]
        },
        labelRotationDegrees = 90f
    )

    // Remember start axis with custom item placer
    val startAxis = rememberStartAxis(
        itemPlacer = AxisItemPlacer.Vertical.count(
            count = { _ -> yTicks },  // Provide a lambda that returns the number of ticks based on the calculated maxY and interval
            shiftTopLines = true  // Assuming you want to shift the top lines to better align with your chart's visual layout
        ),
        label = rememberTextComponent(
            color = Color.White,
        )
    )

    Box(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, bottom = 40.dp)
            .imePadding()
            .clip(RoundedCornerShape(8.dp))
            .background(
                LightBlack.copy(alpha = 0.8f)
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
                    color = White
                ).copy(fontWeight = FontWeight.Normal),
            )
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                color = Color(0xFFFFFFFF),
                                thickness = 15.dp,
                                shape = remember { Shape.rounded(allPercent = 40) },
                            ),
                        ),
                    ),
                    startAxis = startAxis,
                    bottomAxis = bottomAxis
                ),
                modelProducer = modelProducer.apply {
                    White
                },
                marker = rememberDefaultCartesianMarker(
                    label = rememberTextComponent(
                        color = Color.White,
                    ),
                    indicatorSize = 8.dp,
                    setIndicatorColor = { Color.White },

                    ),
                modifier = Modifier
                    .padding(16.dp)
                    .height(450.dp)
                    .fillMaxWidth()
                    .background(
                        Color.Transparent
                    )
            )
        }
    }
}