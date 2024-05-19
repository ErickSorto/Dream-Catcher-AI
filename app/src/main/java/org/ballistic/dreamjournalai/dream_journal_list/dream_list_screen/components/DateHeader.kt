package org.ballistic.dreamjournalai.dream_journal_list.dream_list_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
            .padding(12.dp, 8.dp, 8.dp, 8.dp)
            .background(
                colorResource(id = R.color.light_black).copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = displayString,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(6.dp)
                .padding(horizontal = 8.dp),
            color = colorResource(id = R.color.white),
            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
        )
    }
}