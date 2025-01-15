package org.ballistic.dreamjournalai.shared.dream_notifications.domain.usecases

import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationRepository

class ScheduleDailyReminderUseCase (
    private val repository: NotificationRepository
) {
    operator fun invoke(timeInMillis: Long) {
        repository.scheduleDailyReminder(timeInMillis)
    }
}