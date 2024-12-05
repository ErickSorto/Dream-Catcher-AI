package org.ballistic.dreamjournalai.dream_notifications.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime

private val Context.dataStore by preferencesDataStore("notification_preferences")

class NotificationPreferences(private val context: Context) {

    // Flow to observe Reality Check Reminder preference
    val realityCheckReminderFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.REALITY_CHECK_REMINDER] ?: false
        }

    // Flow to observe Dream Journal Reminder preference
    val dreamJournalReminderFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DREAM_JOURNAL_REMINDER] ?: false
        }

    // Flow to observe Lucidity Frequency preference
    val lucidityFrequencyFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LUCIDITY_FREQUENCY] ?: 0
        }

    // Flow to observe Reminder Time preference as LocalTime
    val reminderTimeFlow: Flow<LocalTime> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.REMINDER_TIME]?.let { timeStr ->
                try {
                    LocalTime.parse(timeStr)
                } catch (e: Exception) {
                    Log.e("NotificationPreferences", "Invalid time format: $timeStr", e)
                    // Return default time if parsing fails
                    LocalTime(7, 0)
                }
            } ?: LocalTime(7, 0) // Default time
        }

    // Flow to observe Time Range preference
    val timeRangeFlow: Flow<ClosedFloatingPointRange<Float>> = context.dataStore.data.map { preferences ->
        val start = preferences[PreferencesKeys.START_TIME] ?: 0f
        val end = preferences[PreferencesKeys.END_TIME] ?: 1440f
        start..end
    }

    /**
     * Updates the Reality Check Reminder preference.
     *
     * @param value The new value to set.
     */
    suspend fun updateRealityCheckReminder(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REALITY_CHECK_REMINDER] = value
        }
    }

    /**
     * Updates the Dream Journal Reminder preference.
     *
     * @param value The new value to set.
     */
    suspend fun updateDreamJournalReminder(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DREAM_JOURNAL_REMINDER] = value
        }
    }

    /**
     * Updates the Lucidity Frequency preference.
     *
     * @param value The new value to set.
     */
    suspend fun updateLucidityFrequency(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LUCIDITY_FREQUENCY] = value
        }
    }

    /**
     * Updates the Reminder Time preference.
     *
     * @param value The new LocalTime to set.
     */
    suspend fun updateReminderTime(value: LocalTime) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_TIME] = value.toString() // "07:00"
        }
    }

    /**
     * Updates the Time Range preference.
     *
     * @param start The start of the range in minutes.
     * @param end The end of the range in minutes.
     */
    suspend fun updateTimeRange(start: Float, end: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.START_TIME] = start
            preferences[PreferencesKeys.END_TIME] = end
        }
    }

    // Object holding all preference keys
    private object PreferencesKeys {
        val REALITY_CHECK_REMINDER = booleanPreferencesKey("reality_check_reminder")
        val DREAM_JOURNAL_REMINDER = booleanPreferencesKey("dream_journal_reminder")
        val LUCIDITY_FREQUENCY = intPreferencesKey("lucidity_frequency")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
        val START_TIME = floatPreferencesKey("start_time")
        val END_TIME = floatPreferencesKey("end_time")
    }
}