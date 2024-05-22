package org.ballistic.dreamjournalai.dream_notifications.data.repository

import android.content.Context
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

        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data)
            .setInitialDelay(timeInMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(notificationWork)
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
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationWork
        )
    }
}