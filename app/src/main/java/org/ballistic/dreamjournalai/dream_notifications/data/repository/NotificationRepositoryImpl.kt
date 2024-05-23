package org.ballistic.dreamjournalai.dream_notifications.data.repository

import android.content.Context
import android.util.Log
import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationRepository
import androidx.work.*
import org.ballistic.dreamjournalai.dream_notifications.data.worker.NotificationWorker
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

        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInputData(data)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyDreamJournalReminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            notificationWork
        )

        Log.d("NotificationRepositoryImpl", "Scheduled daily reminder with initial delay: $initialDelay ms")
    }

    private fun calculateInitialDelay(timeInMillis: Long): Long {
        val currentTimeInMillis = System.currentTimeMillis()
        var delay = timeInMillis - currentTimeInMillis

        Log.d("NotificationRepositoryImpl", "Current time: $currentTimeInMillis")
        Log.d("NotificationRepositoryImpl", "Scheduled time: $timeInMillis")
        Log.d("NotificationRepositoryImpl", "Initial delay (ms): $delay")

        if (delay <= 0) {
            delay += TimeUnit.DAYS.toMillis(1)
            Log.d("NotificationRepositoryImpl", "Adjusted delay for next day (ms): $delay")
        }

        return delay
    }

    override fun scheduleLucidityNotification(frequency: Int, intervalInMillis: Long) {
        val data = Data.Builder()
            .putString("title", "Lucidity Reminder")
            .putString("message", "Practice lucidity techniques")
            .build()

        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(intervalInMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "Lucidity Notification",
            ExistingPeriodicWorkPolicy.UPDATE,
            notificationWork
        )

        Log.d("NotificationRepositoryImpl", "Scheduled lucidity notification with interval: $intervalInMillis ms")
    }
}