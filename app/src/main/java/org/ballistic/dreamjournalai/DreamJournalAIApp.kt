package org.ballistic.dreamjournalai

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.Configuration
import androidx.work.WorkManager
import org.ballistic.dreamjournalai.di.adMobModule
import org.ballistic.dreamjournalai.di.appModule
import org.ballistic.dreamjournalai.di.billingModule
import org.ballistic.dreamjournalai.di.notificationModule
import org.ballistic.dreamjournalai.di.signInModule
import org.ballistic.dreamjournalai.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class DreamJournalAIApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeWorkManager()
        initializeKoin() // Initialize Koin here
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun initializeWorkManager() {
        val configuration = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
        WorkManager.initialize(this, configuration)
    }

    private fun initializeKoin() {
        startKoin {
            androidLogger()
            androidContext(this@DreamJournalAIApp)
            modules(
                listOf(
                    appModule,
                    signInModule,
                    billingModule,
                    notificationModule,
                    adMobModule,
                    viewModelModule
                )
            )
        }
    }

    companion object {
        const val CHANNEL_ID = "dream_journal_channel"
    }
}