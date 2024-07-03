package org.ballistic.dreamjournalai.dream_notifications.data.repository

import android.content.Context
import android.util.Log
import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationRepository
import androidx.work.*
import org.ballistic.dreamjournalai.dream_notifications.data.worker.DreamJournalNotificationWorker
import org.ballistic.dreamjournalai.dream_notifications.data.worker.RealityCheckNotificationWorker
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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
        val currentTimeInMillis = System.currentTimeMillis()
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
        val intervalMillis = if (frequency == 0) TimeUnit.MINUTES.toMillis(30) else TimeUnit.HOURS.toMillis(frequency.toLong())
        val data = Data.Builder()
            .putString("title", "Reality Check")
            .putString("message", "Are you dreaming? 🌙")
            .putLong("startTime", startTime.toLong())
            .putLong("endTime", endTime.toLong())
            .build()

        val initialDelay = calculateInitialDelayForLucidity(startTime.toInt(), intervalMillis)

        val notificationWork = PeriodicWorkRequestBuilder<RealityCheckNotificationWorker>(intervalMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("LucidityNotificationTag")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "LucidityNotification",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            notificationWork
        )

        Log.d("NotificationRepositoryImpl", "Scheduled lucidity notification with interval: ${formatDuration(intervalMillis)}, start time: ${formatTime(startTime)}, end time: ${formatTime(endTime)}")
    }

    private fun calculateInitialDelayForLucidity(startMinutes: Int, intervalMillis: Long): Long {
        val now = LocalDateTime.now()
        val startLocalTime = LocalTime.of((startMinutes / 60), (startMinutes % 60))

        // Calculate the next notification time
        var nextNotificationTime = now.withHour(startLocalTime.hour).withMinute(startLocalTime.minute)
        if (now.isAfter(nextNotificationTime)) {
            nextNotificationTime = nextNotificationTime.plusMinutes(intervalMillis / 60000)
        }

        // Ensure the delay does not exceed the frequency interval
        val delayMillis = Duration.between(now, nextNotificationTime).toMillis()
        val adjustedDelayMillis = delayMillis % intervalMillis

        Log.d("NotificationRepositoryImpl", "Initial delay for lucidity notification: ${formatDuration(adjustedDelayMillis)}")
        return adjustedDelayMillis
    }
    private fun formatTime(minutes: Float): String {
        val totalMinutes = minutes.toInt()
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return if (hours == 24) "24:00" else String.format("%02d:%02d", hours % 24, mins)
    }

    private fun formatMillisToDateTime(millis: Long): String {
        val dateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    private fun formatDuration(durationInMillis: Long): String {
        val duration = Duration.ofMillis(durationInMillis)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60
        return String.format("%02d hours %02d minutes and %02d seconds", hours, minutes, seconds)
    }
}
