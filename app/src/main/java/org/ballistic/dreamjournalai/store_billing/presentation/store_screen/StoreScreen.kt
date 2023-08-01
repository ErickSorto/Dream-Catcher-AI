package org.ballistic.dreamjournalai.store_billing.presentation.store_screen

import android.app.Activity
import android.telephony.SubscriptionInfo
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.store_billing.presentation.anonymous_store_screen.AnonymousStoreScreen
import org.ballistic.dreamjournalai.store_billing.presentation.store_screen.components.CustomButtonLayout
import org.ballistic.dreamjournalai.store_billing.presentation.store_screen.components.DreamTokenInfo


//import all compose-

@Composable
fun StoreScreen(
    mainScreenViewModelState: MainScreenViewModelState,
    onMainEvent : (MainScreenEvent) -> Unit = {},
    onStoreEvent: (StoreEvent) -> Unit = {},
    navigateToAccountScreen: () -> Unit = {},
) {
    val isAnonymous = mainScreenViewModelState.authRepo
        .currentUser.collectAsStateWithLifecycle().value?.isAnonymous
    val activity = LocalContext.current as Activity


    onMainEvent(MainScreenEvent.SetBottomBarState(true))
    onMainEvent(MainScreenEvent.SetFloatingActionButtonState(true))


    if (isAnonymous == true) {
        onMainEvent(MainScreenEvent.SetTopBarState(true))
        AnonymousStoreScreen(
            paddingValues = PaddingValues(bottom = 68.dp),
        ) {
            navigateToAccountScreen()
        }
    } else {
        onMainEvent(MainScreenEvent.SetTopBarState(false))
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .padding(bottom = 68.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DreamTokenInfo(modifier = Modifier.padding(16.dp), mainScreenViewModelState = mainScreenViewModelState)
                Spacer(modifier = Modifier.weight(1f))
                CustomButtonLayout(
                    buy100IsClicked = {onStoreEvent(StoreEvent.Buy100DreamTokens(activity))},
                    buy500IsClicked = {onStoreEvent(StoreEvent.Buy500DreamTokens(activity))},
                )
            }
        }
    }
}