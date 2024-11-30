package org.ballistic.dreamjournalai.dream_notifications.presentation

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.ballistic.dreamjournalai.dream_notifications.domain.NotificationEvent
import org.ballistic.dreamjournalai.dream_notifications.presentation.components.DreamJournalReminderLayout
import org.ballistic.dreamjournalai.dream_notifications.presentation.components.DreamNotificationTopBar
import org.ballistic.dreamjournalai.dream_notifications.presentation.components.RealityCheckReminderLayout
import org.ballistic.dreamjournalai.dream_notifications.presentation.viewmodel.NotificationScreenState
import org.ballistic.dreamjournalai.dream_main.presentation.viewmodel.MainScreenViewModelState

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun DreamNotificationSettingScreen(
    mainScreenViewModelState: MainScreenViewModelState,
    notificationScreenState: NotificationScreenState,
    bottomPaddingValue: Dp,
    onEvent: (NotificationEvent) -> Unit,
) {

    val postNotificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(Unit) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

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
                .padding(top = paddingValues.calculateTopPadding(), bottom = bottomPaddingValue)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            DreamJournalReminderLayout(
                notificationScreenState = notificationScreenState,
                onEvent = onEvent,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            RealityCheckReminderLayout(
                modifier = Modifier,
                dreamNotificationScreenState = notificationScreenState,
                onEvent = onEvent
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}