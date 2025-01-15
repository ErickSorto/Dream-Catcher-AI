package org.ballistic.dreamjournalai.shared.dream_symbols.domain

import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.viewmodel.DictionaryWord

sealed class SymbolEvent {

    data object LoadWords : SymbolEvent()
    data class ClickWord(
        val dictionaryWord: DictionaryWord
    ) : SymbolEvent()

    data object GetUnlockedWords : SymbolEvent()
    data class FilterByLetter(val letter: Char) : SymbolEvent()

    data class ClickBuySymbol(
        val dictionaryWord: DictionaryWord,
        val isAd: Boolean
    ) : SymbolEvent()
    data class AdSymbolToggle(val bool: Boolean) : SymbolEvent()

    data object ListenForSearchChange : SymbolEvent()

    data class SetSearchingState(val state: Boolean) : SymbolEvent()

    data object GetDreamTokens : SymbolEvent()

    data object TriggerVibration : SymbolEvent()

    data object CancelVibration : SymbolEvent()

}

