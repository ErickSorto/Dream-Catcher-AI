package org.ballistic.dreamjournalai.feature_dream.domain.util

sealed class OrderType{
    object Ascending: OrderType()
    object Descending: OrderType()
    object Date: OrderType()
}
