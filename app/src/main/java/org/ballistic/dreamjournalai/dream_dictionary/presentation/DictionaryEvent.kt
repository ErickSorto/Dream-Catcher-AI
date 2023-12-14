package org.ballistic.dreamjournalai.dream_dictionary.presentation

import android.app.Activity
import org.ballistic.dreamjournalai.ad_feature.domain.AdCallback

sealed class DictionaryEvent {

    data object LoadWords : DictionaryEvent()
    data class ClickWord(val word: String, val cost: Int, val isUnlocked: Boolean) : DictionaryEvent()
    data class ClickUnlock(val word: String, val cost: Int) : DictionaryEvent()
    data class FilterByLetter(val letter: Char) : DictionaryEvent()

    data class ClickBuyWord(val word: String, val cost: Int = 0, val isAd: Boolean, val activity: Activity) : DictionaryEvent()

}

