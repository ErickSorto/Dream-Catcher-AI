package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen

import androidx.annotation.Keep
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditDreamEvent

@Keep
enum class ButtonType(
    val title: String,  // Use the string key for the title
    val description: String,  // Use the string key for the description
    val drawableId: Int,
    val pageIndex: Int,
    val baseColorId: Int,
    val longTextColorId: Int,
    val eventCreator: (Boolean) -> AddEditDreamEvent
) {
    PAINT(
        "paint_dream",  // Key for localization lookup
        "generate_painting",  // Key for localization lookup
        R.drawable.baseline_brush_24,
        0, R.color.white,
        R.color.sky_blue,
        {
            AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(it)
        }
    ),
    INTERPRET(
        "interpret_dream",
        "generate_interpretation",
        R.drawable.interpret_vector,
        1,
        R.color.white,
        R.color.purple,
        {
            AddEditDreamEvent.ToggleDreamInterpretationPopUpState(it)
        }
    ),
    ADVICE(
        "get_advice",
        "generate_advice",
        R.drawable.baseline_lightbulb_24,
        2,
        R.color.white,
        R.color.Yellow,
        {
            AddEditDreamEvent.ToggleDreamAdvicePopUpState(it)
        }
    ),
    QUESTION(
        "ask_a_question_title",
        "ask_a_question_description",
        R.drawable.baseline_question_answer_24,
        3,
        R.color.white,
        R.color.RedOrange,
        {
            AddEditDreamEvent.ToggleDreamQuestionPopUpState(it)
        }
    ),
    STORY(
        "create_story_title",
        "create_story_description",
        R.drawable.baseline_auto_fix_high_24,
        4,
        R.color.white,
        R.color.lighter_yellow,
        {
            AddEditDreamEvent.ToggleDreamStoryPopUpState(it)
        }
    ),
    MOOD(
        "analyze_mood_title",
        "analyze_mood_description",
        R.drawable.baseline_mood_24,
        5,
        R.color.white,
        R.color.green,
        {
            AddEditDreamEvent.ToggleDreamMoodPopUpState(it)
        }
    );
}
