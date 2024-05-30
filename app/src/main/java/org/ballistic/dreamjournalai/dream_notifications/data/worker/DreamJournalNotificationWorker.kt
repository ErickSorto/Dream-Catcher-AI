package org.ballistic.dreamjournalai.dream_notifications.data.worker

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.ballistic.dreamjournalai.DreamJournalAiApp
import org.ballistic.dreamjournalai.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DreamJournalNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("DreamJournalNotificationWorker", "Executing DreamJournalNotificationWorker")

        val title = inputData.getString("title") ?: "Reminder"
        val message = inputData.getString("message") ?: "Log your dreams! 😊🌙"

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("DreamJournalNotificationWorker", "Permission not granted")
            return Result.failure()
        }

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        val builder = NotificationCompat.Builder(applicationContext, DreamJournalAiApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.dream_journal_icon_vector)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(Notification.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())

        Log.d("DreamJournalNotificationWorker", "Notification displayed at: ${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))}")
        return Result.success()
    }
}
