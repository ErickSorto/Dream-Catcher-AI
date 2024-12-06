package org.ballistic.dreamjournalai.core.util

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
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

/**
 * Formats a LocalDate to "MMM d, yyyy" format.
 * Example: "Jun 3, 2023"
 *
 * @param localDate The LocalDate to format.
 * @return The formatted date string.
 */
fun formatLocalDate(localDate: LocalDate): String {
    // We map the month ordinal to a short month name.
    val shortMonthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val monthName = shortMonthNames[localDate.month.ordinal]
    val day = localDate.dayOfMonth
    val year = localDate.year
    return "$monthName $day, $year"
}

/**
 * Formats a LocalTime to "h:mm a" format.
 * Example: "7:00 AM"
 *
 * @param localTime The LocalTime to format.
 * @return The formatted time string.
 */
fun formatLocalTime(localTime: LocalTime): String {
    val hour = localTime.hour % 12
    val displayHour = if (hour == 0) 12 else hour
    val minute = localTime.minute
    val period = if (localTime.hour < 12) "AM" else "PM"
    return "$displayHour:${minute.toString().padStart(2, '0')} $period"
}

fun parseFormattedTime(timeStr: String): LocalTime {
    val regex = Regex("""(\d{1,2}):(\d{2})\s*(AM|PM)""", RegexOption.IGNORE_CASE)
    val matchResult = regex.matchEntire(timeStr.trim())
        ?: throw IllegalArgumentException("Invalid time format: $timeStr")
    val (hourStr, minuteStr, periodStr) = matchResult.destructured
    var hour = hourStr.toInt()
    val minute = minuteStr.toInt()
    val period = periodStr.uppercase()

    if (period == "PM" && hour != 12) {
        hour += 12
    }
    if (period == "AM" && hour == 12) {
        hour = 0
    }
    return LocalTime(hour, minute)
}

/**
 * Determines if a given year is a leap year.
 *
 * @param year The year to check.
 * @return `true` if the year is a leap year, `false` otherwise.
 */
fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

/**
 * Returns the number of days in a given month for a specific year.
 *
 * @param year The year.
 * @param month The month (1-12).
 * @return The number of days in the month.
 * @throws IllegalArgumentException If the month is not between 1 and 12.
 */
fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> throw IllegalArgumentException("Invalid month: $month")
    }
}

/**
 * Adds one day to the provided [LocalDateTime] instance.
 *
 * @param dateTime The original [LocalDateTime].
 * @return A new [LocalDateTime] instance with one day added.
 */
fun addOneDay(dateTime: LocalDateTime): LocalDateTime {
    var year = dateTime.year
    var month = dateTime.monthNumber
    var day = dateTime.dayOfMonth + 1

    val daysInMonth = getDaysInMonth(year, month)

    if (day > daysInMonth) {
        day = 1
        month += 1
        if (month > 12) {
            month = 1
            year += 1
        }
    }

    return LocalDateTime(year, month, day, dateTime.hour, dateTime.minute, dateTime.second, dateTime.nanosecond)
}
