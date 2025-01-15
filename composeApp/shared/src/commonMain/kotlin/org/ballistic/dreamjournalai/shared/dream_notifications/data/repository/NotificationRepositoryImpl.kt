package org.ballistic.dreamjournalai.shared.dream_notifications.data.repository

import android.content.Context
import android.util.Log
import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationRepository
import androidx.work.*
import dev.gitlive.firebase.storage.Data
import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.datetime.TimeZone
import org.ballistic.dreamjournalai.core.util.addMinutes
import org.ballistic.dreamjournalai.dream_notifications.data.worker.DreamJournalNotificationWorker
import org.ballistic.dreamjournalai.dream_notifications.data.worker.RealityCheckNotificationWorker

class NotificationRepositoryImpl @Inject constructor(
    private val context: Context
) : NotificationRepository {

    override fun scheduleDailyReminder(timeInMillis: Long) {
        val data = Data.Builder()
            .putString("title", "Reminder")
            .putString("message", "Log your dreams! 😊🌙")
            .build()

        val initialDelay = calculateInitialDelay(timeInMillis)

        val notificationWork = PeriodicWorkRequestBuilder<DreamJournalNotificationWorker>(1, TimeUnit.DAYS)
            .setInputData(data)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("DailyDreamJournalReminderTag")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyDreamJournalReminder",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            notificationWork
        )

        Log.d("NotificationRepositoryImpl", "Scheduled daily reminder with initial delay: ${formatDuration(initialDelay)}")
    }

    private fun calculateInitialDelay(timeInMillis: Long): Long {
        val currentTimeInMillis = Clock.System.now().toEpochMilliseconds()
        var delay = timeInMillis - currentTimeInMillis

        Log.d("NotificationRepositoryImpl", "Current time: ${formatMillisToDateTime(currentTimeInMillis)}")
        Log.d("NotificationRepositoryImpl", "Scheduled time: ${formatMillisToDateTime(timeInMillis)}")
        Log.d("NotificationRepositoryImpl", "Initial delay: ${formatDuration(delay)}")

        if (delay <= 0) {
            delay += TimeUnit.DAYS.toMillis(1)
            Log.d("NotificationRepositoryImpl", "Adjusted delay for next day: ${formatDuration(delay)}")
        }

        return delay
    }

    override fun scheduleLucidityNotification(frequency: Int, startTime: Float, endTime: Float) {
        val intervalMillis = if (frequency == 0) {
            TimeUnit.MINUTES.toMillis(30)
        } else {
            TimeUnit.HOURS.toMillis(frequency.toLong())
        }

        val data = Data.Builder()
            .putString("title", "Reality Check")
            .putString("message", "Are you dreaming? 🌙")
            .putLong("startTime", startTime.toLong())
            .putLong("endTime", endTime.toLong())
            .build()

        val initialDelay = calculateInitialDelayForLucidity(startTime.toInt(), intervalMillis)

        val notificationWork = PeriodicWorkRequestBuilder<RealityCheckNotificationWorker>(
            intervalMillis, TimeUnit.MILLISECONDS
        )
            .setInputData(data)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("LucidityNotificationTag")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "LucidityNotification",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            notificationWork
        )

        Log.d(
            "NotificationRepositoryImpl",
            "Scheduled lucidity notification with interval: ${formatDuration(intervalMillis)}, start time: ${formatTime(startTime)}, end time: ${formatTime(endTime)}"
        )
    }

    private fun calculateInitialDelayForLucidity(startMinutes: Int, intervalMillis: Long): Long {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val startHour = startMinutes / 60
        val startMin = startMinutes % 60
        val startLocalTime = LocalTime(startHour, startMin)

        var nextNotificationTime = LocalDateTime(
            year = now.year,
            month = now.month,
            dayOfMonth = now.dayOfMonth,
            hour = startLocalTime.hour,
            minute = startLocalTime.minute,
            second = 0,
            nanosecond = 0
        )

        // If now is after the start time, move to the next interval time slot
        if (now > nextNotificationTime) {
            val additionalMinutes = (intervalMillis / 60000).toInt()
            nextNotificationTime = addMinutes(nextNotificationTime, additionalMinutes)
        }

        val nowInstant = now.toInstant(TimeZone.currentSystemDefault())
        val nextNotificationInstant = nextNotificationTime.toInstant(TimeZone.currentSystemDefault())

        val delayMillis = (nextNotificationInstant - nowInstant).inWholeMilliseconds

        Log.d("NotificationRepositoryImpl", "Initial delay for lucidity notification: ${formatDuration(delayMillis)}")
        return delayMillis
    }

    private fun formatTime(minutes: Float): String {
        val totalMinutes = minutes.toInt()
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return if (hours == 24) "24:00" else "%02d:%02d".format(hours % 24, mins)
    }

    private fun formatMillisToDateTime(millis: Long): String {
        val dateTime = Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        // Manual formatting of "yyyy-MM-dd HH:mm:ss"
        val year = dateTime.year
        val month = dateTime.monthNumber
        val day = dateTime.dayOfMonth
        val hour = dateTime.hour
        val minute = dateTime.minute
        val second = dateTime.second
        return String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second)
    }

    private fun formatDuration(durationInMillis: Long): String {
        val duration: Duration = durationInMillis.milliseconds
        val hours = duration.inWholeHours
        val minutes = duration.inWholeMinutes % 60
        val seconds = duration.inWholeSeconds % 60
        return String.format("%02d hours %02d minutes and %02d seconds", hours, minutes, seconds)
    }
}
