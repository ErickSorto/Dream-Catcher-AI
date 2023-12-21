package org.ballistic.dreamjournalai.dream_dictionary.presentation.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.rewarded.RewardItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.ad_feature.domain.AdCallback
import org.ballistic.dreamjournalai.ad_feature.domain.AdManagerRepository
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.dream_dictionary.presentation.DictionaryEvent
import org.ballistic.dreamjournalai.user_authentication.domain.repository.AuthRepository
import java.io.IOException
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class DictionaryScreenViewModel @Inject constructor(
    private val application: Application,
    private val adManagerRepository: AdManagerRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _dictionaryScreenState = MutableStateFlow(DictionaryScreenState(authRepository))
    val dictionaryScreenState: StateFlow<DictionaryScreenState> =
        _dictionaryScreenState.asStateFlow()

    fun onEvent(event: DictionaryEvent) = viewModelScope.launch {
        when (event) {
            is DictionaryEvent.LoadWords -> {
                Log.d("DictionaryScreen", "Loading words")
                loadWords()
            }

            is DictionaryEvent.ClickWord -> {
                _dictionaryScreenState.update { state ->
                    state.copy(
                        bottomSheetState = mutableStateOf(true),
                        isClickedWordUnlocked = event.dictionaryWord.cost == 0,
                        clickedWord = event.dictionaryWord
                    )
                }
            }

            is DictionaryEvent.ChangeSearchedQuery -> {
                _dictionaryScreenState.update { state ->
                    state.copy(
                        searchedText = MutableStateFlow(event.query)
                    )
                }
                filterBySearchedWord()
            }

            is DictionaryEvent.SetSearchingState -> {
                _dictionaryScreenState.update { state ->
                    filterBySearchedWord()
                    state.copy(
                        isSearching = event.state
                    )
                }
            }

            is DictionaryEvent.ClickBuyWord -> handleUnlockWord(event)

            is DictionaryEvent.GetUnlockedWords -> {
                Log.d("DictionaryScreen", "Getting unlocked words")
                viewModelScope.launch {
                    authRepository.getUnlockedWords().collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                // Handle loading state if needed
                            }

                            is Resource.Success -> {
                                _dictionaryScreenState.update { state ->
                                    state.copy(
                                        unlockedWords = result.data?.toMutableList()
                                            ?: mutableListOf()
                                    )
                                }
                            }

                            is Resource.Error -> {
                                _dictionaryScreenState.value.snackBarHostState.value.showSnackbar(
                                    message = "Error getting unlocked words: ${result.message}",
                                    actionLabel = "Dismiss"
                                )
                            }
                        }
                    }
                }
            }

            is DictionaryEvent.FilterByLetter -> {
                Log.d("DictionaryScreen", "Filtering words by letter: ${event.letter}")
                viewModelScope.launch {
                    filterWordsByLetter(event.letter)
                }
            }
        }
    }

    private fun handleUnlockWord(event: DictionaryEvent.ClickBuyWord) {
        if (event.isAd) {
            runAd(
                activity = event.activity,
                onRewardedAd = { unlockWordWithAd(event.dictionaryWord) },
                onAdFailed = { Log.d("DictionaryScreen", "Ad failed") }
            )
        } else {
            unlockWordWithTokens(event.dictionaryWord)
        }
    }

    private fun unlockWordWithAd(dictionaryWord: DictionaryWord) {
        viewModelScope.launch {
            Log.d("DictionaryScreen", "Unlocking word with ad")
            processUnlockWordResult(
                result = authRepository.unlockWord(dictionaryWord.word, 0),
                dictionaryWord = dictionaryWord
            )
        }
    }

    private fun unlockWordWithTokens(dictionaryWord: DictionaryWord) {
        Log.d("DictionaryScreen", "Unlocking word with dream tokens")
        viewModelScope.launch {
            processUnlockWordResult(
                result = authRepository.unlockWord(dictionaryWord.word, dictionaryWord.cost),
                dictionaryWord = dictionaryWord
            )
        }
    }

    private suspend fun processUnlockWordResult(
        result: Resource<Boolean>,
        dictionaryWord: DictionaryWord
    ) {
        when (result) {
            is Resource.Error -> {
                _dictionaryScreenState.value.snackBarHostState.value.showSnackbar(
                    message = "Error unlocking word: ${result.message}",
                    actionLabel = "Dismiss"
                )
            }

            is Resource.Success -> {
                updateScreenStateForUnlockedWord(dictionaryWord)
                Log.d("DictionaryScreen", "Word unlocked successfully")
            }

            is Resource.Loading -> {
                // Handle loading state if needed
            }
        }
    }

    private fun updateScreenStateForUnlockedWord(dictionaryWord: DictionaryWord) {
        _dictionaryScreenState.update { state ->
            val newList = state.unlockedWords.toMutableList().apply {
                add(dictionaryWord.word)
            }
            state.copy(
                isClickedWordUnlocked = true,
                clickedWord = dictionaryWord,
                unlockedWords = newList
            )
        }
    }

    private fun filterWordsByLetter(letter: Char) {
        //dictionary size
        Log.d(
            "DictionaryScreen",
            "Dictionary size: ${_dictionaryScreenState.value.dictionaryWordList.size}"
        )
        val filtered = _dictionaryScreenState.value.dictionaryWordList.filter {
            it.word.startsWith(letter, ignoreCase = true)
        }
        _dictionaryScreenState.value = _dictionaryScreenState.value.copy(
            filteredWordsByLetter = filtered.toMutableList(),
            selectedLetter = letter
        )
        Log.d("DictionaryScreen", "Filtered words: ${filtered.size}")
    }

    private fun loadWords() {
        viewModelScope.launch(Dispatchers.IO) {
            val words = readDictionaryWordsFromCsv(application.applicationContext)
            Log.d("DictionaryScreen", "Loaded words: ${words.size}")

            _dictionaryScreenState.update { state ->
                state.copy(
                    dictionaryWordList = words.toMutableList(),
                )
            }
            filterWordsByLetter('A')
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterBySearchedWord() {
        viewModelScope.launch {
            _dictionaryScreenState.update { state ->
                val searchText = state.searchedText.value
                val filtered = if (searchText.isBlank()) {
                    state.dictionaryWordList
                } else {
                    state.dictionaryWordList.filter {
                        it.doesMatchSearchQuery(searchText)
                    }
                }
                state.copy(filteredSearchedWords = filtered)
            }
        }
    }

    private fun runAd(
        activity: Activity,
        onRewardedAd: () -> Unit,
        onAdFailed: () -> Unit
    ) {
        activity.runOnUiThread {
            adManagerRepository.loadRewardedAd(activity) {
                //show ad
                adManagerRepository.showRewardedAd(
                    activity,
                    object : AdCallback {
                        override fun onAdClosed() {
                            //to be added later
                        }

                        override fun onAdRewarded(reward: RewardItem) {
                            onRewardedAd()
                        }

                        override fun onAdLeftApplication() {
                            TODO("Not yet implemented")
                        }

                        override fun onAdLoaded() {
                            TODO("Not yet implemented")
                        }

                        override fun onAdFailedToLoad(errorCode: Int) {
                            onAdFailed()
                        }

                        override fun onAdOpened() {
                            TODO("Not yet implemented")
                        }
                    })
            }
        }
    }
}


data class DictionaryScreenState(
    val authRepository: AuthRepository,
    val dictionaryWordList: List<DictionaryWord> = emptyList(),
    val unlockedWords: List<String> = emptyList(),
    val filteredWordsByLetter: List<DictionaryWord> = emptyList(),
    val filteredSearchedWords: List<DictionaryWord> = emptyList(),
    val selectedLetter: Char = 'A',
    val bottomSheetState: MutableState<Boolean> = mutableStateOf(false),
    val isClickedWordUnlocked: Boolean = false,
    val dreamTokens: StateFlow<Int> = authRepository.dreamTokens,
    val clickedWord: DictionaryWord = DictionaryWord("", "", false, 0),
    val snackBarHostState: MutableState<SnackbarHostState> = mutableStateOf(SnackbarHostState()),
    val isSearching: Boolean = false,
    val searchedText: MutableStateFlow<String> = MutableStateFlow(""),
)

data class DictionaryWord(
    val word: String,
    val definition: String,
    val isUnlocked: Boolean,
    val cost: Int
) {
    fun doesMatchSearchQuery(searchQuery: String): Boolean {
        return word.contains(searchQuery, ignoreCase = true)
    }
}