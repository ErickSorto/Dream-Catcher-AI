package org.ballistic.dreamjournalai.dream_notifications.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_notifications.presentation.NotificationEvent
import org.ballistic.dreamjournalai.dream_notifications.presentation.NotificationScreenState

@Composable
fun RealityCheckReminderLayout(
    modifier: Modifier,
    dreamNotificationScreenState: NotificationScreenState,
    onEvent: (NotificationEvent) -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .background(
                color = colorResource(id = R.color.light_black).copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Lucidity Notification",
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
                    style = typography.bodyMedium,
                    color = colorResource(id = R.color.white)
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = dreamNotificationScreenState.realityCheckReminder,
                    onCheckedChange = {
                        onEvent(NotificationEvent.ToggleRealityCheckReminder(it))
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}