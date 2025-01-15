package org.ballistic.dreamjournalai.shared.dream_notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.ballistic.dreamjournalai.shared.core.util.addOneDay
import org.ballistic.dreamjournalai.shared.dream_notifications.data.local.NotificationPreferences
import org.ballistic.dreamjournalai.shared.dream_notifications.domain.NotificationEvent
import org.ballistic.dreamjournalai.shared.dream_notifications.domain.usecases.ScheduleDailyReminderUseCase
import org.ballistic.dreamjournalai.shared.dream_notifications.domain.usecases.ScheduleLucidityNotificationUseCase
import org.ballistic.dreamjournalai.shared.core.util.formatLocalTime
import org.ballistic.dreamjournalai.shared.core.util.parseFormattedTime

class NotificationScreenViewModel(
    private val scheduleDailyReminderUseCase: ScheduleDailyReminderUseCase,
    private val scheduleLucidityNotificationUseCase: ScheduleLucidityNotificationUseCase,
    private val notificationPreferences: NotificationPreferences,
) : ViewModel() {

    private val _notificationScreenState = MutableStateFlow(NotificationScreenState())
    val notificationScreenState: StateFlow<NotificationScreenState> = _notificationScreenState.asStateFlow()

    init {
        // Collect Reality Check Reminder
        viewModelScope.launch {
            notificationPreferences.realityCheckReminderFlow.collect { value ->
                _notificationScreenState.value =
                    _notificationScreenState.value.copy(realityCheckReminder = value)
            }
        }

        // Collect Dream Journal Reminder
        viewModelScope.launch {
            notificationPreferences.dreamJournalReminderFlow.collect { value ->
                _notificationScreenState.value =
                    _notificationScreenState.value.copy(dreamJournalReminder = value)
            }
        }

        // Collect Lucidity Frequency
        viewModelScope.launch {
            notificationPreferences.lucidityFrequencyFlow.collect { value ->
                _notificationScreenState.value =
                    _notificationScreenState.value.copy(lucidityFrequency = value)
            }
        }

        // Collect Reminder Time and format it
        viewModelScope.launch {
            notificationPreferences.reminderTimeFlow.collect { localTime ->
                val formattedTime = formatLocalTime(localTime)
                _notificationScreenState.value =
                    _notificationScreenState.value.copy(reminderTime = formattedTime)
            }
        }

        // Collect Time Range
        viewModelScope.launch {
            notificationPreferences.timeRangeFlow.collect { range ->
                _notificationScreenState.value = _notificationScreenState.value.copy(
                    startTime = range.start,
                    endTime = range.endInclusive
                )
            }
        }
    }

    fun onEvent(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.ToggleRealityCheckReminder -> {
                _notificationScreenState.value = _notificationScreenState.value.copy(
                    realityCheckReminder = event.lucidityNotification
                )
                viewModelScope.launch {
                    notificationPreferences.updateRealityCheckReminder(event.lucidityNotification)
                    if (event.lucidityNotification) {
                        scheduleLucidityNotificationUseCase(
                            _notificationScreenState.value.lucidityFrequency,
                            _notificationScreenState.value.startTime,
                            _notificationScreenState.value.endTime
                        )
                    } else {
                        cancelWorkByTag("LucidityNotificationTag")
                    }
                }
            }

            is NotificationEvent.ChangeLucidityFrequency -> {
                _notificationScreenState.value = _notificationScreenState.value.copy(
                    lucidityFrequency = event.lucidityFrequency
                )
                viewModelScope.launch {
                    notificationPreferences.updateLucidityFrequency(event.lucidityFrequency)
                }

                viewModelScope.launch {
                    scheduleLucidityNotificationUseCase(
                        event.lucidityFrequency,
                        _notificationScreenState.value.startTime,
                        _notificationScreenState.value.endTime
                    )
                }
            }

            is NotificationEvent.SetReminderTime -> {
                val formattedTime = formatLocalTime(event.localTime)
                _notificationScreenState.value = _notificationScreenState.value.copy(
                    reminderTime = formattedTime
                )
                viewModelScope.launch {
                    notificationPreferences.updateReminderTime(event.localTime)
                    val reminderTimeInMillis = calculateReminderTimeInMillis(event.localTime)
                    scheduleDailyReminderUseCase(reminderTimeInMillis)
                }
            }

            is NotificationEvent.ToggleDreamJournalReminder -> {
                _notificationScreenState.value = _notificationScreenState.value.copy(
                    dreamJournalReminder = event.dreamReminder
                )
                viewModelScope.launch {
                    notificationPreferences.updateDreamJournalReminder(event.dreamReminder)
                    if (event.dreamReminder) {
                        val reminderTime = try {
                            parseFormattedTime(_notificationScreenState.value.reminderTime)
                        } catch (e: IllegalArgumentException) {
                            LocalTime(7, 0) // Default time
                        }
                        val reminderTimeInMillis = calculateReminderTimeInMillis(reminderTime)
                        scheduleDailyReminderUseCase(reminderTimeInMillis)
                    } else {
                        cancelWorkByTag("DailyDreamJournalReminderTag")
                    }
                }
            }

            is NotificationEvent.SetTimeRange -> {
                _notificationScreenState.update {
                    it.copy(
                        startTime = event.range.start,
                        endTime = event.range.endInclusive
                    )
                }

                viewModelScope.launch {
                    notificationPreferences.updateTimeRange(event.range.start, event.range.endInclusive)
                }

                viewModelScope.launch {
                    scheduleLucidityNotificationUseCase(
                        _notificationScreenState.value.lucidityFrequency,
                        event.range.start,
                        event.range.endInclusive
                    )
                }
            }

            NotificationEvent.ScheduleLucidityNotification -> {
                viewModelScope.launch {
                    scheduleLucidityNotificationUseCase(
                        _notificationScreenState.value.lucidityFrequency,
                        _notificationScreenState.value.startTime,
                        _notificationScreenState.value.endTime
                    )
                }
            }
        }
    }

    private fun cancelWorkByTag(tag: String) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(tag).result.addListener({
            workManager.getWorkInfosByTag(tag).get().let { workInfos ->
                Log.d("Notification", "Check if cancelled: $workInfos")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun calculateReminderTimeInMillis(localTime: LocalTime): Long {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        var reminderDateTime = LocalDateTime(now.year, now.monthNumber, now.dayOfMonth, localTime.hour, localTime.minute, localTime.second, localTime.nanosecond)

        if (reminderDateTime < now) {
            // Manually add one day
            reminderDateTime = addOneDay(reminderDateTime)
        }

        // Convert to Instant considering the system's default time zone
        val reminderInstant = reminderDateTime.toInstant(TimeZone.currentSystemDefault())
        return reminderInstant.toEpochMilliseconds()
    }
}

data class NotificationScreenState(
    val realityCheckReminder: Boolean = false,
    val dreamJournalReminder: Boolean = false,
    val lucidityFrequency: Int = 1, // Default to 1 hour
    val reminderTime: String = formatLocalTime(LocalTime(7, 0)),
    val startTime: Float = 12f,
    val endTime: Float = 1440f // Default to entire day
)