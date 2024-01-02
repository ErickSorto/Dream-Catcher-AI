package org.ballistic.dreamjournalai.dream_statistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.maxkeppeker.sheets.core.views.Grid
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_statistics.presentation.viewmodel.DreamStatisticScreenState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopFiveDreamWordPieChart(
    dreamStatisticScreenState: DreamStatisticScreenState
) {
    if (dreamStatisticScreenState.topFiveWordsInDreams.isNotEmpty()) {
        val listOfColor = listOf(
            Color(0xFFEF5350), // Red
            Color(0xFFAB47BC), // Purple
            Color(0xFF42A5F5), // Blue
            Color(0xFFFFA726), // Orange
            Color(0xFF26A69A), // Teal
            Color(0xFF66BB6A)  // Light Green
        )


        val donutChartData = PieChartData(
            slices = dreamStatisticScreenState.topFiveWordsInDreams.entries
                .distinctBy { it.key } // Ensure unique entries based on the key
                .mapIndexed { index, entry ->
                    PieChartData.Slice(
                        label = "${entry.key.word} (Total: ${entry.value})",
                        value = entry.value.toFloat(),
                        color = listOfColor[index % listOfColor.size],
                        sliceDescription = { "Word: ${entry.key.word}, Total: ${entry.value}" }
                    )
                },
            plotType = PlotType.Donut
        )

        val donutChartConfig = PieChartConfig(
            backgroundColor = colorResource(id = R.color.white).copy(alpha = 0.8f),
            strokeWidth = 120f,
            activeSliceAlpha = .9f,
            isAnimationEnable = true,
            labelVisible = true,
            labelColor = Color.Black,
            isEllipsizeEnabled = true,
            labelFontSize = 16.sp, // Increased font size for better visibility
            chartPadding = 16
        )

        Column(
            modifier = Modifier
                .fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DonutPieChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                donutChartData,
                donutChartConfig
            )
            Legend(donutChartData.slices, listOfColor)
        }
    }
}

@Composable
fun Legend(slices: List<PieChartData.Slice>, colors: List<Color>) {
        Grid(
            columns = 2,
            columnSpacing = 8.dp,
            rowSpacing = 8.dp,
            items = slices,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ){
            LegendItem(it, colors[slices.indexOf(it)])
        }
}

@Composable
fun LegendItem(slice: PieChartData.Slice, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(color)
        )
        Spacer(Modifier.width(8.dp)) // Space between color box and text
        Text(
            text = slice.label,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
