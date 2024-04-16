package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.events

import org.ballistic.dreamjournalai.R

enum class AITool(val title: String, val shortDescription: String, val icon: Int, val color: Int) {
    PAINT_DREAM("Paint", "Paint Dream", R.drawable.baseline_brush_24, R.color.sky_blue),
    INTERPRET_DREAM("Interpret", "Interpret Dream", R.drawable.interpret_vector, R.color.purple),
    DREAM_ADVICE("Advice", "Dream Advice", R.drawable.baseline_lightbulb_24, R.color.Yellow),
    DREAM_QUESTION("Question", "Dream Question", R.drawable.baseline_question_answer_24, R.color.RedOrange),
    DREAM_STORY("Story", "Dream Story", R.drawable.baseline_auto_fix_high_24, R.color.lighter_yellow),
    DREAM_MOOD("Mood", "Dream Mood", R.drawable.baseline_mood_24, R.color.green),
}