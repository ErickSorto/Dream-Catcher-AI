package org.ballistic.dreamjournalai.dream_dictionary.presentation

import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent

sealed class DictionaryEvent {

    data object LoadWords : DictionaryEvent()
    data class ClickWord(val word: String, val cost: Int) : DictionaryEvent()
    data class ClickUnlock(val word: String, val cost: Int) : DictionaryEvent()
    data class FilterByLetter(val letter: Char) : DictionaryEvent()

}

