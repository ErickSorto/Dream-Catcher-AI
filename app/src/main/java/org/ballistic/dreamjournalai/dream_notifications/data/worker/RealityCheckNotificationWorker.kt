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

class RealityCheckNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("RealityCheckNotificationWorker", "Executing RealityCheckNotificationWorker")

        val title = inputData.getString("title") ?: "Reality Check"
        val message = inputData.getString("message") ?: "Are you dreaming?"
        val startTime = inputData.getLong("startTime", 0L)
        val endTime = inputData.getLong("endTime", 1440L)  // default to end of day if not set

        val currentTimeMinutes = LocalTime.now().toSecondOfDay() / 60
        val startMinutes = startTime.toInt()
        val endMinutes = endTime.toInt()

        Log.d("RealityCheckNotificationWorker", "Current time: ${LocalTime.now()}, current time minutes: $currentTimeMinutes, start time: $startMinutes minutes, end time: $endMinutes minutes")

        val isWithinTimeRange = if (startMinutes <= endMinutes) {
            currentTimeMinutes in startMinutes..endMinutes
        } else {
            currentTimeMinutes in startMinutes..1439 || currentTimeMinutes in 0..endMinutes
        }

        Log.d("RealityCheckNotificationWorker", "Is within time range: $isWithinTimeRange")

        if (!isWithinTimeRange) {
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

        Log.d("RealityCheckNotificationWorker", "Notification displayed at: ${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))}")
        return Result.success()
    }
}