package org.ballistic.dreamjournalai.dream_journal_list.domain.use_case

import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.dream_journal_list.domain.repository.DreamRepository

class GetDream (
    private val repository: DreamRepository
){
    suspend operator fun invoke(id: String): Resource<Dream> {
        return repository.getDream(id)
    }
}