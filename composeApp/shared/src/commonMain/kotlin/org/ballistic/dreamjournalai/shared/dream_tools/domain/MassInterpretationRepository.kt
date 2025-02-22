package org.ballistic.dreamjournalai.shared.dream_tools.domain

import kotlinx.coroutines.flow.Flow
import org.ballistic.dreamjournalai.shared.core.Resource
import org.ballistic.dreamjournalai.shared.dream_tools.domain.model.MassInterpretation

interface MassInterpretationRepository {
    fun getInterpretations(): Flow<List<MassInterpretation>>
    suspend fun addInterpretation(massInterpretation: MassInterpretation) : Resource<Unit>
    suspend fun removeInterpretation(massInterpretation: MassInterpretation) : Resource<Unit>
}