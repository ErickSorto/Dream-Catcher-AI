package org.ballistic.dreamjournalai.shared.dream_main.domain

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerValue

sealed class MainScreenEvent {
    data class SetBottomBarVisibilityState(val state: Boolean) : MainScreenEvent()
    data class SetBottomBarEnabledState(val state: Boolean) : MainScreenEvent()
    data class SetSearchingState(val state: Boolean) : MainScreenEvent()
    data class SetTopBarState(val state: Boolean) : MainScreenEvent()
    data class SetDrawerState(val state: Boolean) : MainScreenEvent()
    data class SetFloatingActionButtonState(val state: Boolean) : MainScreenEvent()
    data class SearchDreams(val query: String) : MainScreenEvent()
    data class ConsumeDreamTokens(val tokensToConsume: Int) : MainScreenEvent()
    data class ShowSnackBar(val message: String) : MainScreenEvent()
    data class UpdatePaddingValues(val paddingValues: PaddingValues) : MainScreenEvent()
    data class ToggleDrawerState(val drawerValue: DrawerValue) : MainScreenEvent()
    data object UserInteracted : MainScreenEvent()
    data class SetDreamRecentlySaved(val state: Boolean) : MainScreenEvent()
    data object GetAuthState : MainScreenEvent()
    data object TriggerVibration : MainScreenEvent()
    data object OpenStoreLink : MainScreenEvent()
}
