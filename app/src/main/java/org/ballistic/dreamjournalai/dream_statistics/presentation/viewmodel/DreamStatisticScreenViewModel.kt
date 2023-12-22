package org.ballistic.dreamjournalai.dream_statistics.presentation.viewmodel

import android.util.Log
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
import org.ballistic.dreamjournalai.dream_statistics.StatisticEvent
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.feature_dream.domain.util.OrderType
import javax.inject.Inject

@HiltViewModel
class DreamStatisticScreenViewModel @Inject constructor(
    private val dreamUseCases: DreamUseCases
) : ViewModel() {

    private val _dreamStatisticScreen = MutableStateFlow(DreamStatisticScreenState())
    val dreamStatisticScreen: StateFlow<DreamStatisticScreenState> = _dreamStatisticScreen

    private var getDreamJob: Job? = null

    fun onEvent(event: StatisticEvent) {
        when (event) {
            is StatisticEvent.LoadDreams -> {
                viewModelScope.launch {
                    getDreams()
                }
            }

            is StatisticEvent.LoadStatistics -> {
                viewModelScope.launch {
                    _dreamStatisticScreen.value = _dreamStatisticScreen.value.copy(
                        totalLucidDreams = _dreamStatisticScreen.value.dreams.count { it.isLucid },
                        totalNormalDreams = _dreamStatisticScreen.value.dreams.count {
                            !it.isLucid && !it.isNightmare && !it.isRecurring
                        },
                        totalNightmares = _dreamStatisticScreen.value.dreams.count { it.isNightmare },
                        totalDreams = _dreamStatisticScreen.value.dreams.size,
                        totalFavoriteDreams = _dreamStatisticScreen.value.dreams.count { it.isFavorite },
                        totalRecurringDreams = _dreamStatisticScreen.value.dreams.count { it.isRecurring },
                        totalFalseAwakenings = _dreamStatisticScreen.value.dreams.count { it.falseAwakening }
                    )
                }
                Log.d("DreamStatisticScreenViewModel", "onEvent: ${_dreamStatisticScreen.value}")
                Log.d("DreamStatisticScreenViewModel", "onEvent: ${_dreamStatisticScreen.value.dreams}")
                Log.d("DreamStatisticScreenViewModel", "onEvent: ${_dreamStatisticScreen.value.totalLucidDreams}")
                Log.d("DreamStatisticScreenViewModel", "onEvent: ${_dreamStatisticScreen.value.totalNormalDreams}")
                Log.d("DreamStatisticScreenViewModel", "onEvent: ${_dreamStatisticScreen.value.totalNightmares}")
                Log.d("DreamStatisticScreenViewModel", "onEvent: ${_dreamStatisticScreen.value.totalDreams}")
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

                _dreamStatisticScreen.value = dreamStatisticScreen.value.copy(
                    dreams = dreams
                )
                onEvent(StatisticEvent.LoadStatistics)
                // Logging after updating the screen state
                Log.d("getDreams", "Updated dreamStatisticScreen with new dreams")
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

data class DreamStatisticScreenState(
    val dreams: List<Dream> = emptyList(),
    val totalLucidDreams: Int = 0,
    val totalNormalDreams: Int = 0,
    val totalNightmares: Int = 0,
    val totalDreams: Int = 0,
    val totalFavoriteDreams: Int = 0,
    val totalRecurringDreams: Int = 0,
    val totalFalseAwakenings: Int = 0,
)