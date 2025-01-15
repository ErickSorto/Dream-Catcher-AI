package org.ballistic.dreamjournalai.shared.dream_journal_list.domain

import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.util.OrderType

sealed class DreamListEvent {
    data class Order(val orderType: OrderType): DreamListEvent()
    data class DeleteDream(val dream: Dream): DreamListEvent()
    data object ToggleOrderSection: DreamListEvent()
    data class ToggleBottomDeleteCancelSheetState(val bottomDeleteCancelSheetState: Boolean): DreamListEvent()
    data class DreamToDelete(val dream: Dream): DreamListEvent()
    data object RestoreDream: DreamListEvent()
    data class SetSearchingState(val state: Boolean): DreamListEvent()
    data object FetchDreams: DreamListEvent()
    data object TriggerVibration: DreamListEvent()
    data object TriggerReview: DreamListEvent()
}
