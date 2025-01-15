package org.ballistic.dreamjournalai.shared.dream_add_edit.domain

import androidx.compose.ui.graphics.Color
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.baseline_auto_fix_high_24
import dreamjournalai.composeapp.shared.generated.resources.baseline_brush_24
import dreamjournalai.composeapp.shared.generated.resources.baseline_lightbulb_24
import dreamjournalai.composeapp.shared.generated.resources.baseline_mood_24
import dreamjournalai.composeapp.shared.generated.resources.baseline_question_answer_24
import dreamjournalai.composeapp.shared.generated.resources.interpret_vector
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Green
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LighterYellow
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Purple
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.SkyBlue
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Yellow
import org.jetbrains.compose.resources.DrawableResource

enum class AITool(val title: String, val icon: DrawableResource, val color: Color) {
    PAINT_DREAM("Paint", Res.drawable.baseline_brush_24, SkyBlue),
    INTERPRET_DREAM("Interpret", Res.drawable.interpret_vector, Purple),
    DREAM_ADVICE("Advice", Res.drawable.baseline_lightbulb_24, Yellow),
    DREAM_QUESTION("Question", Res.drawable.baseline_question_answer_24, RedOrange),
    DREAM_STORY("Story", Res.drawable.baseline_auto_fix_high_24, LighterYellow),
    DREAM_MOOD("Mood", Res.drawable.baseline_mood_24, Green),
}