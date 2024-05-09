package org.ballistic.dreamjournalai.dream_favorites.presentation

import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream

sealed class FavoriteEvent {
    data object LoadDreams : FavoriteEvent()
    data class DeleteDream(val dream: Dream) : FavoriteEvent()

    data class DreamToDelete(val dream: Dream) : FavoriteEvent()

    data class ToggleBottomDeleteCancelSheetState(val bottomDeleteCancelSheetState: Boolean) : FavoriteEvent()
    data object RestoreDream : FavoriteEvent()
}