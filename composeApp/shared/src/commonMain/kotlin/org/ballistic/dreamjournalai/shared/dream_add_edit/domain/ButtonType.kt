package org.ballistic.dreamjournalai.shared.dream_add_edit.domain

import androidx.compose.ui.graphics.Color
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.ask_a_question_description
import dreamjournalai.composeapp.shared.generated.resources.ask_a_question_title
import dreamjournalai.composeapp.shared.generated.resources.baseline_auto_fix_high_24
import dreamjournalai.composeapp.shared.generated.resources.baseline_brush_24
import dreamjournalai.composeapp.shared.generated.resources.baseline_lightbulb_24
import dreamjournalai.composeapp.shared.generated.resources.baseline_mood_24
import dreamjournalai.composeapp.shared.generated.resources.baseline_question_answer_24
import dreamjournalai.composeapp.shared.generated.resources.create_story_description
import dreamjournalai.composeapp.shared.generated.resources.create_story_title
import dreamjournalai.composeapp.shared.generated.resources.generate_advice
import dreamjournalai.composeapp.shared.generated.resources.generate_interpretation
import dreamjournalai.composeapp.shared.generated.resources.generate_painting
import dreamjournalai.composeapp.shared.generated.resources.get_advice
import dreamjournalai.composeapp.shared.generated.resources.interpret_dream
import dreamjournalai.composeapp.shared.generated.resources.interpret_vector
import dreamjournalai.composeapp.shared.generated.resources.paint_dream
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Green
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LighterYellow
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Purple
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.SkyBlue
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Yellow
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class ButtonType(
    val title: StringResource,  // Use the string key for the title
    val description: StringResource,  // Use the string key for the description
    val drawableId: DrawableResource,
    val pageIndex: Int,
    val baseColorId: Color,
    val longTextColorId: Color,
    val eventCreator: (Boolean) -> AddEditDreamEvent
) {
    PAINT(
        Res.string.paint_dream,  // Key for localization lookup
        Res.string.generate_painting,  // Key for localization lookup
        Res.drawable.baseline_brush_24,
        0,
        White,
        SkyBlue,
        {
            AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(it)
        }
    ),
    INTERPRET(
        Res.string.interpret_dream,
        Res.string.generate_interpretation,
        Res.drawable.interpret_vector,
        1,
        White,
        Purple,
        {
            AddEditDreamEvent.ToggleDreamInterpretationPopUpState(it)
        }
    ),
    ADVICE(
        Res.string.get_advice,
        Res.string.generate_advice,
        Res.drawable.baseline_lightbulb_24,
        2,
        White,
        Yellow,
        {
            AddEditDreamEvent.ToggleDreamAdvicePopUpState(it)
        }
    ),
    QUESTION(
        Res.string.ask_a_question_title,
        Res.string.ask_a_question_description,
        Res.drawable.baseline_question_answer_24,
        3,
        White,
        RedOrange,
        {
            AddEditDreamEvent.ToggleDreamQuestionPopUpState(it)
        }
    ),
    STORY(
        Res.string.create_story_title,
        Res.string.create_story_description,
        Res.drawable.baseline_auto_fix_high_24,
        4,
        White,
        LighterYellow,
        {
            AddEditDreamEvent.ToggleDreamStoryPopUpState(it)
        }
    ),
    MOOD(
        Res.string.create_story_title,
        Res.string.create_story_description,
        Res.drawable.baseline_mood_24,
        5,
        White,
        Green,
        {
            AddEditDreamEvent.ToggleDreamMoodPopUpState(it)
        }
    );
}
