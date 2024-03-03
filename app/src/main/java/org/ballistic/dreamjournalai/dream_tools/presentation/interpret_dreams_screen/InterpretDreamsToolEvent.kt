package org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen

import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream

sealed class InterpretDreamsToolEvent {
    data object GetDreams : InterpretDreamsToolEvent()
    data class AddDreamToInterpretationList(val dream: Dream) : InterpretDreamsToolEvent()
    data class RemoveDreamFromInterpretationList(val dream: Dream) : InterpretDreamsToolEvent()
}