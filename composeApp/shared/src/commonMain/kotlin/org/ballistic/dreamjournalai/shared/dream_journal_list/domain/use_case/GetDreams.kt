package org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.repository.DreamRepository
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.util.OrderType

class GetDreams(
    private val repository: DreamRepository
) {

    operator fun invoke(
        orderType: OrderType = OrderType.Date,
    ): Flow<List<Dream>> {
        return repository.getDreams().map { dreams ->
            when (orderType) {
                is OrderType.Ascending -> {
                    dreams.sortedBy { dream -> dream.timestamp }
                }

                is OrderType.Descending -> {
                    dreams.sortedByDescending { dream -> dream.timestamp }
                }

                is OrderType.Date -> {
                    dreams.sortedByDescending { dream -> dream.date }
                }
            }
        }
    }
}
