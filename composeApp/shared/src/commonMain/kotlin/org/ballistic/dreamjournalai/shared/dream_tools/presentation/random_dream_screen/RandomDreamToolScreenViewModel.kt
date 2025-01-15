package org.ballistic.dreamjournalai.shared.dream_tools.presentation.random_dream_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.core.domain.VibratorUtil
import org.ballistic.dreamjournalai.shared.dream_tools.domain.event.RandomToolEvent
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.util.OrderType

class RandomDreamToolScreenViewModel(
    private val dreamUseCases: DreamUseCases,
    private val vibratorUtil: VibratorUtil
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
            is RandomToolEvent.TriggerVibration -> {
                viewModelScope.launch {
                    vibratorUtil.triggerVibration()
                }
            }
        }
    }

    private fun getRandomDream() {
        //catching the exception
        try {
            //getting the random dream
            val randomDream = randomDreamToolScreenState.value.dreams.random()
            //updating the screen state
            _randomDreamToolScreenState.value = randomDreamToolScreenState.value.copy(
                randomDream = randomDream
            )
        } catch (e: Exception) {
            //TODO: handle exception
        }
    }

    private fun getDreams() {
        getDreamJob?.cancel()
        getDreamJob = dreamUseCases.getDreams(OrderType.Date)
            .onEach { dreams ->

                _randomDreamToolScreenState.value = randomDreamToolScreenState.value.copy(
                    dreams = dreams
                )
            }
            .catch { exception ->
            }
            .launchIn(viewModelScope)
    }
}

data class RandomDreamToolScreenState(
    val dreams: List<Dream> = emptyList(),
    val randomDream: Dream? = null,
)