package org.ballistic.dreamjournalai.shared.dream_tools.presentation.dream_tools_screen.viewmodel

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
import org.ballistic.dreamjournalai.shared.dream_tools.domain.event.ToolsEvent
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.util.OrderType
import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.viewmodel.DictionaryWord

class DreamToolsScreenViewModel(
    private val dreamUseCases: DreamUseCases,
    private val dictionaryRepository: DictionaryRepository,
) : ViewModel() {

    private val _dreamToolsScreen = MutableStateFlow(DreamToolsScreenState())
    val dreamToolsScreen: StateFlow<DreamToolsScreenState> = _dreamToolsScreen

    private var getDreamJob: Job? = null

    fun onEvent(event: ToolsEvent) {
        when (event) {
            is ToolsEvent.LoadDreams -> {
                viewModelScope.launch {
                    getDreams()
                    onEvent(ToolsEvent.LoadDictionary)
                }
            }

            is ToolsEvent.LoadDictionary -> {
                viewModelScope.launch {
                    loadWords()
                }
            }

            is ToolsEvent.LoadStatistics -> {
                viewModelScope.launch {
                    _dreamToolsScreen.value = _dreamToolsScreen.value.copy(
                        totalLucidDreams = _dreamToolsScreen.value.dreams.count { it.isLucid },
                        totalNormalDreams = _dreamToolsScreen.value.dreams.count {
                            !it.isLucid && !it.isNightmare && !it.isRecurring
                        },
                        totalNightmares = _dreamToolsScreen.value.dreams.count { it.isNightmare },
                        totalDreams = _dreamToolsScreen.value.dreams.size,
                        totalFavoriteDreams = _dreamToolsScreen.value.dreams.count { it.isFavorite },
                        totalRecurringDreams = _dreamToolsScreen.value.dreams.count { it.isRecurring },
                        totalFalseAwakenings = _dreamToolsScreen.value.dreams.count { it.falseAwakening }
                    )
                }
            }
            is ToolsEvent.ChooseRandomDream -> {
                viewModelScope.launch {
                    val randomDream = _dreamToolsScreen.value.dreams.randomOrNull()
                    if (randomDream != null) {
                        _dreamToolsScreen.value = _dreamToolsScreen.value.copy(
                            randomDream = randomDream
                        )
                    }
                }
            }
        }
    }

    private fun loadWords() {
        viewModelScope.launch(Dispatchers.IO) {
            // Use the repository function you created
            val words = dictionaryRepository.loadDictionaryWordsFromCsv("dream_dictionary.csv")

            _dreamToolsScreen.update {
                it.copy(
                    dictionaryWordMutableList = words,
                )
            }

            //top 6 words in dreams
            val topSixWordsInDreams = _dreamToolsScreen.value.dictionaryWordMutableList
                .map { dictionaryWord ->
                    dictionaryWord to _dreamToolsScreen.value.dreams.count { dream ->
                        dream.content.contains(dictionaryWord.word, ignoreCase = true)
                    }
                }
                .sortedByDescending { it.second }
                .take(6)
                .toMap()

            _dreamToolsScreen.update {
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

                _dreamToolsScreen.value = dreamToolsScreen.value.copy(
                    dreams = dreams
                )
                onEvent(ToolsEvent.LoadStatistics)
            }
            .catch { exception ->
                // Handle the exception
            }
            .launchIn(viewModelScope)
    }

}

@Stable
data class DreamToolsScreenState(
    val dreams: List<Dream> = emptyList(),
    val randomDream: Dream? = null,
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