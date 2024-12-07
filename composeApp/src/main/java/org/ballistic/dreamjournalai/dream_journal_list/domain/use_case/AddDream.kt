package org.ballistic.dreamjournalai.dream_journal_list.domain.use_case

import org.ballistic.dreamjournalai.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.dream_journal_list.domain.model.InvalidDreamException
import org.ballistic.dreamjournalai.dream_journal_list.domain.repository.DreamRepository

class AddDream(private val repository: DreamRepository) {
    @Throws(InvalidDreamException::class)
    suspend operator fun invoke(dream: Dream) {
        if (dream.content.isBlank()) {
            throw InvalidDreamException("Please enter a dream")
        }
        repository.insertDream(dream)
    }
}