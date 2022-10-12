package org.ballistic.dreamjournalai.feature_dream.presentation.dreams

import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.util.DreamOrder
import org.ballistic.dreamjournalai.feature_dream.domain.util.OrderType

data class DreamsState (
    val dreams: List<Dream> = emptyList(),
    val dreamOrder: DreamOrder = DreamOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false,
)