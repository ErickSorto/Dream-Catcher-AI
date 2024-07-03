package org.ballistic.dreamjournalai.feature_dream.domain.use_case

import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.model.InvalidDreamException
import org.ballistic.dreamjournalai.feature_dream.domain.repository.DreamRepository

class AddDream(private val repository: DreamRepository) {
    @Throws(InvalidDreamException::class)
    suspend operator fun invoke(dream: Dream) {
        if (dream.content.isBlank()) {
            throw InvalidDreamException("Please enter a dream")
        }
        repository.insertDream(dream)
    }
}