package org.ballistic.dreamjournalai.feature_dream.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream

interface DreamRepository {
    fun getDreams(): Flow<List<Dream>>
    suspend fun getDream(id: String): Resource<Dream>
    suspend fun getCurrentDreamId(): Resource<String>
    suspend fun insertDream(dream: Dream): Resource<Unit>
    suspend fun deleteDream(id: String): Resource<Unit>
}