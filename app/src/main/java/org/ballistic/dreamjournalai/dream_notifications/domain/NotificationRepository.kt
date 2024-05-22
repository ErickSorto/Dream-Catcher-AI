package org.ballistic.dreamjournalai.dream_notifications.domain

interface NotificationRepository {
    fun scheduleDailyReminder(timeInMillis: Long)
    fun scheduleLucidityNotification(frequency: Int, intervalInMillis: Long)
}