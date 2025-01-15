package org.ballistic.dreamjournalai.shared.dream_favorites.domain

import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream

sealed class FavoriteEvent {
    data object LoadDreams : FavoriteEvent()
    data class DeleteDream(val dream: Dream) : FavoriteEvent()
    data class DreamToDelete(val dream: Dream) : FavoriteEvent()
    data class ToggleBottomDeleteCancelSheetState(val bottomDeleteCancelSheetState: Boolean) : FavoriteEvent()
    data object RestoreDream : FavoriteEvent()
}