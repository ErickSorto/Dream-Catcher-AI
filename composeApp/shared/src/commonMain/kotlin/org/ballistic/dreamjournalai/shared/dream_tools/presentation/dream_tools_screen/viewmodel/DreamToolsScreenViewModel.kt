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
import org.ballistic.dreamjournalai.shared.dream_tools.domain.event.ToolsEvent
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.util.OrderType
import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.viewmodel.DictionaryWord

class DreamToolsScreenViewModel(
    private val dreamUseCases: DreamUseCases,
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
            val dictionaryWordList = readDictionaryWordsFromCsv(application.applicationContext)

            _dreamToolsScreen.update {
                it.copy(
                    dictionaryWordMutableList = dictionaryWordList,
                )
            }
            _dreamToolsScreen.update {
                it.copy(
                    topSixWordsInDreams = dictionaryWordsInDreamFilterList()
                )
            }
            Log.d("topSixWordsInDreams", "topSixWordsInDreams: ${_dreamToolsScreen.value.topSixWordsInDreams}")
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

                _dreamToolsScreen.value = dreamToolsScreen.value.copy(
                    dreams = dreams
                )
                onEvent(ToolsEvent.LoadStatistics)
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

    private fun readDictionaryWordsFromCsv(context: Context): List<DictionaryWord> {
        val words = mutableListOf<DictionaryWord>()
        val csvRegex = """"(.*?)"|([^,]+)""".toRegex() // Matches quoted strings or unquoted tokens
        try {
            context.assets.open("dream_dictionary.csv").bufferedReader().useLines { lines ->
                lines.drop(1).forEach { line ->
                    val tokens = csvRegex.findAll(line).map { it.value.trim('"') }.toList()
                    if (tokens.size >= 3) {
                        val cost =
                            tokens.last().toIntOrNull() ?: 0 // Assuming cost is the last token
                        words.add(
                            DictionaryWord(
                                word = tokens.first(), // Assuming word is the first token
                                definition = tokens.drop(1).dropLast(1)
                                    .joinToString(","), // Joining all tokens that are part of the definition
                                isUnlocked = cost == 0, // If cost is 0, then the word is unlocked
                                cost = cost
                            )
                        )
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return words
    }

    private fun dictionaryWordsInDreamFilterList(): Map<DictionaryWord, Int> {
        _dreamToolsScreen.value = dreamToolsScreen.value.copy(
            isDreamWordFilterLoading = true
        )
        val wordCounts = mutableMapOf<DictionaryWord, Int>()
        val dictionaryWords = dreamToolsScreen.value.dictionaryWordMutableList
        val suffixes = listOf("ing", "ed", "er", "est", "s", "y")

        for (dream in dreamToolsScreen.value.dreams) {
            val dreamContent = dream.content.lowercase(Locale.ROOT)
            val dreamWords =
                dreamContent.split("\\s+".toRegex()).map { it.trim('.', '?', '\"', '\'') }

            for (dreamWord in dreamWords) {
                if (dreamWord.isNotEmpty() && dreamWord.length > 2) {
                    for (dictionary in dictionaryWords) {
                        val dictionaryWordLower = dictionary.word.lowercase(Locale.getDefault())
                        val possibleMatches = generatePossibleMatches(dreamWord, suffixes)

                        if (possibleMatches.contains(dictionaryWordLower)) {
                            wordCounts[dictionary] = wordCounts.getOrDefault(dictionary, 0) + 1
                        } else {
                            val baseForm = removeSuffixes(dreamWord, suffixes)
                            if (baseForm == dictionaryWordLower) {
                                wordCounts[dictionary] = wordCounts.getOrDefault(dictionary, 0) + 1
                            }
                        }
                    }
                }
            }
        }

        _dreamToolsScreen.value = dreamToolsScreen.value.copy(
            isDreamWordFilterLoading = false
        )

        // Sorting and getting the top five words as map word count already map
        return wordCounts.entries.sortedByDescending { it.value }.take(6).associate { it.toPair() }
    }

    private fun generatePossibleMatches(baseWord: String, suffixes: List<String>): Set<String> {
        val matches = mutableSetOf<String>()
        if (baseWord.isNotEmpty()) {
            matches.add(baseWord) // Add the base word itself

            if (baseWord.length <= 3) {
                suffixes.forEach { suffix ->
                    matches.add(baseWord + baseWord.last() + suffix)
                }
            } else {
                suffixes.forEach { suffix ->
                    matches.add(baseWord + suffix)
                    matches.add(baseWord + baseWord.last() + suffix)
                    if (baseWord.last() != suffix.first()) {
                        matches.add(baseWord.dropLast(1) + suffix)
                    }
                }
            }
        }
        return matches
    }

    private fun removeSuffixes(word: String, suffixes: List<String>): String {
        var baseForm = word
        suffixes.forEach { suffix ->
            if (word.endsWith(suffix)) {
                baseForm = word.removeSuffix(suffix)
                return@forEach
            }
        }
        return baseForm
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