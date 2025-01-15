package org.ballistic.dreamjournalai.shared.core.domain

import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.viewmodel.DictionaryWord

interface DictionaryRepository {
    fun loadDictionaryWordsFromCsv(fileName: String): List<DictionaryWord>
    fun dictionaryWordsInDreamFilterList(
        dreamContent: String,
        dictionaryWordList: List<DictionaryWord>
    ): List<DictionaryWord>
}