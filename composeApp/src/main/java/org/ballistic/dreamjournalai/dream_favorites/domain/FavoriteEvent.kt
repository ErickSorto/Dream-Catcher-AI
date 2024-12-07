package org.ballistic.dreamjournalai.dream_favorites.domain

import org.ballistic.dreamjournalai.dream_journal_list.domain.model.Dream

sealed class FavoriteEvent {
    data object LoadDreams : FavoriteEvent()
    data class DeleteDream(val dream: Dream) : FavoriteEvent()
    data class DreamToDelete(val dream: Dream) : FavoriteEvent()
    data class ToggleBottomDeleteCancelSheetState(val bottomDeleteCancelSheetState: Boolean) : FavoriteEvent()
    data object RestoreDream : FavoriteEvent()
}