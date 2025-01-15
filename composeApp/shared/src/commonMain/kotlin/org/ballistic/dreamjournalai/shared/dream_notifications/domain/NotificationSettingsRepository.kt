package org.ballistic.dreamjournalai.shared.dream_notifications.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalTime

interface NotificationSettingsRepository {
    val realityCheckReminderFlow: Flow<Boolean>
    val dreamJournalReminderFlow: Flow<Boolean>
    val lucidityFrequencyFlow: Flow<Int>
    val reminderTimeFlow: Flow<LocalTime>    // or a string you parse
    val timeRangeFlow: Flow<ClosedFloatingPointRange<Float>>

    suspend fun updateRealityCheckReminder(value: Boolean)
    suspend fun updateDreamJournalReminder(value: Boolean)
    suspend fun updateLucidityFrequency(value: Int)
    suspend fun updateReminderTime(value: LocalTime)
    suspend fun updateTimeRange(start: Float, end: Float)
}