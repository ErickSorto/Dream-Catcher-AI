package org.ballistic.dreamjournalai.shared.core.data

import org.ballistic.dreamjournalai.shared.core.domain.DictionaryFileReader
import org.ballistic.dreamjournalai.shared.core.domain.DictionaryRepository
import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.viewmodel.DictionaryWord

class DictionaryRepositoryImpl(
    private val fileReader: DictionaryFileReader
) : DictionaryRepository {

    private val csvRegex = """"(.*?)"|([^,]+)""".toRegex() // Matches quoted strings or unquoted tokens
    private val suffixes = listOf("ing", "ed", "er", "est", "s", "y")

    override fun loadDictionaryWordsFromCsv(fileName: String): List<DictionaryWord> {
        val lines = fileReader.readDictionaryWordsFromCsv(fileName)
        val words = mutableListOf<DictionaryWord>()

        // Skip the CSV header by dropping the first line
        lines.drop(1).forEach { line ->
            val tokens = csvRegex.findAll(line).map { it.value.trim('"') }.toList()
            if (tokens.size >= 3) {
                val cost = tokens.last().toIntOrNull() ?: 0
                words.add(
                    DictionaryWord(
                        word = tokens.first(),
                        definition = tokens.drop(1).dropLast(1).joinToString(","),
                        isUnlocked = cost == 0,
                        cost = cost
                    )
                )
            }
        }

        return words
    }

    override fun dictionaryWordsInDreamFilterList(
        dreamContent: String,
        dictionaryWordList: List<DictionaryWord>
    ): List<DictionaryWord> {
        // Reuse your existing logic from your sample:
        // 1) Multi-word matches, 2) single word if length >= 5, etc.

        val output = mutableListOf<DictionaryWord>()
        val contentLower = dreamContent.lowercase()

        // 1) Multi-word or single words >= 5 chars
        dictionaryWordList.forEach { dictWord ->
            val dictLower = dictWord.word.lowercase()
            if (dictLower.contains(" ") && contentLower.contains(dictLower)) {
                output.add(dictWord)
            } else if (!dictLower.contains(" ") && dictLower.length >= 5 && contentLower.contains(dictLower)) {
                output.add(dictWord)
            }
        }

        // 2) Check for suffix transformations
        val dreamWords = contentLower
            .split("\\s+".toRegex())
            .map { it.trim('.', '?', '\"', '\'') }

        dreamWords.forEach { dreamWord ->
            if (dreamWord.isNotEmpty() && dreamWord.length > 2) {
                dictionaryWordList.forEach { dictWord ->
                    val dictLower = dictWord.word.lowercase()

                    val possibleMatches = generatePossibleMatches(dreamWord, suffixes)
                    if (possibleMatches.contains(dictLower) && !output.contains(dictWord)) {
                        output.add(dictWord)
                    } else {
                        val baseForm = removeSuffixes(dreamWord, suffixes)
                        if (baseForm == dictLower && !output.contains(dictWord)) {
                            output.add(dictWord)
                        }
                    }
                }
            }
        }

        // Return sorted & distinct
        return output.sortedBy { it.word }.distinct()
    }

    // Helper to generate possible suffix forms:
    private fun generatePossibleMatches(baseWord: String, suffixes: List<String>): Set<String> {
        val matches = mutableSetOf<String>()
        if (baseWord.isNotEmpty()) {
            matches.add(baseWord) // Add the base word
            if (baseWord.length <= 3) {
                suffixes.forEach { suffix ->
                    matches.add(baseWord + baseWord.last() + suffix)
                }
            } else {
                suffixes.forEach { suffix ->
                    matches.add(baseWord + suffix)
                    matches.add(baseWord + baseWord.last() + suffix)
                    if (baseWord.last() != suffix.first()) {
                        matches.add(baseWord.dropLast(1) + suffix)
                    }
                }
            }
        }
        return matches
    }

    // Helper to remove known suffixes:
    private fun removeSuffixes(word: String, suffixes: List<String>): String {
        var baseForm = word
        suffixes.forEach { suffix ->
            if (baseForm.endsWith(suffix)) {
                baseForm = baseForm.removeSuffix(suffix)
                // do not break if you want to handle multiple suffixes,
                // but typically you'd break if you only remove one suffix
            }
        }
        return baseForm
    }
}