package org.ballistic.dreamjournalai.dream_tools.presentation.random_dream_screen

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
class RandomDreamToolScreenViewModel @Inject constructor(
    private val dreamUseCases: DreamUseCases
) : ViewModel() {
    private val _randomDreamToolScreenState = MutableStateFlow(RandomDreamToolScreenState())
    val randomDreamToolScreenState: StateFlow<RandomDreamToolScreenState> =
        _randomDreamToolScreenState.asStateFlow()

    private var getDreamJob: Job? = null
    fun onEvent(event: RandomToolEvent) {
        when (event) {
            is RandomToolEvent.GetDreams -> {
                viewModelScope.launch {
                    getDreams()
                }
            }
            is RandomToolEvent.GetRandomDream -> {
                viewModelScope.launch {
                    getRandomDream()
                }
            }
        }
    }
    private fun getRandomDream() {
        Log.d("getRandomDream", "Starting to fetch random dream")

        //catching the exception
        try {
            //getting the random dream
            val randomDream = randomDreamToolScreenState.value.dreams.random()
            //updating the screen state
            _randomDreamToolScreenState.value = randomDreamToolScreenState.value.copy(
                randomDream = randomDream
            )

            Log.d("getRandomDream", "Random Dream: ${randomDream.id}")
            Log.d("getRandomDream", "GetRandomDream event triggered")
        } catch (e: Exception) {
            //logging the exception
            Log.e("getRandomDream", "Error fetching random dream", e)

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

                _randomDreamToolScreenState.value = randomDreamToolScreenState.value.copy(
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

data class RandomDreamToolScreenState(
    val dreams: List<Dream> = emptyList(),
    val randomDream: Dream? = null,
)