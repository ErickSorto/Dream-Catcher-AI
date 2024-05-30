package org.ballistic.dreamjournalai.dream_notifications.presentation

import java.time.LocalTime

sealed class NotificationEvent {
    data class ToggleRealityCheckReminder(val lucidityNotification: Boolean) : NotificationEvent()
    data class ToggleDreamJournalReminder(val dreamReminder: Boolean) : NotificationEvent()
    data class ChangeLucidityFrequency(val lucidityFrequency: Int) : NotificationEvent()
    data class SetReminderTime(val javaTime: LocalTime) : NotificationEvent()
    data class ToggleTimePickerForJournalReminder(val showTimePicker: Boolean) : NotificationEvent()
    data class SetTimeRange(val range: ClosedFloatingPointRange<Float>) : NotificationEvent()
    data object ScheduleLucidityNotification : NotificationEvent()
}