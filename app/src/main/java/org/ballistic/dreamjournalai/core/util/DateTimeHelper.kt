package org.ballistic.dreamjournalai.core.util

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.number

// Fixed map for month abbreviations to month numbers (English)
private val MONTH_ABBR_MAP = mapOf(
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

// Fixed list for full day names (Monday to Sunday)
private val DAY_OF_WEEK_FULL_NAMES = listOf(
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
    "Sunday"
)

/**
 * Parses a date string in the format "MMM d, yyyy".
 * Example: "Dec 5, 2024"
 *
 * @param dateStr The date string to parse.
 * @return The corresponding LocalDate.
 * @throws IllegalArgumentException If the date string is invalid.
 */
fun parseCustomDate(dateStr: String): LocalDate {
    // Regex to match "MMM d, yyyy"
    val regex = Regex("""([A-Za-z]{3})\s+(\d{1,2}),\s+(\d{4})""")
    val matchResult = regex.matchEntire(dateStr)
        ?: throw IllegalArgumentException("Date string does not match format MMM d, yyyy")

    val (monthStr, dayStr, yearStr) = matchResult.destructured

    val month = MONTH_ABBR_MAP[monthStr]
        ?: throw IllegalArgumentException("Invalid month abbreviation: $monthStr")

    val day = dayStr.toIntOrNull()
        ?: throw IllegalArgumentException("Invalid day: $dayStr")

    val year = yearStr.toIntOrNull()
        ?: throw IllegalArgumentException("Invalid year: $yearStr")

    return LocalDate(year, month, day)
}

/**
 * Formats a LocalDate to "MMM d, yyyy" format.
 * Example: "Dec 5, 2024"
 *
 * @param date The LocalDate to format.
 * @return The formatted date string.
 */
fun formatCustomDate(date: LocalDate): String {
    val monthAbbr = MONTH_ABBR_MAP.entries.find { it.value == date.month.number }?.key
        ?: throw IllegalArgumentException("Invalid month number: ${date.month.number}")

    return "$monthAbbr ${date.dayOfMonth}, ${date.year}"
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
    // DayOfWeek ordinal: Monday = 0, Sunday = 6
    return DAY_OF_WEEK_FULL_NAMES[dayOfWeek.ordinal]
}