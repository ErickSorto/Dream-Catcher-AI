package org.ballistic.dreamjournalai.dream_notifications.domain.usecases

import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationRepository
import javax.inject.Inject

class ScheduleLucidityNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(frequency: Int, startTime: Float, endTime: Float) {
        repository.scheduleLucidityNotification(frequency, startTime, endTime)
    }
}
