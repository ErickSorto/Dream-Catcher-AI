package org.ballistic.dreamjournalai.dream_nightmares

import android.content.Context
import org.ballistic.dreamjournalai.dream_favorites.presentation.FavoriteEvent
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream

sealed class NightmareEvent {
    data object LoadDreams : NightmareEvent()
    data class DeleteDream(val dream: Dream) : NightmareEvent()
    data class DreamToDelete(val dream: Dream) : NightmareEvent()
    data class ToggleBottomDeleteCancelSheetState(val bottomDeleteCancelSheetState: Boolean) : NightmareEvent()
    data object RestoreDream : NightmareEvent()
}