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
    val dictionaryScreenState: StateFlow<DictionaryScreenState> = _dictionaryScreenState.asStateFlow()

    fun onEvent(event: DictionaryEvent) = viewModelScope.launch{
        when (event) {
            is DictionaryEvent.LoadWords -> {
                Log.d("DictionaryScreen", "Loading words")
                    loadWords()
            }
            is DictionaryEvent.ClickWord -> {
                _dictionaryScreenState.update {state ->
                    state.copy(
                        bottomSheetState = mutableStateOf(true),
                        isClickedWordUnlocked = event.isUnlocked,
                        clickedWord = event.word
                    )
                }
            }

            is DictionaryEvent.ClickBuyWord -> {
                if (event.isAd) {
                    runAd(
                        activity = event.activity,
                        onRewardedAd = {
                            Log.d("DictionaryScreen", "Ad was rewarded")
                            _dictionaryScreenState.update { state ->
                                state.copy(
                                    bottomSheetState = mutableStateOf(false),
                                    isClickedWordUnlocked = true,
                                    clickedWord = event.word
                                )
                            }
                            viewModelScope.launch {
                                val result = authRepository.unlockWord(event.word)
                                if (result is Resource.Error) {
                                    _dictionaryScreenState.value.snackBarHostState.value.showSnackbar(
                                        message = "Error unlocking word",
                                        actionLabel = "Dismiss"
                                    )
                                }
                            }
                        },
                        onAdFailed = {
                            Log.d("DictionaryScreen", "Ad failed")
                        }
                    )
                } else {
                    Log.d("DictionaryScreen", "Buying word")
                    _dictionaryScreenState.update { state ->
                        state.copy(
                            bottomSheetState = mutableStateOf(false),
                            isClickedWordUnlocked = true,
                            clickedWord = event.word
                        )
                    }
                }
            }

            is DictionaryEvent.ClickUnlock -> {

            }

            is DictionaryEvent.FilterByLetter -> {
                Log.d("DictionaryScreen", "Filtering words by letter: ${event.letter}")
                viewModelScope.launch {
                    filterWordsByLetter(event.letter)
                }
            }
        }
    }

    private fun filterWordsByLetter(letter: Char) {
        //dictionary size
        Log.d("DictionaryScreen", "Dictionary size: ${_dictionaryScreenState.value.dictionaryWordMutableList.size}")
        val filtered = _dictionaryScreenState.value.dictionaryWordMutableList.filter {
            it.word.startsWith(letter, ignoreCase = true)
        }
        _dictionaryScreenState.value = _dictionaryScreenState.value.copy(
            filteredWords = filtered.toMutableList(),
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
                    dictionaryWordMutableList = words.toMutableList(),
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
                        val cost = tokens.last().toIntOrNull() ?: 0 // Assuming cost is the last token
                        words.add(
                            DictionaryWord(
                                word = tokens.first(), // Assuming word is the first token
                                definition = tokens.drop(1).dropLast(1).joinToString(","), // Joining all tokens that are part of the definition
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
    val dictionaryWordMutableList: MutableList<DictionaryWord> = mutableListOf(),
    val filteredWords: MutableList<DictionaryWord> = mutableListOf(),
    val selectedLetter: Char = 'A',
    val bottomSheetState: MutableState<Boolean> = mutableStateOf(false),
    val isClickedWordUnlocked: Boolean = false,
    val dreamTokens: StateFlow<Int> = authRepository.dreamTokens,
    val clickedWord: String = "",
    val snackBarHostState: MutableState<SnackbarHostState> = mutableStateOf(SnackbarHostState()),
)
data class DictionaryWord(
    val word: String,
    val definition: String,
    val isUnlocked: Boolean,
    val cost: Int
)