package org.ballistic.dreamjournalai.dream_statistics.presentation.components

import android.graphics.Typeface
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_statistics.presentation.viewmodel.DreamStatisticScreenState


@Composable
fun TopSixDreamWordPieChart(
    dreamStatisticScreenState: DreamStatisticScreenState
) {
    if (dreamStatisticScreenState.topSixWordsInDreams.isEmpty()) {
        return
    }
    Column(
        modifier = Modifier
            .padding(12.dp, 0.dp, 12.dp, 0.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(id = R.color.light_black).copy(alpha = 0.8f))
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Top 6 Words in Dreams",
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                color = colorResource(id = R.color.white)
            ).copy(fontWeight = FontWeight.Normal),
        )

        val listOfColor = listOf(
            Color(0xFFEF5350), // Red
            Color(0xFFAB47BC), // Purple
            Color(0xFF42A5F5), // Blue
            Color(0xFFFFA726), // Orange
            Color(0xFF26A69A), // Teal
            Color(0xFF66BB6A)  // Light Green
        )


        val donutChartData = PieChartData(
            slices = dreamStatisticScreenState.topSixWordsInDreams.entries
                .distinctBy { it.key } // Ensure unique entries based on the key
                .mapIndexed { index, entry ->
                    PieChartData.Slice(
                        label = "${entry.key.word}: ${entry.value}",
                        value = entry.value.toFloat(),
                        color = listOfColor[index % listOfColor.size],
                        sliceDescription = { "Word: ${entry.key.word}, Total: ${entry.value}" }
                    )
                },
            plotType = PlotType.Donut
        )

        val donutChartConfig = PieChartConfig(
            backgroundColor = Color.Transparent,
            strokeWidth = 100f,
            activeSliceAlpha = .9f,
            isAnimationEnable = true,
            labelVisible = true,
            labelColor = colorResource(id = R.color.white),
            labelTypeface = Typeface.DEFAULT_BOLD,
            isEllipsizeEnabled = true,
            labelFontSize = 24.sp, // Increased font size for better visibility
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .aspectRatio(1f)
                .background(Color.Transparent)
        ) {
            DonutPieChart(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(Color.Transparent),
                donutChartData,
                donutChartConfig
            )
        }
        Legend(donutChartData.slices, listOfColor)
    }
}


@Composable
fun Legend(slices: List<PieChartData.Slice>, colors: List<Color>) {
    val slicePairs = slices.windowed(size = 2, step = 2, partialWindows = true)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp, 16.dp, 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        slicePairs.forEach { pair ->
            Row(modifier = Modifier.fillMaxWidth()) {
                pair.forEach { slice ->
                    LegendItem(slice, colors[slices.indexOf(slice)], Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp)) // Space between items
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f)) // Fill space if only one item in row
                }
            }
        }
    }
}

@Composable
fun LegendItem(slice: PieChartData.Slice, color: Color, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(color)
        )
        Spacer(Modifier.width(8.dp)) // Space between color box and text
        Text(
            text = slice.label,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

