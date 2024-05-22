package org.ballistic.dreamjournalai.dream_notifications.domain.usecases

import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationRepository
import javax.inject.Inject

class ScheduleLucidityNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(frequency: Int, intervalInMillis: Long) {
        repository.scheduleLucidityNotification(frequency, intervalInMillis)
    }
}