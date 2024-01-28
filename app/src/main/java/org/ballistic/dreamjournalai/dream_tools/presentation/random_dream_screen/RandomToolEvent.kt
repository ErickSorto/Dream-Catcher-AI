package org.ballistic.dreamjournalai.dream_tools.presentation.random_dream_screen

sealed class RandomToolEvent {
    data object GetDreams : RandomToolEvent()
    data object GetRandomDream : RandomToolEvent()
}