package org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.icerock.moko.permissions.PermissionsController
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.background_during_day
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.ballistic.dreamjournalai.shared.dream_main.domain.MainScreenEvent
import org.ballistic.dreamjournalai.shared.dream_authentication.domain.repository.AuthRepository
import dreamjournalai.composeapp.shared.generated.resources.blue_lighthouse
import org.ballistic.dreamjournalai.shared.core.Resource
import org.ballistic.dreamjournalai.shared.core.domain.VibratorUtil
import org.ballistic.dreamjournalai.shared.core.util.StoreLinkOpener
import org.jetbrains.compose.resources.DrawableResource

class MainScreenViewModel(
    private val repo: AuthRepository,
    private val vibratorUtil: VibratorUtil,
    private val storeLinkOpener: StoreLinkOpener
) : ViewModel() {
    private val _mainScreenViewModelState = MutableStateFlow(MainScreenViewModelState(authRepo = repo))
    val mainScreenViewModelState: StateFlow<MainScreenViewModelState> = _mainScreenViewModelState.asStateFlow()

    init {
        Logger.d("MainScreenViewModel") { "MainScreenViewModel initialized" }
        updateBackgroundPeriodically()
        _mainScreenViewModelState.value = _mainScreenViewModelState.value.copy(
            backgroundResource = getBackgroundResource()
        )
    }

    private fun getBackgroundResource(): DrawableResource {
        val currentTime =  Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val currentHour = currentTime.hour

        return if (currentHour in 20..23 || currentHour in 0..5) {
            Res.drawable.blue_lighthouse
        } else {
            Res.drawable.blue_lighthouse
        }
    }

    private fun updateBackgroundPeriodically() {
        viewModelScope.launch {
            while (isActive) {
                val newResource = getBackgroundResource()
                if (newResource != _mainScreenViewModelState.value.backgroundResource) {
                    _mainScreenViewModelState.value = _mainScreenViewModelState.value.copy(
                        backgroundResource = newResource
                    )
                }
                delay(60000) // Check every minute
            }
        }
    }

    fun onEvent (event: MainScreenEvent) = viewModelScope.launch {
        when (event) {
            is MainScreenEvent.SetBottomBarVisibilityState -> {
                viewModelScope.launch {
                    _mainScreenViewModelState.value = _mainScreenViewModelState.value.copy(
                        scaffoldState = _mainScreenViewModelState.value.scaffoldState.copy(
                            bottomBarState = event.state
                        )
                    )
                }
            }
            is MainScreenEvent.SetSearchingState -> {
                _mainScreenViewModelState.value = _mainScreenViewModelState.value.copy(
                    scaffoldState = _mainScreenViewModelState.value.scaffoldState.copy(
                        isUserSearching = event.state
                    )
                )
            }
            is MainScreenEvent.SetTopBarState -> {
                _mainScreenViewModelState.value = _mainScreenViewModelState.value.copy(
                    scaffoldState = _mainScreenViewModelState.value.scaffoldState.copy(
                        topBarState = event.state
                    )
                )
            }
            is MainScreenEvent.SetFloatingActionButtonState -> {
                _mainScreenViewModelState.value = _mainScreenViewModelState.value.copy(
                    scaffoldState = _mainScreenViewModelState.value.scaffoldState.copy(
                        floatingActionButtonState = event.state
                    )
                )
            }
            is MainScreenEvent.SetDrawerState -> {
                viewModelScope.launch {
                    _mainScreenViewModelState.value = _mainScreenViewModelState.value.copy(
                        isDrawerEnabled = event.state
                    )
                }
            }
            is MainScreenEvent.SearchDreams -> {
                mainScreenViewModelState.value.searchedText.value = event.query
            }
            is MainScreenEvent.ConsumeDreamTokens -> {
                val result = repo.consumeDreamTokens(event.tokensToConsume)
                if (result is Resource.Error) {
                    result.message?.let {
                        _mainScreenViewModelState.value.scaffoldState.snackBarHostState.value.showSnackbar(
                            message = it
                        )
                    }
                }
            }
            is MainScreenEvent.ShowSnackBar -> {
                viewModelScope.launch {
                    _mainScreenViewModelState.value.scaffoldState.snackBarHostState.value.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short,
                        actionLabel = "Dismiss"
                    )
                }
            }
            is MainScreenEvent.ToggleDrawerState -> {
                viewModelScope.launch {
                    if(event.drawerValue == DrawerValue.Closed) {
                        _mainScreenViewModelState.value.drawerMain.close()
                    } else {
                        _mainScreenViewModelState.value.drawerMain.open()
                    }
                }
            }
            is MainScreenEvent.UserInteracted -> {
                repo.recordUserInteraction()
            }
            is MainScreenEvent.GetAuthState -> {
                repo.getAuthState(viewModelScope)
            }

            is MainScreenEvent.SetBottomBarEnabledState -> {
                _mainScreenViewModelState.value = _mainScreenViewModelState.value.copy(
                    isBottomBarEnabledState = event.state
                )
            }
            is MainScreenEvent.UpdatePaddingValues -> {
                _mainScreenViewModelState.value = _mainScreenViewModelState.value.copy(
                    paddingValues = event.paddingValues
                )
            }

            is MainScreenEvent.SetDreamRecentlySaved -> {
                _mainScreenViewModelState.update{
                    it.copy(
                        isDreamRecentlySaved = event.state
                    )
                }
            }
            is MainScreenEvent.TriggerVibration -> {
                viewModelScope.launch { vibratorUtil.triggerVibration() }
            }
            is MainScreenEvent.OpenStoreLink -> {
                storeLinkOpener.openStoreLink()
            }
        }
    }
}

data class MainScreenViewModelState(
    val scaffoldState: ScaffoldState = ScaffoldState(),
    val isDrawerEnabled : Boolean = true,
    val isBottomBarEnabledState : Boolean = true,
    val drawerMain: DrawerState = DrawerState(DrawerValue.Closed),
    val authRepo: AuthRepository,
    val searchedText: MutableStateFlow<String> = MutableStateFlow(""),
    val dreamTokens: Int = 0,
    val backgroundResource: DrawableResource = Res.drawable.background_during_day,
    val paddingValues: PaddingValues = PaddingValues(0.dp),
    val isDreamRecentlySaved: Boolean = false
)
data class ScaffoldState (
    val bottomBarState: Boolean = true,
    val topBarState: Boolean = true,
    val floatingActionButtonState: Boolean = true,
    val isUserSearching : Boolean = false,
    val snackBarHostState: MutableState<SnackbarHostState> = mutableStateOf(SnackbarHostState()),
)