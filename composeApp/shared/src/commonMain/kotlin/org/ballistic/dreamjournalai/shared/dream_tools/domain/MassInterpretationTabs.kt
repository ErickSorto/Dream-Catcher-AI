package org.ballistic.dreamjournalai.shared.dream_tools.domain

import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.baseline_history_24
import dreamjournalai.composeapp.shared.generated.resources.dream_word_vector_icon
import dreamjournalai.composeapp.shared.generated.resources.interpret_vector
import org.jetbrains.compose.resources.DrawableResource

enum class MassInterpretationTabs(
    val title: String,
    val icon: DrawableResource
) {
    DREAMS("Dreams", Res.drawable.dream_word_vector_icon),
    INTERPRETATION_RESULTS("Result", Res.drawable.interpret_vector),
    INTERPRETATION_LIST("History", Res.drawable.baseline_history_24),
}