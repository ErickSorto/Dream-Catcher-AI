package org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun DateHeader(dateString: String) {
    val displayString = remember(dateString) {
        val todayDate = LocalDate.now()
        val yesterdayDate = todayDate.minusDays(1)
        val thisWeekStartDate = todayDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
        val fixedDateString = dateString.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        val date = try {
            LocalDate.parse(fixedDateString, dateFormatter)
        } catch (e: DateTimeParseException) {
            null
        }

        if (date == todayDate) {
            "Today"
        } else if (date == yesterdayDate) {
            "Yesterday"
        } else if (date != null && date.isAfter(thisWeekStartDate)) {
            date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        } else {
            dateString
        }
    }


    Box(
        modifier = Modifier
            .padding(12.dp, 16.dp, 8.dp, 10.dp)
            .background(
                colorResource(id = R.color.dark_blue).copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = displayString,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(8.dp)
                .padding(horizontal = 16.dp),
            color = colorResource(id = R.color.white),
            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
        )
    }
}