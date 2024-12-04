package org.ballistic.dreamjournalai.dream_symbols.domain

import android.app.Activity
import org.ballistic.dreamjournalai.dream_symbols.presentation.viewmodel.DictionaryWord

sealed class SymbolEvent {

    data object LoadWords : SymbolEvent()
    data class ClickWord(
        val dictionaryWord: DictionaryWord
    ) : SymbolEvent()

    data object GetUnlockedWords : SymbolEvent()
    data class FilterByLetter(val letter: Char) : SymbolEvent()

    data class ClickBuySymbol(
        val dictionaryWord: DictionaryWord,
        val isAd: Boolean,
        val activity: Activity
    ) : SymbolEvent()

    data object ListenForSearchChange : SymbolEvent()

    data class SetSearchingState(val state: Boolean) : SymbolEvent()

    data object GetDreamTokens : SymbolEvent()

}

