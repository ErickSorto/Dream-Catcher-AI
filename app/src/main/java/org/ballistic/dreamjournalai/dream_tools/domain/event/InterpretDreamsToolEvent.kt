package org.ballistic.dreamjournalai.dream_tools.domain.event

import android.app.Activity
import org.ballistic.dreamjournalai.dream_tools.domain.model.MassInterpretation
import org.ballistic.dreamjournalai.dream_journal_list.domain.model.Dream

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
        val activity: Activity,
        val cost: Int,
        val isFinishedEvent: (Boolean) -> Unit
    ) : InterpretDreamsToolEvent()
}