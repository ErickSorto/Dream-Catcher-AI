package org.ballistic.dreamjournalai.dream_statistics.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.dream_dictionary.presentation.viewmodel.DictionaryWord
import org.ballistic.dreamjournalai.dream_statistics.StatisticEvent
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.feature_dream.domain.util.OrderType
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DreamStatisticScreenViewModel @Inject constructor(
    private val dreamUseCases: DreamUseCases,
    private val application: Application,
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
            val dictionaryWordList = readDictionaryWordsFromCsv(application.applicationContext)

            _dreamStatisticScreen.update {
                it.copy(
                    dictionaryWordMutableList = dictionaryWordList,
                )
            }
            _dreamStatisticScreen.update {
                it.copy(
                    topFiveWordsInDreams = dictionaryWordsInDreamFilterList()
                )
            }
            Log.d("topFiveWordsInDreams", "topFiveWordsInDreams: ${_dreamStatisticScreen.value.topFiveWordsInDreams}")
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
        _dreamStatisticScreen.value = dreamStatisticScreen.value.copy(
            isDreamWordFilterLoading = true
        )
        val wordCounts = mutableMapOf<DictionaryWord, Int>()
        val dictionaryWords = dreamStatisticScreen.value.dictionaryWordMutableList
        val suffixes = listOf("ing", "ed", "er", "est", "s", "y")

        for (dream in dreamStatisticScreen.value.dreams) {
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

        _dreamStatisticScreen.value = dreamStatisticScreen.value.copy(
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

data class DreamStatisticScreenState(
    val dreams: List<Dream> = emptyList(),
    val topFiveWordsInDreams: Map<DictionaryWord, Int> = mapOf(),
    val dictionaryWordMutableList: List<DictionaryWord> = emptyList(),
    val totalLucidDreams: Int = 0,
    val totalNormalDreams: Int = 0,
    val totalNightmares: Int = 0,
    val totalDreams: Int = 0,
    val totalFavoriteDreams: Int = 0,
    val totalRecurringDreams: Int = 0,
    val totalFalseAwakenings: Int = 0,
    val isDreamWordFilterLoading: Boolean = false,
)