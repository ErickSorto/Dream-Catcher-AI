package org.ballistic.dreamjournalai.dream_add_edit.domain


import org.ballistic.dreamjournalai.R


enum class AddEditPages(val title: String, val icon: Int) {
    DREAM("Dream", R.drawable.baseline_nightlight_24),
    AI("AI", R.drawable.ai_vector_icon),
    WORDS("Symbols", R.drawable.baseline_book_24),
    INFO("Info", R.drawable.dream_details)
}