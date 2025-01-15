package org.ballistic.dreamjournalai.shared.dream_tools.domain.event

import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_tools.domain.model.MassInterpretation


sealed class InterpretDreamsToolEvent {
    data object GetDreams : InterpretDreamsToolEvent()

    data object GetMassInterpretations : InterpretDreamsToolEvent()

    data class ToggleDreamToInterpretationList(val dream: Dream) : InterpretDreamsToolEvent()
    data class DeleteMassInterpretation(val massInterpretation: MassInterpretation) : InterpretDreamsToolEvent()

    data class ToggleBottomMassInterpretationSheetState(val state: Boolean) : InterpretDreamsToolEvent()

    data class ToggleBottomDeleteCancelSheetState(val state: Boolean) : InterpretDreamsToolEvent()
    data class UpdateModel(val model: String) : InterpretDreamsToolEvent()

    data class ChooseMassInterpretation(val massInterpretation: MassInterpretation) : InterpretDreamsToolEvent()
    data class InterpretDreams(
        val isAd: Boolean,
        val cost: Int,
        val isFinishedEvent: (Boolean) -> Unit
    ) : InterpretDreamsToolEvent()

    data object GetDreamTokens : InterpretDreamsToolEvent()

    data object TriggerVibration : InterpretDreamsToolEvent()
}