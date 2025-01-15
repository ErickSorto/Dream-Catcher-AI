package org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case

import org.ballistic.dreamjournalai.shared.core.Resource
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.repository.DreamRepository

class GetCurrentDreamID(
    private val repository: DreamRepository
) {
    suspend operator fun invoke(): Resource<String> {
        return repository.getCurrentDreamId()
    }
}