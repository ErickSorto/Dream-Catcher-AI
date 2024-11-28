package org.ballistic.dreamjournalai.dream_nightmares.presentation

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.dream_nightmares.NightmareEvent
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.feature_dream.domain.util.OrderType
import javax.inject.Inject

@HiltViewModel
class DreamNightmareScreenViewModel @Inject constructor(
    private val dreamUseCases: DreamUseCases,
) : ViewModel() {

    private val _dreamNightmareScreenState = MutableStateFlow(DreamNightmareScreenState())
    val dreamNightmareScreenState: StateFlow<DreamNightmareScreenState> = _dreamNightmareScreenState

    private var recentlyDeletedDream: Dream? = null
    private var getDreamJob: Job? = null

    fun onEvent(event: NightmareEvent) {
        when (event) {
            is NightmareEvent.LoadDreams -> {
                viewModelScope.launch {
                    getDreams()
                }
            }
            is NightmareEvent.DeleteDream -> {
                viewModelScope.launch {
                    dreamUseCases.deleteDream(event.dream)
                    recentlyDeletedDream = event.dream
                }
            }
            is NightmareEvent.RestoreDream -> {
                viewModelScope.launch {
                    val dreamToRestore = recentlyDeletedDream?.copy(generatedImage = "") ?: return@launch
                    dreamUseCases.addDream(dreamToRestore)
                    recentlyDeletedDream = null
                }
            }

            is NightmareEvent.DreamToDelete ->  {
                viewModelScope.launch {
                    _dreamNightmareScreenState.value = dreamNightmareScreenState.value.copy(
                        dreamToDelete = event.dream
                    )
                }
            }

            is NightmareEvent.ToggleBottomDeleteCancelSheetState ->  {
                viewModelScope.launch {
                    _dreamNightmareScreenState.value = dreamNightmareScreenState.value.copy(
                        bottomDeleteCancelSheetState = event.bottomDeleteCancelSheetState
                    )
                }
            }
        }
    }


    private fun getDreams() {
        // Logging the start of the function
        Log.d("getDreams", "Starting to fetch dreams")

        getDreamJob?.cancel()
        getDreamJob = dreamUseCases.getDreams(OrderType.Date)
            .onEach { dreams ->
                // Logging when dreams are received
                Log.d("getDreams", "Received dreams: ${dreams.size} items")

                _dreamNightmareScreenState.value = dreamNightmareScreenState.value.copy(
                    dreams = dreams,
                    dreamNightmareList = dreams.filter { it.isNightmare }
                )
                Log.d("getDreams", "Dreams updated in the screen state")
            }
            .catch { exception ->
                // Logging in case of an error
                Log.e("getDreams", "Error fetching dreams", exception)
            }
            .launchIn(viewModelScope)


        // Logging the event trigger
        Log.d("getDreams", "LoadStatistics event triggered")
    }
}

@Stable
data class DreamNightmareScreenState(
    val dreams: List<Dream> = emptyList(),
    val dreamNightmareList: List<Dream> = emptyList(),
    val dreamToDelete: Dream? = null,
    val bottomDeleteCancelSheetState: Boolean = false,
    val recentlyDeletedDream: Dream? = null,
)