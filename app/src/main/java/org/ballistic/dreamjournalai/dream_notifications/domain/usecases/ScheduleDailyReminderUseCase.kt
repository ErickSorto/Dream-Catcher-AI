package org.ballistic.dreamjournalai.dream_notifications.domain.usecases

import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationRepository
import javax.inject.Inject

class ScheduleDailyReminderUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(timeInMillis: Long) {
        repository.scheduleDailyReminder(timeInMillis)
    }
}