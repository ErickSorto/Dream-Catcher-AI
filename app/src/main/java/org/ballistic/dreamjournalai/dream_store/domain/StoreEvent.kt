package org.ballistic.dreamjournalai.dream_store.domain

import android.app.Activity

sealed class StoreEvent {
    data class Buy100DreamTokens(val activity: Activity) : StoreEvent()
    data class Buy500DreamTokens(val activity: Activity) : StoreEvent()

    data class ToggleLoading(val isLoading: Boolean) : StoreEvent()
}