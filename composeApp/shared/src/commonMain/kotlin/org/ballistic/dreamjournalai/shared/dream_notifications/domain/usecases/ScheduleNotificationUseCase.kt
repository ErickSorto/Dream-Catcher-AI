package org.ballistic.dreamjournalai.shared.dream_notifications.domain.usecases

import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationRepository

class ScheduleLucidityNotificationUseCase (
    private val repository: NotificationRepository
) {
    operator fun invoke(frequency: Int, startTime: Float, endTime: Float) {
        repository.scheduleLucidityNotification(frequency, startTime, endTime)
    }
}
