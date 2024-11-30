package org.ballistic.dreamjournalai.dream_journal_list.domain.use_case

import org.ballistic.dreamjournalai.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.dream_journal_list.domain.repository.DreamRepository

class DeleteDream(private val repository: DreamRepository) {
    suspend operator fun invoke(dream: Dream) {
        dream.id?.let { repository.deleteDream(it) }
    }
}
