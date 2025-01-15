package org.ballistic.dreamjournalai.shared.dream_notifications.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime
import org.ballistic.dreamjournalai.shared.dream_notifications.domain.NotificationSettingsRepository

/**
 * A common implementation that reads/writes from a provided DataStore<Preferences>.
 */
class NotificationSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : NotificationSettingsRepository {

    // region: flows
    override val realityCheckReminderFlow: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[Keys.REALITY_CHECK_REMINDER] ?: false
        }

    override val dreamJournalReminderFlow: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[Keys.DREAM_JOURNAL_REMINDER] ?: false
        }

    override val lucidityFrequencyFlow: Flow<Int> =
        dataStore.data.map { prefs ->
            prefs[Keys.LUCIDITY_FREQUENCY] ?: 0
        }

    override val reminderTimeFlow: Flow<LocalTime> =
        dataStore.data.map { prefs ->
            val timeStr: String? = prefs[Keys.REMINDER_TIME]
            parseLocalTimeOrDefault(timeStr)
        }

    override val timeRangeFlow: Flow<ClosedFloatingPointRange<Float>> =
        dataStore.data.map { prefs ->
            val start = prefs[Keys.START_TIME] ?: 0f
            val end = prefs[Keys.END_TIME] ?: 1440f
            start..end
        }
    // endregion flows

    // region: updates
    override suspend fun updateRealityCheckReminder(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.REALITY_CHECK_REMINDER] = value
        }
    }

    override suspend fun updateDreamJournalReminder(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.DREAM_JOURNAL_REMINDER] = value
        }
    }

    override suspend fun updateLucidityFrequency(value: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.LUCIDITY_FREQUENCY] = value
        }
    }

    override suspend fun updateReminderTime(value: LocalTime) {
        dataStore.edit { prefs ->
            prefs[Keys.REMINDER_TIME] = value.toString() // e.g. "07:00"
        }
    }

    override suspend fun updateTimeRange(start: Float, end: Float) {
        dataStore.edit { prefs ->
            prefs[Keys.START_TIME] = start
            prefs[Keys.END_TIME] = end
        }
    }
    // endregion updates

    // region: keys
    private object Keys {
        val REALITY_CHECK_REMINDER = booleanPreferencesKey("reality_check_reminder")
        val DREAM_JOURNAL_REMINDER = booleanPreferencesKey("dream_journal_reminder")
        val LUCIDITY_FREQUENCY = intPreferencesKey("lucidity_frequency")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
        val START_TIME = floatPreferencesKey("start_time")
        val END_TIME = floatPreferencesKey("end_time")
    }
    // endregion keys

    private fun parseLocalTimeOrDefault(str: String?): LocalTime {
        return if (str.isNullOrBlank()) {
            LocalTime(hour = 7, minute = 0)
        } else {
            try {
                LocalTime.parse(str) // e.g. "07:00"
            } catch (e: Exception) {
                LocalTime(hour = 7, minute = 0)
            }
        }
    }
}