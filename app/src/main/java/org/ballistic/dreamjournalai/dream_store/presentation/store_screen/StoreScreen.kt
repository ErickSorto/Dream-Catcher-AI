package org.ballistic.dreamjournalai.dream_store.presentation.store_screen

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent
import org.ballistic.dreamjournalai.dream_store.presentation.anonymous_store_screen.AnonymousStoreScreen
import org.ballistic.dreamjournalai.dream_store.presentation.store_screen.components.CustomButtonLayout
import org.ballistic.dreamjournalai.dream_store.presentation.store_screen.components.DreamTokenInfo


//import all compose-

@Composable
fun StoreScreen(
    storeScreenViewModelState: StoreScreenViewModelState,
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onStoreEvent: (StoreEvent) -> Unit = {},
    navigateToAccountScreen: () -> Unit = {},
) {
    LaunchedEffect(Unit){
        storeScreenViewModelState.authRepository.reloadFirebaseUser()
    }
    val isAnonymous = storeScreenViewModelState.isUserAnonymous.collectAsStateWithLifecycle().value
    val activity = LocalContext.current as Activity


    onMainEvent(MainScreenEvent.SetBottomBarState(true))
    onMainEvent(MainScreenEvent.SetFloatingActionButtonState(true))


    if (isAnonymous) {
        onMainEvent(MainScreenEvent.SetTopBarState(true))
        AnonymousStoreScreen(
            paddingValues = PaddingValues(bottom = 68.dp),
        ) {
            navigateToAccountScreen()
        }
    } else {
        onMainEvent(MainScreenEvent.SetTopBarState(false))
        Box(modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .padding(bottom = 68.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DreamTokenInfo(
                    modifier = Modifier.padding(16.dp),
                    storeScreenViewModelState = storeScreenViewModelState,
                )
                Spacer(modifier = Modifier.weight(1f))
                CustomButtonLayout(
                    storeScreenViewModelState = storeScreenViewModelState,
                    buy100IsClicked = {
                        onStoreEvent(StoreEvent.ToggleLoading(true))
                        onStoreEvent(StoreEvent.Buy100DreamTokens(activity))
                                      },
                    buy500IsClicked = {
                        onStoreEvent(StoreEvent.ToggleLoading(true))
                        onStoreEvent(StoreEvent.Buy500DreamTokens(activity)) },
                )
            }
        }
    }
}