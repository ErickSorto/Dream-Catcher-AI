package org.ballistic.dreamjournalai.shared.dream_journal_list.domain.util

sealed class OrderType{
    data object Ascending: OrderType()
    data object Descending: OrderType()
    data object Date: OrderType()
}
