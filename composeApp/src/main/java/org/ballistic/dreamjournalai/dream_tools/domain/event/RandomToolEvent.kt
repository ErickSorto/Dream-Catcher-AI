package org.ballistic.dreamjournalai.dream_tools.domain.event

sealed class RandomToolEvent {
    data object GetDreams : RandomToolEvent()
    data object GetRandomDream : RandomToolEvent()
}