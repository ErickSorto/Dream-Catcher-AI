package org.ballistic.dreamjournalai.shared.dream_add_edit.domain


import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.ai_vector_icon
import dreamjournalai.composeapp.shared.generated.resources.baseline_book_24
import dreamjournalai.composeapp.shared.generated.resources.baseline_nightlight_24
import dreamjournalai.composeapp.shared.generated.resources.dream_details
import org.jetbrains.compose.resources.DrawableResource


enum class AddEditPages(val title: String, val icon: DrawableResource) {
    DREAM("Dream", Res.drawable.baseline_nightlight_24),
    AI("AI", Res.drawable.ai_vector_icon),
    WORDS("Symbols", Res.drawable.baseline_book_24),
    INFO("Info", Res.drawable.dream_details)
}