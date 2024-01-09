package org.ballistic.dreamjournalai.dream_nightmares

import android.content.Context
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream

sealed class NightmareEvent {
    data object LoadDreams : NightmareEvent()
    data class DeleteDream(val dream: Dream, val context: Context) : NightmareEvent()
    data object RestoreDream : NightmareEvent()
}