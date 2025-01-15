package org.ballistic.dreamjournalai.shared.dream_tools.domain.event

sealed class RandomToolEvent {
    data object GetDreams : RandomToolEvent()
    data object GetRandomDream : RandomToolEvent()
    data object TriggerVibration : RandomToolEvent()
}