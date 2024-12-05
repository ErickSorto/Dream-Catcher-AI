package org.ballistic.dreamjournalai.dream_journal_list.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.ballistic.dreamjournalai.R


@Composable
fun DateHeader(dateString: String) {
    val displayString = remember(dateString) {
        // Define the system's default time zone
        val timeZone = TimeZone.currentSystemDefault()

        // Get today's date
        val todayDate = Clock.System.todayIn(timeZone)

        // Get yesterday's date
        val yesterdayDate = todayDate.minus(DatePeriod(days = 1))

        // Calculate the start of the current week (assuming week starts on Sunday)
        val thisWeekStartDate = getStartOfWeek(todayDate, DayOfWeek.SUNDAY)

        // Custom parser for "MMM d, yyyy" format
        val fixedDateString = dateString.replaceFirstChar {
            if (it.isLowerCase()) it.uppercaseChar() else it
        }

        val date = try {
            parseCustomDate(fixedDateString)
        } catch (e: IllegalArgumentException) {
            null
        }

        when {
            date == todayDate -> "Today"
            date == yesterdayDate -> "Yesterday"
            date != null && date > thisWeekStartDate -> {
                // Get the full display name of the day of the week
                getDayOfWeekDisplayName(date.dayOfWeek)
            }
            else -> dateString
        }
    }

    Box(
        modifier = Modifier
            .padding(start = 12.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
            .background(
                color = colorResource(id = R.color.light_black).copy(alpha = 0.8f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
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

/**
 * Parses a date string in the format "MMM d, yyyy".
 * Example: "Dec 5, 2024"
 *
 * @param dateStr The date string to parse.
 * @return The corresponding LocalDate.
 * @throws IllegalArgumentException If the date string is invalid.
 */
fun parseCustomDate(dateStr: String): LocalDate {
    // Define a fixed map of month abbreviations to month numbers (English)
    val monthMap = mapOf(
        "Jan" to 1,
        "Feb" to 2,
        "Mar" to 3,
        "Apr" to 4,
        "May" to 5,
        "Jun" to 6,
        "Jul" to 7,
        "Aug" to 8,
        "Sep" to 9,
        "Oct" to 10,
        "Nov" to 11,
        "Dec" to 12
    )

    // Regex to match "MMM d, yyyy"
    val regex = Regex("""([A-Za-z]{3})\s+(\d{1,2}),\s+(\d{4})""")
    val matchResult = regex.matchEntire(dateStr)
        ?: throw IllegalArgumentException("Date string does not match format MMM d, yyyy")

    val (monthStr, dayStr, yearStr) = matchResult.destructured

    val month = monthMap[monthStr]
        ?: throw IllegalArgumentException("Invalid month abbreviation: $monthStr")

    val day = dayStr.toIntOrNull()
        ?: throw IllegalArgumentException("Invalid day: $dayStr")

    val year = yearStr.toIntOrNull()
        ?: throw IllegalArgumentException("Invalid year: $yearStr")

    return LocalDate(year, month, day)
}

/**
 * Returns the start of the week for the given date, based on the specified first day of the week.
 *
 * @param date The date for which to find the start of the week.
 * @param firstDayOfWeek The first day of the week (e.g., Sunday, Monday).
 * @return The LocalDate representing the start of the week.
 */
fun getStartOfWeek(date: LocalDate, firstDayOfWeek: DayOfWeek): LocalDate {
    val currentDayOfWeek = date.dayOfWeek
    val daysToSubtract = (currentDayOfWeek.ordinal - firstDayOfWeek.ordinal + 7) % 7
    return date.minus(DatePeriod(days = daysToSubtract))
}

/**
 * Returns the full display name of the given DayOfWeek.
 *
 * @param dayOfWeek The DayOfWeek enum.
 * @return The full name of the day (e.g., "Monday").
 */
fun getDayOfWeekDisplayName(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "Monday"
        DayOfWeek.TUESDAY -> "Tuesday"
        DayOfWeek.WEDNESDAY -> "Wednesday"
        DayOfWeek.THURSDAY -> "Thursday"
        DayOfWeek.FRIDAY -> "Friday"
        DayOfWeek.SATURDAY -> "Saturday"
        DayOfWeek.SUNDAY -> "Sunday"
    }
}