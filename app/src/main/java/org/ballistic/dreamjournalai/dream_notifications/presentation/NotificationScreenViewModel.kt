package org.ballistic.dreamjournalai.dream_notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.maxkeppeker.sheets.core.models.base.SheetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.dream_notifications.domain.usecases.ScheduleDailyReminderUseCase
import org.ballistic.dreamjournalai.dream_notifications.domain.usecases.ScheduleLucidityNotificationUseCase
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NotificationScreenViewModel @Inject constructor(
    private val scheduleDailyReminderUseCase: ScheduleDailyReminderUseCase,
    private val scheduleLucidityNotificationUseCase: ScheduleLucidityNotificationUseCase,
) : ViewModel() {

    private val _notificationScreenState = MutableStateFlow(NotificationScreenState())
    val notificationScreenState: StateFlow<NotificationScreenState> = _notificationScreenState

    fun onEvent(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.ToggleRealityCheckReminder -> {
                _notificationScreenState.value = _notificationScreenState.value.copy(
                    realityCheckReminder = event.lucidityNotification
                )
                if (event.lucidityNotification) {
                    viewModelScope.launch {
                        scheduleLucidityNotificationUseCase(
                            _notificationScreenState.value.lucidityFrequency,
                            TimeUnit.HOURS.toMillis(_notificationScreenState.value.lucidityFrequency.toLong())
                        )
                    }
                } else {
                    WorkManager.getInstance().cancelUniqueWork("Reality Check Reminder")
                }
            }

            is NotificationEvent.ChangeLucidityFrequency -> {
                _notificationScreenState.value = _notificationScreenState.value.copy(
                    lucidityFrequency = event.lucidityFrequency
                )
                if (_notificationScreenState.value.realityCheckReminder) {
                    viewModelScope.launch {
                        scheduleLucidityNotificationUseCase(
                            _notificationScreenState.value.lucidityFrequency,
                            TimeUnit.HOURS.toMillis(_notificationScreenState.value.lucidityFrequency.toLong())
                        )
                    }
                }
            }

            is NotificationEvent.SetReminderTime -> {
                _notificationScreenState.value = _notificationScreenState.value.copy(
                    reminderTime = event.javaTime.format(DateTimeFormatter.ofPattern("h:mm a"))
                )
                viewModelScope.launch {
                    scheduleDailyReminderUseCase(event.javaTime.toNanoOfDay())
                }
            }

            is NotificationEvent.ToggleDreamJournalReminder -> {
                _notificationScreenState.value = _notificationScreenState.value.copy(
                    dreamJournalReminder = event.dreamReminder
                )
                if (event.dreamReminder) {
                    viewModelScope.launch {
                        scheduleDailyReminderUseCase(
                            LocalTime.parse(
                                _notificationScreenState.value.reminderTime,
                                DateTimeFormatter.ofPattern("h:mm a")
                            ).toNanoOfDay()
                        )
                    }
                } else {
                    WorkManager.getInstance().cancelUniqueWork("Dream Journal Reminder")
                }
            }

            is NotificationEvent.ToggleTimePickerForJournalReminder -> {
                notificationScreenState.value.dreamJournalReminderTimePickerState.show()
            }
        }
    }
}

data class NotificationScreenState(
    val realityCheckReminder: Boolean = false,
    val dreamJournalReminder: Boolean = false,
    val dreamJournalReminderTimePickerState: SheetState = SheetState(),
    val lucidityFrequency: Int = 0,
    val reminderTime: String = LocalTime.of(7, 0).format(DateTimeFormatter.ofPattern("h:mm a"))
)