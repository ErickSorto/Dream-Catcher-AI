package org.ballistic.dreamjournalai.shared.dream_journal_list.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ballistic.dreamjournalai.shared.core.Resource
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream

interface DreamRepository {
    fun getDreams(): Flow<List<Dream>>
    suspend fun getDream(id: String): Resource<Dream>
    suspend fun getCurrentDreamId(): Resource<String>
    suspend fun insertDream(dream: Dream): Resource<Unit>
    suspend fun deleteDream(id: String): Resource<Unit>
    suspend fun flagDream(
        dreamId: String?,         // Nullable to handle cases where dream might not be saved
        imageAddress: String?    // Nullable to handle cases where image might not be available
    ): Resource<Unit>
}