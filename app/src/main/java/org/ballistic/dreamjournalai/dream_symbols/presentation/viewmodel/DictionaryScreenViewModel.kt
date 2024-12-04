package org.ballistic.dreamjournalai.dream_symbols.presentation.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.rewarded.RewardItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.dream_ad.domain.AdCallback
import org.ballistic.dreamjournalai.dream_ad.domain.AdManagerRepository
import org.ballistic.dreamjournalai.dream_authentication.domain.repository.AuthRepository
import org.ballistic.dreamjournalai.dream_symbols.domain.SymbolEvent
import java.io.IOException

class DictionaryScreenViewModel(
    private val adManagerRepository: AdManagerRepository,
    private val authRepository: AuthRepository,
    private val application: Application,
) : ViewModel() {

    private val _symbolScreenState = MutableStateFlow(SymbolScreenState(authRepository))
    val symbolScreenState: StateFlow<SymbolScreenState> =
        _symbolScreenState.asStateFlow()

    @OptIn(ExperimentalFoundationApi::class)
    private val _searchTextFieldState = MutableStateFlow(TextFieldState())

    @OptIn(ExperimentalFoundationApi::class)
    val searchTextFieldState: StateFlow<TextFieldState> = _searchTextFieldState.asStateFlow()

    fun onEvent(event: SymbolEvent) = viewModelScope.launch {
        when (event) {
            is SymbolEvent.LoadWords -> {
                loadWords()
            }

            is SymbolEvent.ClickWord -> {
                _symbolScreenState.update { state ->
                    state.copy(
                        bottomSheetState = mutableStateOf(true),
                        isClickedWordUnlocked = event.dictionaryWord.cost == 0,
                        clickedSymbol = event.dictionaryWord
                    )
                }
            }

            is SymbolEvent.ListenForSearchChange -> {
                viewModelScope.launch {
                    filterBySearchedWord()
                }
            }

            is SymbolEvent.SetSearchingState -> {
                _symbolScreenState.update { state ->
                    state.copy(
                        isSearching = event.state
                    )
                }
            }

            is SymbolEvent.ClickBuySymbol -> handleUnlockWord(event)

            is SymbolEvent.GetUnlockedWords -> {
                Log.d("DictionaryScreen", "Getting unlocked words")
                viewModelScope.launch {
                    authRepository.getUnlockedWords().collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                // Handle loading state if needed
                            }

                            is Resource.Success -> {
                                _symbolScreenState.update { state ->
                                    state.copy(
                                        unlockedWords = result.data?.toMutableList()
                                            ?: mutableListOf()
                                    )
                                }
                            }

                            is Resource.Error -> {
                                Log.d("DictionaryScreen", "Error getting unlocked words")
                                viewModelScope.launch {
                                    _symbolScreenState.value.snackBarHostState.value.showSnackbar(
                                        message = "${result.message}",
                                        actionLabel = "Dismiss"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is SymbolEvent.FilterByLetter -> {
                Log.d("DictionaryScreen", "Filtering words by letter: ${event.letter}")
                viewModelScope.launch {
                    filterWordsByLetter(event.letter)
                }
            }
        }
    }

    private fun handleUnlockWord(event: SymbolEvent.ClickBuySymbol) {
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
                _symbolScreenState.value.bottomSheetState.value = false
                _symbolScreenState.value.snackBarHostState.value.showSnackbar(
                    message = "${result.message}",
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
        _symbolScreenState.update { state ->
            val newList = state.unlockedWords.toMutableList().apply {
                add(dictionaryWord.word)
            }
            state.copy(
                isClickedWordUnlocked = true,
                clickedSymbol = dictionaryWord,
                unlockedWords = newList
            )
        }
    }

    private fun filterWordsByLetter(letter: Char) {
        //dictionary size
        Log.d(
            "DictionaryScreen",
            "Dictionary size: ${_symbolScreenState.value.dictionaryWordList.size}"
        )
        val filtered = _symbolScreenState.value.dictionaryWordList.filter {
            it.word.startsWith(letter, ignoreCase = true)
        }
        _symbolScreenState.value = _symbolScreenState.value.copy(
            filteredWordsByLetter = filtered.toMutableList(),
            selectedLetter = letter
        )
        Log.d("DictionaryScreen", "Filtered words: ${filtered.size}")
    }

    private fun loadWords() {
        viewModelScope.launch(Dispatchers.IO) {
            val words = readDictionaryWordsFromCsv(application.applicationContext)
            Log.d("DictionaryScreen", "Loaded words: ${words.size}")

            _symbolScreenState.update { state ->
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

    private suspend fun filterBySearchedWord() {
        snapshotFlow {
            searchTextFieldState.value.text
        }.collect { text ->
            val searchedText = text.trim()
            val filteredWords = _symbolScreenState.value.dictionaryWordList.filter {
                it.doesMatchSearchQuery(searchedText.toString())
            }
            _symbolScreenState.update { state ->
                state.copy(
                    filteredSearchedWords = filteredWords.toMutableList()
                )
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

@Stable
data class SymbolScreenState(
    val authRepository: AuthRepository,
    val dictionaryWordList: List<DictionaryWord> = emptyList(),
    val unlockedWords: List<String> = emptyList(),
    val filteredWordsByLetter: List<DictionaryWord> = emptyList(),
    val filteredSearchedWords: List<DictionaryWord> = emptyList(),
    val selectedLetter: Char = 'A',
    val bottomSheetState: MutableState<Boolean> = mutableStateOf(false),
    val isClickedWordUnlocked: Boolean = false,
    val dreamTokens: Int = 0,
    val clickedSymbol: DictionaryWord = DictionaryWord("", "", false, 0),
    val snackBarHostState: MutableState<SnackbarHostState> = mutableStateOf(SnackbarHostState()),
    val isSearching: Boolean = false
)

@Stable
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