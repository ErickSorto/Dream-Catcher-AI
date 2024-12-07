package org.ballistic.dreamjournalai.dream_journal_list.domain.util

sealed class OrderType{
    object Ascending: OrderType()
    object Descending: OrderType()
    object Date: OrderType()
}
