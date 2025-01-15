package org.ballistic.dreamjournalai.shared.dream_statistics.presentation.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.core.domain.DictionaryRepository
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.util.OrderType
import org.ballistic.dreamjournalai.shared.dream_statistics.StatisticEvent
import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.viewmodel.DictionaryWord


class DreamStatisticScreenViewModel(
    private val dreamUseCases: DreamUseCases,
    private val dictionaryRepository: DictionaryRepository,
) : ViewModel() {

    private val _dreamStatisticScreen = MutableStateFlow(DreamStatisticScreenState())
    val dreamStatisticScreen: StateFlow<DreamStatisticScreenState> = _dreamStatisticScreen

    private var getDreamJob: Job? = null

    fun onEvent(event: StatisticEvent) {
        when (event) {
            is StatisticEvent.LoadDreams -> {
                viewModelScope.launch {
                    getDreams()
                    onEvent(StatisticEvent.LoadDictionary)
                }
            }

            is StatisticEvent.LoadDictionary -> {
                viewModelScope.launch {
                    loadWords()
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
            }
        }
    }

    private fun loadWords() {
        viewModelScope.launch(Dispatchers.IO) {
            // Use the repository function you created
            val words = dictionaryRepository.loadDictionaryWordsFromCsv("dream_dictionary.csv")

            _dreamStatisticScreen.update {
                it.copy(
                    dictionaryWordMutableList = words,
                )
            }
            //top 6 words in dreams
            val topSixWordsInDreams = _dreamStatisticScreen.value.dictionaryWordMutableList
                .map { dictionaryWord ->
                    dictionaryWord to _dreamStatisticScreen.value.dreams.count { dream ->
                        dream.content.contains(dictionaryWord.word, ignoreCase = true)
                    }
                }
                .sortedByDescending { it.second }
                .take(6)
                .toMap()

            _dreamStatisticScreen.update {
                it.copy(
                    topSixWordsInDreams = topSixWordsInDreams
                )
            }
        }
    }


    private fun getDreams() {
        getDreamJob?.cancel()
        getDreamJob = dreamUseCases.getDreams(OrderType.Date)
            .onEach { dreams ->

                _dreamStatisticScreen.value = dreamStatisticScreen.value.copy(
                    dreams = dreams
                )
                onEvent(StatisticEvent.LoadStatistics)
            }
            .catch { exception ->
            }
            .launchIn(viewModelScope)
    }

}

@Stable
data class DreamStatisticScreenState(
    val dreams: List<Dream> = emptyList(),
    val topSixWordsInDreams: Map<DictionaryWord, Int> = mapOf(),
    val dictionaryWordMutableList: List<DictionaryWord> = emptyList(),
    val totalLucidDreams: Int = 0,
    val totalNormalDreams: Int = 0,
    val totalNightmares: Int = 0,
    val totalDreams: Int = 0,
    val totalFavoriteDreams: Int = 0,
    val totalRecurringDreams: Int = 0,
    val totalFalseAwakenings: Int = 0,
    val isDreamWordFilterLoading: Boolean = true,
)