package org.ballistic.dreamjournalai.di


import org.ballistic.dreamjournalai.dream_notifications.data.local.NotificationPreferences
import org.ballistic.dreamjournalai.dream_notifications.data.repository.NotificationRepositoryImpl
import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationRepository
import org.ballistic.dreamjournalai.dream_notifications.domain.usecases.ScheduleDailyReminderUseCase
import org.ballistic.dreamjournalai.dream_notifications.domain.usecases.ScheduleLucidityNotificationUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val notificationModule = module {
    singleOf(::NotificationPreferences)
    singleOf(::ScheduleDailyReminderUseCase)
    singleOf(::ScheduleLucidityNotificationUseCase)
    singleOf(::NotificationRepositoryImpl) { bind<NotificationRepository>() }
}