package org.ballistic.dreamjournalai.dream_fullscreen

sealed class FullScreenEvent {
    data class Flag(val imagePath: String, val onSuccessEvent : () -> Unit, ) : FullScreenEvent()
}

