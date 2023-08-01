package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.state

data class DreamTextFieldState (
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true)