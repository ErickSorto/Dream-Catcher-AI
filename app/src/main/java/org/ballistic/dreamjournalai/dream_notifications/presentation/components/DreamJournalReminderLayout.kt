package org.ballistic.dreamjournalai.dream_notifications.presentation.components


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_notifications.presentation.NotificationEvent
import org.ballistic.dreamjournalai.dream_notifications.presentation.NotificationScreenState
import java.time.Clock
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamJournalReminderLayout(
    modifier: Modifier,
    notificationScreenState: NotificationScreenState,
    onEvent: (NotificationEvent) -> Unit
) {

    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    val time =
        LocalTime.parse(notificationScreenState.reminderTime.toString(), formatter).toString()

    ClockDialog(
        state = notificationScreenState.dreamJournalReminderTimePickerState,
        config = ClockConfig(
            defaultTime = Clock.systemDefaultZone().instant().atZone(Clock.systemDefaultZone().zone)
                .toLocalTime(),
            is24HourFormat = false,

            ),
        selection = ClockSelection.HoursMinutes { hour, minute ->
            onEvent(NotificationEvent.SetReminderTime(LocalTime.of(hour, minute)))
        }
    )

    Column(
        modifier = modifier
            .padding(16.dp)
            .background(
                color = colorResource(id = R.color.light_black).copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp)
            )
            .animateContentSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Dream Journal Reminder",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.white)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "This feature will remind you to write down your dreams every morning. You can set the time of the reminder below.",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.white)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enable Reminder:",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorResource(id = R.color.white)
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = notificationScreenState.dreamJournalReminder,
                    onCheckedChange = {
                        onEvent(NotificationEvent.ToggleDreamJournalReminder(it))
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (notificationScreenState.dreamJournalReminder) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp).fillMaxWidth()
                ) {
                    Text(
                        text = "Reminder Time",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorResource(id = R.color.white)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                color = colorResource(id = R.color.white).copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onEvent(NotificationEvent.ToggleTimePickerForJournalReminder(true))
                            }

                    ) {
                        Text(
                            text = time,
                            modifier = Modifier.padding(
                                horizontal = 16.dp, vertical = 8.dp
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorResource(id = R.color.white),
                        )
                    }
                }
            }
        }
    }
}