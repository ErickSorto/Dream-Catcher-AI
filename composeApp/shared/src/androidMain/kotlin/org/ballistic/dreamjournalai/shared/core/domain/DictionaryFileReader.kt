package org.ballistic.dreamjournalai.shared.core.domain

import android.content.Context

actual class DictionaryFileReader(
    private val context: Context
) {
    actual fun readDictionaryWordsFromCsv(fileName: String): List<String> {
        val result = mutableListOf<String>()
        try {
            context.assets.open(fileName).bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    result.add(line)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}