package org.ballistic.dreamjournalai.dream_notifications.data.local

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import org.ballistic.dreamjournalai.DreamJournalAiApp
import org.ballistic.dreamjournalai.R
import kotlin.random.Random

class NotificationHandler(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showSimpleNotification() {
        val notification = NotificationCompat.Builder(context, DreamJournalAiApp.CHANNEL_ID) // Use the correct channel ID
            .setContentTitle("Simple Notification")
            .setContentText("Message or text with notification")
            .setSmallIcon(R.drawable.dream_word_vector_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}