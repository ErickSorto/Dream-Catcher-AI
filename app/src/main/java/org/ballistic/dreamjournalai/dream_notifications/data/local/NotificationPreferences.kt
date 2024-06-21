package org.ballistic.dreamjournalai.dream_notifications.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val Context.dataStore by preferencesDataStore("notification_preferences")

class NotificationPreferences(private val context: Context) {

    val realityCheckReminderFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.REALITY_CHECK_REMINDER] ?: false
        }

    val dreamJournalReminderFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DREAM_JOURNAL_REMINDER] ?: false
        }

    val lucidityFrequencyFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LUCIDITY_FREQUENCY] ?: 1
        }

    val reminderTimeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.REMINDER_TIME] ?: LocalTime.of(7, 0).format(DateTimeFormatter.ofPattern("h:mm a"))
        }

    val timeRangeFlow: Flow<ClosedFloatingPointRange<Float>> = context.dataStore.data.map { preferences ->
        val start = preferences[PreferencesKeys.START_TIME] ?: 0f
        val end = preferences[PreferencesKeys.END_TIME] ?: 1440f
        start..end
    }

    suspend fun updateRealityCheckReminder(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REALITY_CHECK_REMINDER] = value
        }
    }

    suspend fun updateDreamJournalReminder(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DREAM_JOURNAL_REMINDER] = value
        }
    }

    suspend fun updateLucidityFrequency(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LUCIDITY_FREQUENCY] = value
        }
    }

    suspend fun updateReminderTime(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_TIME] = value
        }
    }

    suspend fun updateTimeRange(start: Float, end: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.START_TIME] = start
            preferences[PreferencesKeys.END_TIME] = end
        }
    }

    private object PreferencesKeys {
        val REALITY_CHECK_REMINDER = booleanPreferencesKey("reality_check_reminder")
        val DREAM_JOURNAL_REMINDER = booleanPreferencesKey("dream_journal_reminder")
        val LUCIDITY_FREQUENCY = intPreferencesKey("lucidity_frequency")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
        val START_TIME = floatPreferencesKey("start_time")
        val END_TIME = floatPreferencesKey("end_time")
    }
}