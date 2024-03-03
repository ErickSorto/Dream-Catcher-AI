package org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.feature_dream.domain.util.OrderType
import javax.inject.Inject

@HiltViewModel
class InterpretDreamsViewModel @Inject constructor(
    private val dreamUseCases: DreamUseCases
) : ViewModel() {
    private val _interpretDreamsScreenState = MutableStateFlow(InterpretDreamsScreenState())
    val interpretDreamsScreenState: StateFlow<InterpretDreamsScreenState> =
        _interpretDreamsScreenState.asStateFlow()

    private var getDreamJob: Job? = null
    fun onEvent(event: InterpretDreamsToolEvent) {
        when (event) {
            is InterpretDreamsToolEvent.GetDreams -> {
                viewModelScope.launch {
                    getDreams()
                }
            }
            is InterpretDreamsToolEvent.AddDreamToInterpretationList -> {
                viewModelScope.launch {
                    addDreamToInterpretationList(event.dream)
                }
            }
            is InterpretDreamsToolEvent.RemoveDreamFromInterpretationList -> {
                viewModelScope.launch {
                    removeDreamFromInterpretationList(event.dream)
                }
            }
        }
    }

    private fun addDreamToInterpretationList(dream: Dream) {
        // Logging the start of the function
        Log.d("addDreamToInterpretationList", "Starting to add dream to interpretation list")

        // Getting the current list of dreams
        val currentDreams = interpretDreamsScreenState.value.chosenDreams.toMutableList()

        // Adding the dream to the list
        currentDreams.add(dream)

        // Updating the screen state
        _interpretDreamsScreenState.value = interpretDreamsScreenState.value.copy(
            chosenDreams = currentDreams
        )

        // Logging the event trigger
        Log.d("addDreamToInterpretationList", "AddDreamToInterpretationList event triggered")
    }

    private fun removeDreamFromInterpretationList(dream: Dream) {
        // Logging the start of the function
        Log.d("removeDreamFromInterpretationList", "Starting to remove dream from interpretation list")

        // Getting the current list of dreams
        val currentDreams = interpretDreamsScreenState.value.chosenDreams.toMutableList()

        // Removing the dream from the list
        currentDreams.remove(dream)

        // Updating the screen state
        _interpretDreamsScreenState.value = interpretDreamsScreenState.value.copy(
            chosenDreams = currentDreams
        )

        // Logging the event trigger
        Log.d("removeDreamFromInterpretationList", "RemoveDreamFromInterpretationList event triggered")
    }


    private fun getDreams() {
        // Logging the start of the function
        Log.d("getDreams", "Starting to fetch dreams")

        getDreamJob?.cancel()
        getDreamJob = dreamUseCases.getDreams(OrderType.Date)
            .onEach { dreams ->
                // Logging when dreams are received
                Log.d("getDreams", "Received dreams: ${dreams.size} items")

                _interpretDreamsScreenState.value = interpretDreamsScreenState.value.copy(
                    dreams = dreams
                )
                // Logging after updating the screen state
                Log.d("getDreams", "Updated randomDreamScreen with new dreams")
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

data class InterpretDreamsScreenState(
    val dreams: List<Dream> = emptyList(),
    val chosenDreams: List<Dream> = emptyList(),
)