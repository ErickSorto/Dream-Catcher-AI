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

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("NotificationWorker", "Executing NotificationWorker")

        val title = inputData.getString("title") ?: "Dream Journal"
        val message = inputData.getString("message") ?: "Don't forget to log your dreams!"

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("NotificationWorker", "Permission not granted")
            return Result.failure()
        }

        val builder = NotificationCompat.Builder(applicationContext, DreamJournalAiApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ai_vector_icon)  // Update with your actual icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }

        Log.d("NotificationWorker", "Notification displayed")
        return Result.success()
    }
}