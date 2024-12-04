package org.ballistic.dreamjournalai.dream_journal_list.domain.use_case

import android.util.Log
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.dream_journal_list.domain.repository.DreamRepository
import java.util.UUID

class FlagDream(
    private val repository: DreamRepository
) {
    suspend operator fun invoke(dreamID: String?, imagePath: String): Resource<Unit> {
        var newDreamID = dreamID
        if (newDreamID.isNullOrBlank()) {
            newDreamID = UUID.randomUUID().toString()
            Log.d("FlagDream", "Generated new ID: $newDreamID")
        }
        return repository.flagDream(newDreamID, imagePath)
    }
}
