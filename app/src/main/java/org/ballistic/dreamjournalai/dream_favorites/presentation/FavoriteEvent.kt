package org.ballistic.dreamjournalai.dream_favorites.presentation

import android.content.Context
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream

sealed class FavoriteEvent {
    data object LoadDreams : FavoriteEvent()
    data class DeleteDream(val dream: Dream, val context: Context) : FavoriteEvent()
    data object RestoreDream : FavoriteEvent()
}