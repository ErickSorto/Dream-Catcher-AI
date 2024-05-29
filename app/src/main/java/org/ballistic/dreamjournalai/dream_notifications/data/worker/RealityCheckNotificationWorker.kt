package org.ballistic.dreamjournalai.dream_notifications.data.worker

import android.Manifest
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

class RealityCheckNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("RealityCheckNotificationWorker", "Executing RealityCheckNotificationWorker")

        val title = inputData.getString("title") ?: "Lucidity Reminder"
        val message = inputData.getString("message") ?: "Practice lucidity techniques"
        val startTime = inputData.getLong("startTime", 0L)
        val endTime = inputData.getLong("endTime", 1440L)  // default to end of day if not set

        val currentTimeMinutes = LocalTime.now().toSecondOfDay() / 60
        val startMinutes = startTime.toInt()
        val endMinutes = endTime.toInt()

        Log.d("RealityCheckNotificationWorker", "Current time minutes: $currentTimeMinutes, start time minutes: $startMinutes, end time minutes: $endMinutes")

        if (currentTimeMinutes < startMinutes || currentTimeMinutes > endMinutes) {
            Log.d("RealityCheckNotificationWorker", "Current time is outside the allowed range, skipping notification")
            return Result.success()
        }

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("RealityCheckNotificationWorker", "Permission not granted")
            return Result.failure()
        }

        val builder = NotificationCompat.Builder(applicationContext, DreamJournalAiApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ai_vector_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }

        Log.d("RealityCheckNotificationWorker", "Notification displayed at: ${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))}")
        return Result.success()
    }
}
