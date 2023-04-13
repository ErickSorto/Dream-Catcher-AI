package org.ballistic.dreamjournalai.feature_dream.presentation.main_screen

sealed class MainScreenEvent {
    data class SetBottomBarState(val state: Boolean) : MainScreenEvent()
    data class SetSearchingState(val state: Boolean) : MainScreenEvent()
    data class SetTopBarState(val state: Boolean) : MainScreenEvent()
    data class SetFloatingActionButtonState(val state: Boolean) : MainScreenEvent()
}