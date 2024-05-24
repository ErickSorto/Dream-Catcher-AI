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
            .putString("title", "Dream Journal Reminder")
            .putString("message", "Time to log your dreams!")
            .build()

        val initialDelay = calculateInitialDelay(timeInMillis)

        val notificationWork = PeriodicWorkRequestBuilder<DreamJournalNotificationWorker>(1, TimeUnit.DAYS)
            .setInputData(data)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyDreamJournalReminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationWork
        )

        Log.d("NotificationRepositoryImpl", "Scheduled daily reminder with initial delay: ${formatDuration(initialDelay)}")
    }

    override fun scheduleLucidityNotification(frequency: Int, startTime: Float, endTime: Float) {
        val intervalMillis = if (frequency == 1) TimeUnit.MINUTES.toMillis(30) else TimeUnit.HOURS.toMillis(frequency.toLong())
        val data = Data.Builder()
            .putString("title", "Lucidity Reminder")
            .putString("message", "Practice lucidity techniques")
            .putLong("startTime", startTime.toLong())
            .putLong("endTime", endTime.toLong())
            .build()

        val initialDelay = calculateInitialDelayForLucidity(startTime.toLong())

        val notificationWork = PeriodicWorkRequestBuilder<RealityCheckNotificationWorker>(intervalMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "Lucidity Notification",
            ExistingPeriodicWorkPolicy.UPDATE,
            notificationWork
        )

        Log.d("NotificationRepositoryImpl", "Scheduled lucidity notification with interval: ${formatDuration(intervalMillis)}, start time: ${formatTime(startTime)}, end time: ${formatTime(endTime)}")
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

    private fun calculateInitialDelayForLucidity(startMinutes: Long): Long {
        val now = LocalDateTime.now()
        val startLocalTime = LocalTime.of((startMinutes / 60).toInt(), (startMinutes % 60).toInt())

        var nextNotificationTime = now.withHour(startLocalTime.hour).withMinute(startLocalTime.minute)

        if (now.isAfter(nextNotificationTime)) {
            nextNotificationTime = nextNotificationTime.plusDays(1)
        }

        return Duration.between(now, nextNotificationTime).toMillis()
    }

    private fun formatTime(minutes: Float): String {
        val totalMinutes = minutes.toInt()
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return String.format("%02d:%02d", hours, mins)
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

