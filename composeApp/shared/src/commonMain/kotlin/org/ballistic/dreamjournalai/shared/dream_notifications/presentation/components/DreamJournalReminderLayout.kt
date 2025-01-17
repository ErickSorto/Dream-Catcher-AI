package org.ballistic.dreamjournalai.shared.dream_notifications.presentation.components


//import android.Manifest
//import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import com.google.accompanist.permissions.isGranted
//import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

//import org.ballistic.dreamjournalai.shared.dream_notifications.domain.NotificationEvent
//import org.ballistic.dreamjournalai.shared.dream_notifications.presentation.viewmodel.NotificationScreenState
//import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.BrighterWhite
//import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
//import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
//import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DreamJournalReminderLayout(
//    modifier: Modifier,
//    notificationScreenState: NotificationScreenState,
//    onEvent: (NotificationEvent) -> Unit
//) {
//    val postNotificationPermission =
//        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
//
//    var showTimePicker = remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) {
//        if (!postNotificationPermission.status.isGranted && notificationScreenState.dreamJournalReminder) {
//            onEvent(NotificationEvent.ToggleDreamJournalReminder(false))
//        }
//    }
//
//    // Display the Time Picker Dialog when showTimePicker is true
//    if (showTimePicker.value) {
//        DialWithDialogExample (
//            onConfirm = { timePickerState ->
//                val time = LocalTime(timePickerState.hour, timePickerState.minute)
//                onEvent(NotificationEvent.SetReminderTime(time))
//                showTimePicker.value = false
//            },
//            onDismiss = { showTimePicker.value = false }
//        )
//    }
//
//    Column(
//        modifier = modifier
//            .padding(horizontal = 16.dp)
//            .background(
//                color = LightBlack.copy(alpha = 0.8f),
//                shape = RoundedCornerShape(8.dp)
//            )
//            .animateContentSize()
//    ) {
//        Column {
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = "Dream Journal Reminder",
//                modifier = Modifier.padding(horizontal = 16.dp),
//                style = typography.titleMedium,
//                fontWeight = FontWeight.Bold,
//                color = BrighterWhite
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = "This feature will remind you to write down your dreams every day. You can set the time of the reminder below.",
//                modifier = Modifier.padding(horizontal = 16.dp),
//                style = typography.bodyMedium,
//                color = White
//            )
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Enable Reminder:",
//                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp),
//                    style = typography.bodyLarge,
//                    color = BrighterWhite
//                )
//                Spacer(modifier = Modifier.weight(1f))
//                Switch(
//                    checked = notificationScreenState.dreamJournalReminder,
//                    onCheckedChange = {
//                        if (postNotificationPermission.status.isGranted) {
//                            onEvent(NotificationEvent.ToggleDreamJournalReminder(it))
//                        } else {
//                            postNotificationPermission.launchPermissionRequest()
//                        }
//                    },
//                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
//                )
//            }
//
//            if (notificationScreenState.dreamJournalReminder && postNotificationPermission.status.isGranted) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center,
//                    modifier = Modifier
//                        .padding(16.dp, 16.dp, 16.dp, 16.dp)
//                        .background(
//                            color = BrighterWhite.copy(alpha = 0.05f),
//                            shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp)
//                        )
//                        .fillMaxWidth()
//                ) {
//                    Text(
//                        text = "Set Time",
//                        modifier = Modifier.padding(16.dp),
//                        style = typography.titleMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = BrighterWhite
//                    )
//                    Box(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(8.dp))
//                            .background(
//                                color = RedOrange,
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                            .border(
//                                width = 1.dp,
//                                color = BrighterWhite.copy(alpha = 0.1f),
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                            .clickable {
//                                showTimePicker.value = true
//                            }
//
//                    ) {
//                        Text(
//                            text = notificationScreenState.reminderTime,
//                            modifier = Modifier.padding(
//                                horizontal = 32.dp, vertical = 8.dp
//                            ),
//                            style = typography.bodyLarge,
//                            fontWeight = FontWeight.ExtraBold,
//                            color = BrighterWhite,
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialWithDialogExample(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = false,
    )

    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) }
    ) {
        TimePicker(
            state = timePickerState,
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        text = { content() }
    )
}