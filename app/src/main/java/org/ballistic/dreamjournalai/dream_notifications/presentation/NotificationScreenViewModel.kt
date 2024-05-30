package org.ballistic.dreamjournalai.dream_notifications.presentation

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.maxkeppeker.sheets.core.models.base.SheetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.dream_notifications.data.local.NotificationPreferences
import org.ballistic.dreamjournalai.dream_notifications.domain.usecases.ScheduleDailyReminderUseCase
import org.ballistic.dreamjournalai.dream_notifications.domain.usecases.ScheduleLucidityNotificationUseCase
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@Suppress("StaticFieldLeak")
@HiltViewModel
class NotificationScreenViewModel @Inject constructor(
    private val scheduleDailyReminderUseCase: ScheduleDailyReminderUseCase,
    private val scheduleLucidityNotificationUseCase: ScheduleLucidityNotificationUseCase,
    private val notificationPreferences: NotificationPreferences,
    private val context: Context
) : ViewModel() {

    private val _notificationScreenState = MutableStateFlow(NotificationScreenState())
    val notificationScreenState: StateFlow<NotificationScreenState> = _notificationScreenState

    init {
        viewModelScope.launch {
            notificationPreferences.realityCheckReminderFlow.collect { value ->
                _notificationScreenState.value =
                    _notificationScreenState.value.copy(realityCheckReminder = value)
            }
        }
        viewModelScope.launch {
            notificationPreferences.dreamJournalReminderFlow.collect { value ->
                _notificationScreenState.value =
                    _notificationScreenState.value.copy(dreamJournalReminder = value)
            }
        }
        viewModelScope.launch {
            notificationPreferences.lucidityFrequencyFlow.collect { value ->
                _notificationScreenState.value =
                    _notificationScreenState.value.copy(lucidityFrequency = value)
            }
        }
        viewModelScope.launch {
            notificationPreferences.reminderTimeFlow.collect { value ->
                _notificationScreenState.value =
                    _notificationScreenState.value.copy(reminderTime = value)
            }
        }
        viewModelScope.launch {
            notificationPreferences.timeRangeFlow.collect { value ->
                _notificationScreenState.value = _notificationScreenState.value.copy(startTime = value.start, endTime = value.endInclusive)
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
            }

            is NotificationEvent.SetReminderTime -> {
                _notificationScreenState.value = _notificationScreenState.value.copy(
                    reminderTime = event.javaTime.format(DateTimeFormatter.ofPattern("h:mm a"))
                )
                viewModelScope.launch {
                    notificationPreferences.updateReminderTime(event.javaTime.format(DateTimeFormatter.ofPattern("h:mm a")))
                    val reminderTimeInMillis = calculateReminderTimeInMillis(event.javaTime)
                    Log.d("Notification", "Reminder Time (ms): $reminderTimeInMillis")
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
                        val reminderTimeInMillis = calculateReminderTimeInMillis(LocalTime.parse(
                            _notificationScreenState.value.reminderTime,
                            DateTimeFormatter.ofPattern("h:mm a")
                        ))
                        scheduleDailyReminderUseCase(reminderTimeInMillis)
                    } else {
                        cancelWorkByTag("DailyDreamJournalReminderTag")
                    }
                }
            }

            is NotificationEvent.ToggleTimePickerForJournalReminder -> {
                _notificationScreenState.value.dreamJournalReminderTimePickerState.show()
            }

            is NotificationEvent.SetTimeRange -> {
                _notificationScreenState.update {
                    it.copy(
                        startTime = event.range.start,
                        endTime = event.range.endInclusive
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
        val now = LocalDateTime.now()
        var reminderDateTime = LocalDateTime.of(now.toLocalDate(), localTime)

        if (reminderDateTime.isBefore(now)) {
            reminderDateTime = reminderDateTime.plusDays(1)
        }

        return reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}

data class NotificationScreenState(
    val realityCheckReminder: Boolean = false,
    val dreamJournalReminder: Boolean = false,
    val dreamJournalReminderTimePickerState: SheetState = SheetState(),
    val lucidityFrequency: Int = 1, // Default to 1 hour
    val reminderTime: String = LocalTime.of(7, 0).format(DateTimeFormatter.ofPattern("h:mm a")),
    val startTime: Float = 12f,
    val endTime: Float = 1440f // Default to entire day
)