package org.ballistic.dreamjournalai.feature_dream.domain.use_case

import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.feature_dream.domain.repository.DreamRepository

class GetCurrentDreamID(
    private val repository: DreamRepository
) {
    suspend operator fun invoke(): Resource<String> {
        return repository.getCurrentDreamId()
    }
}