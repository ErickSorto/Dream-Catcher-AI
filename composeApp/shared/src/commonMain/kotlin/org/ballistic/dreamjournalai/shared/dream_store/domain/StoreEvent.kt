package org.ballistic.dreamjournalai.shared.dream_store.domain

sealed class StoreEvent {
    data object Buy100DreamTokens : StoreEvent()
    data object Buy500DreamTokens : StoreEvent()

    data class ToggleLoading(val isLoading: Boolean) : StoreEvent()
    data object GetDreamTokens : StoreEvent()
}