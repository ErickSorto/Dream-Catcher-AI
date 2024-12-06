package org.ballistic.dreamjournalai.dream_notifications.data.worker

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.ballistic.dreamjournalai.DreamJournalAIApp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.MainActivity


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

        // Get current local time using kotlinx.datetime
        val nowTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
        val currentTimeMinutes = nowTime.hour * 60 + nowTime.minute
        val startMinutes = startTime.toInt()
        val endMinutes = endTime.toInt()

        Log.d("RealityCheckNotificationWorker", "Current time: ${formatTimeHHmmss(nowTime)}, current time minutes: $currentTimeMinutes, start time: $startMinutes minutes, end time: $endMinutes minutes")

        val isWithinTimeRange = if (startMinutes <= endMinutes) {
            currentTimeMinutes in startMinutes..endMinutes
        } else {
            // Handles wrap-around (e.g., start at 22:00 and end at 02:00)
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

        // Create an intent that will be fired when the user taps the notification
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(applicationContext, DreamJournalAIApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.dream_journal_icon_vector)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(Notification.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)  // Set the intent to be fired when the notification is tapped

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())

        Log.d("RealityCheckNotificationWorker", "Notification displayed at: ${formatTimeHHmmss(nowTime)}")
        return Result.success()
    }

    /**
     * Formats a [LocalTime] as "HH:mm:ss".
     */
    private fun formatTimeHHmmss(localTime: LocalTime): String {
        val hour = localTime.hour.toString().padStart(2, '0')
        val minute = localTime.minute.toString().padStart(2, '0')
        val second = localTime.second.toString().padStart(2, '0')
        return "$hour:$minute:$second"
    }
}
