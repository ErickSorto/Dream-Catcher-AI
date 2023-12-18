package org.ballistic.dreamjournalai.feature_dream.presentation.main_screen

import androidx.compose.material3.DrawerValue

sealed class MainScreenEvent {
    data class SetBottomBarState(val state: Boolean) : MainScreenEvent()
    data class SetSearchingState(val state: Boolean) : MainScreenEvent()
    data class SetTopBarState(val state: Boolean) : MainScreenEvent()
    data class SetDrawerState(val state: Boolean) : MainScreenEvent()
    data class SetFloatingActionButtonState(val state: Boolean) : MainScreenEvent()
    data class SearchDreams(val query: String) : MainScreenEvent()
    data class ConsumeDreamTokens(val tokensToConsume: Int) : MainScreenEvent()
    data class ShowSnackBar(val message: String) : MainScreenEvent()

    data class ToggleDrawerState(val drawerValue: DrawerValue) : MainScreenEvent()
    data object UserInteracted : MainScreenEvent()

    data object GetAuthState : MainScreenEvent()
}
