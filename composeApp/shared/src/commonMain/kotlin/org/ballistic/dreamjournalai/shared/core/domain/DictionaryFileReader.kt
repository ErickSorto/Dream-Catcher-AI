package org.ballistic.dreamjournalai.shared.core.domain

expect class DictionaryFileReader {
    fun readDictionaryWordsFromCsv(fileName: String): List<String>
}