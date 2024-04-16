package org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen

import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.util.OrderType

sealed class DreamListEvent {
    data class Order(val orderType: OrderType): DreamListEvent()
    data class DeleteDream(val dream: Dream): DreamListEvent()
    data object ToggleOrderSection: DreamListEvent()
    data object RestoreDream: DreamListEvent()
    data class SetSearchingState(val state: Boolean): DreamListEvent()
}
