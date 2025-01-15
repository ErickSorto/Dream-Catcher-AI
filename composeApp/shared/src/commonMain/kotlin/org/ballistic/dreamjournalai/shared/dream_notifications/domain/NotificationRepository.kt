package org.ballistic.dreamjournalai.shared.dream_notifications.domain

interface NotificationRepository {
    fun scheduleDailyReminder(timeInMillis: Long)
    fun scheduleLucidityNotification(frequency: Int, startTime: Float, endTime: Float)
}