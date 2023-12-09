package org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent
import org.ballistic.dreamjournalai.user_authentication.domain.repository.AuthRepository
import java.time.LocalDateTime
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {
    private val _mainScreenViewModelState = MutableStateFlow(MainScreenViewModelState(authRepo = repo))
    val mainScreenViewModelState: StateFlow<MainScreenViewModelState> = _mainScreenViewModelState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBackgroundResource(): Int {
        val currentTime = LocalDateTime.now()
        val currentHour = currentTime.hour

        return if (currentHour in 20..23 || currentHour in 0..5) {
            R.drawable.blue_lighthouse

        } else {
            R.drawable.background_during_day
        }
    }

    init {
        _mainScreenViewModelState.value = _mainScreenViewModelState.value.copy(
            backgroundResource = getBackgroundResource()
        )
    }

    fun onEvent (event: MainScreenEvent) = viewModelScope.launch {
        when (event) {
            is MainScreenEvent.SetBottomBarState -> {
                _mainScreenViewModelState.value = _mainScreenViewModelState.value.copy(
                    scaffoldState = _mainScreenViewModelState.value.scaffoldState.copy(
                        bottomBarState = event.state
                    )
                )
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
            is MainScreenEvent.UserInteracted -> {
                repo.recordUserInteraction()
            }
            is MainScreenEvent.GetAuthState -> {
                repo.getAuthState(viewModelScope)
            }
        }
    }
}


data class MainScreenViewModelState(
    val scaffoldState: ScaffoldState = ScaffoldState(),
    val authRepo: AuthRepository,
    val searchedText: MutableStateFlow<String> = MutableStateFlow(""),
    val dreamTokens: StateFlow<Int> = authRepo.dreamTokens,
    val backgroundResource: Int = R.drawable.background_during_day,
)
data class ScaffoldState (
    val bottomBarState: Boolean = true,
    val topBarState: Boolean = true,
    val floatingActionButtonState: Boolean = true,
    val isUserSearching : Boolean = false,
    val snackBarHostState: MutableState<SnackbarHostState> = mutableStateOf(SnackbarHostState()),
)