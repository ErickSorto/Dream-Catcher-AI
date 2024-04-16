package org.ballistic.dreamjournalai.dream_dictionary.presentation

import android.app.Activity
import org.ballistic.dreamjournalai.dream_dictionary.presentation.viewmodel.DictionaryWord

sealed class DictionaryEvent {

    data object LoadWords : DictionaryEvent()
    data class ClickWord(
        val dictionaryWord: DictionaryWord
    ) : DictionaryEvent()

    data object GetUnlockedWords : DictionaryEvent()
    data class FilterByLetter(val letter: Char) : DictionaryEvent()

    data class ClickBuyWord(
        val dictionaryWord: DictionaryWord,
        val isAd: Boolean,
        val activity: Activity
    ) : DictionaryEvent()

    data object ListenForSearchChange : DictionaryEvent()

    data class SetSearchingState(val state: Boolean) : DictionaryEvent()

}

