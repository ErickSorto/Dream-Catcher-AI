package org.ballistic.dreamjournalai.dream_tools.domain

import org.ballistic.dreamjournalai.R

enum class MassInterpretationTabs(
    val title: String,
    val icon: Int
) {
    DREAMS("Dreams", R.drawable.dream_word_vector_icon),
    INTERPRETATION_RESULTS("Result", R.drawable.interpret_vector),
    INTERPRETATION_LIST("History", R.drawable.baseline_history_24),
}