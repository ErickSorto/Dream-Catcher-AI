package org.ballistic.dreamjournalai.dream_notifications.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.ballistic.dreamjournalai.dream_notifications.presentation.components.DreamNotificationTopBar
import org.ballistic.dreamjournalai.dream_notifications.presentation.components.DreamJournalReminderLayout
import org.ballistic.dreamjournalai.dream_notifications.presentation.components.RealityCheckReminderLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@Composable
fun DreamNotificationSettingScreen(
    mainScreenViewModelState: MainScreenViewModelState,
    notificationScreenState: NotificationScreenState,
    onEvent: (NotificationEvent) -> Unit
) {


    Scaffold(
        topBar = {
            DreamNotificationTopBar(
                mainScreenViewModelState = mainScreenViewModelState
            )
        },
        containerColor = Color.Transparent,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            DreamJournalReminderLayout(
                notificationScreenState = notificationScreenState,
                onEvent = onEvent,
                modifier = Modifier
            )
            RealityCheckReminderLayout(
                modifier = Modifier,
                dreamNotificationScreenState = notificationScreenState,
                onEvent = onEvent
            )
        }
    }
}