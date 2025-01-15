package org.ballistic.dreamjournalai.shared.dream_symbols.presentation.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.core.Resource
import org.ballistic.dreamjournalai.shared.core.domain.DictionaryRepository
import org.ballistic.dreamjournalai.shared.core.domain.VibratorUtil
import org.ballistic.dreamjournalai.shared.dream_authentication.domain.repository.AuthRepository
import org.ballistic.dreamjournalai.shared.dream_symbols.domain.SymbolEvent

class DictionaryScreenViewModel(
    private val authRepository: AuthRepository,
    private val dictionaryRepository: DictionaryRepository,
    private val vibratorUtil: VibratorUtil
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
                viewModelScope.launch {
                    filterWordsByLetter(event.letter)
                }
            }
            is SymbolEvent.GetDreamTokens -> {
                viewModelScope.launch {
                    authRepository.addDreamTokensFlowListener().collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                _symbolScreenState.update {
                                    it.copy(
                                        dreamTokens = resource.data?.toInt() ?: 0
                                    )
                                }
                            }

                            is Resource.Error -> {
                                // Handle error
                            }

                            is Resource.Loading -> {
                                // Handle loading state if needed
                            }
                        }
                    }
                }
            }
            is SymbolEvent.AdSymbolToggle -> {
                _symbolScreenState.update {
                    it.copy(
                        isAdSymbol = event.bool
                    )
                }
            }
            is SymbolEvent.TriggerVibration -> {
                viewModelScope.launch {
                    vibratorUtil.triggerVibration()
                }
            }
            is SymbolEvent.CancelVibration -> {
                viewModelScope.launch {
                    vibratorUtil.cancelVibration()
                }
            }
        }
    }

    private fun handleUnlockWord(event: SymbolEvent.ClickBuySymbol) {
        if (event.isAd) {
            unlockWordWithAd(event.dictionaryWord)
        } else {
            unlockWordWithTokens(event.dictionaryWord)
        }
    }

    private fun unlockWordWithAd(dictionaryWord: DictionaryWord) {
        viewModelScope.launch {
            processUnlockWordResult(
                result = authRepository.unlockWord(dictionaryWord.word, 0),
                dictionaryWord = dictionaryWord
            )
        }
    }

    private fun unlockWordWithTokens(dictionaryWord: DictionaryWord) {
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
        val filtered = _symbolScreenState.value.dictionaryWordList.filter {
            it.word.startsWith(letter, ignoreCase = true)
        }
        _symbolScreenState.value = _symbolScreenState.value.copy(
            filteredWordsByLetter = filtered.toMutableList(),
            selectedLetter = letter
        )
    }


    private fun loadWords() {
        viewModelScope.launch(Dispatchers.IO) {
            // Use the repository function you created
            val words = dictionaryRepository.loadDictionaryWordsFromCsv("dream_dictionary.csv")

            _symbolScreenState.update { state ->
                state.copy(
                    dictionaryWordList = words.toMutableList()
                )
            }
            filterWordsByLetter('A')
        }
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
    val isSearching: Boolean = false,
    val isAdSymbol: Boolean = false
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