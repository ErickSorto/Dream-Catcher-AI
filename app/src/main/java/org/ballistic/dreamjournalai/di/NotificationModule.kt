package org.ballistic.dreamjournalai.di

import android.content.Context


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.ballistic.dreamjournalai.dream_notifications.data.local.NotificationPreferences
import org.ballistic.dreamjournalai.dream_notifications.data.repository.NotificationRepositoryImpl
import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationPreferences(
        @ApplicationContext context: Context
    ): NotificationPreferences {
        return NotificationPreferences(context)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        @ApplicationContext context: Context
    ): NotificationRepository {
        return NotificationRepositoryImpl(context)
    }
}