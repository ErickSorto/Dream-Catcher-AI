package org.ballistic.dreamjournalai.dream_notifications.presentation.components

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationEvent
import org.ballistic.dreamjournalai.dream_notifications.presentation.viewmodel.NotificationScreenState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RealityCheckReminderLayout(
    modifier: Modifier,
    dreamNotificationScreenState: NotificationScreenState,
    onEvent: (NotificationEvent) -> Unit
) {
    val postNotificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(Unit) {
        if (!postNotificationPermission.status.isGranted && dreamNotificationScreenState.dreamJournalReminder) {
            onEvent(NotificationEvent.ToggleDreamJournalReminder(false))
        }
    }
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .background(
                color = colorResource(id = R.color.light_black).copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp)
            )
            .animateContentSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Reality Check Reminder",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.white)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This feature will remind you to perform reality checks throughout the day. You can set the frequency of the notifications below.",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = typography.bodyMedium,
            color = colorResource(id = R.color.white)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enable Reality Check",
                modifier = Modifier.padding(16.dp),
                style = typography.bodyLarge,
                color = colorResource(id = R.color.white)
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = dreamNotificationScreenState.realityCheckReminder,
                onCheckedChange = {
                    if (postNotificationPermission.status.isGranted) {
                        onEvent(NotificationEvent.ToggleRealityCheckReminder(it))
                    } else {
                        postNotificationPermission.launchPermissionRequest()
                    }
                },
                modifier = Modifier.padding(16.dp)
            )
        }
        if (dreamNotificationScreenState.realityCheckReminder && postNotificationPermission.status.isGranted) {
            FrequencySlider(
                modifier = Modifier.padding(horizontal = 16.dp),
                frequency = dreamNotificationScreenState.lucidityFrequency.toFloat(),
                onValueChange = {
                    onEvent(NotificationEvent.ChangeLucidityFrequency(it.toInt()))
                },
                onValueChangeFinished = {
                    onEvent(NotificationEvent.ScheduleLucidityNotification)
                }

            )
            Spacer(modifier = Modifier.height(16.dp))
            TimeRangeSlider(
                modifier = Modifier.padding(horizontal = 16.dp),
                startTime = dreamNotificationScreenState.startTime,
                endTime = dreamNotificationScreenState.endTime,
                onValueChange = {
                    onEvent(NotificationEvent.SetTimeRange(it))
                },
                onValueChangeFinished = {
                    onEvent(NotificationEvent.ScheduleLucidityNotification)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FrequencySlider(
    modifier: Modifier,
    frequency: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(frequency) }

    Column(
        modifier = modifier
            .background(
                color = colorResource(id = R.color.brighter_white).copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            )
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Frequency",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.white)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = formatFrequency(sliderPosition),
            style = typography.bodyLarge,
            color = colorResource(id = R.color.white)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onValueChange(it)
            },
            onValueChangeFinished = {
                onValueChangeFinished()
            },
            valueRange = 0f..6f,
            steps = 5,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun formatFrequency(frequency: Float): String {
    return when (frequency) {
        0f -> "Every 30 minutes"
        else -> "Every ${frequency.toInt()} hours"
    }
}
@Composable
fun TimeRangeSlider(
    modifier: Modifier,
    startTime: Float,
    endTime: Float,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeFinished: () -> Unit
) {
    var sliderPosition by remember { mutableStateOf(startTime..endTime) }

    Column(
        modifier = modifier
            .background(
                color = colorResource(id = R.color.brighter_white).copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            )
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Time Range",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.white)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Start: ${formatTime(sliderPosition.start)}",
                style = typography.bodyLarge,
                color = colorResource(id = R.color.white)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "End: ${formatTime(sliderPosition.endInclusive)}",
                style = typography.bodyLarge,
                color = colorResource(id = R.color.white)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        RangeSlider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onValueChange(it)
            },
            valueRange = 0f..1440f, // 1440 minutes in a day (24*60)
            steps = 47, // 48 total steps for 30-minute intervals (1440/30 - 1)
            modifier = Modifier.padding(horizontal = 16.dp),
            onValueChangeFinished = onValueChangeFinished,
            colors = SliderDefaults.colors(
                inactiveTickColor = Color.Transparent,
                activeTickColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun formatTime(minutes: Float): String {
    val totalMinutes = minutes.toInt()
    val hours = totalMinutes / 60
    val mins = totalMinutes % 60
    return String.format("%02d:%02d", hours, mins)
}
