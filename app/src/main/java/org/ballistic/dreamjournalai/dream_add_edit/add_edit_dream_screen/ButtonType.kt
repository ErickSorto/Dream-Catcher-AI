package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen

import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditDreamEvent

enum class ButtonType(
    val title: String,
    val description: String,
    val drawableId: Int,
    val pageIndex: Int,
    val baseColorId: Int,
    val longTextColorId: Int,
    val eventCreator: (Boolean) -> AddEditDreamEvent
) {
    PAINT("Paint Dream",
        "Generate Painting",
        R.drawable.baseline_brush_24,
        0, R.color.white,
        R.color.sky_blue,
        {
            AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(
                it
            )
        }),
    INTERPRET("Interpret Dream",
        "Generate Interpretation",
        R.drawable.interpret_vector,
        1,
        R.color.white,
        R.color.purple,
        {
            AddEditDreamEvent.ToggleDreamInterpretationPopUpState(
                it
            )
        }),
    ADVICE("get Advice",
        "Generate Advice",
        R.drawable.baseline_lightbulb_24,
        2,
        R.color.white,
        R.color.Yellow,
        {
            AddEditDreamEvent.ToggleDreamAdvicePopUpState(
                it
            )
        }),
    QUESTION("ask a Question",
        "Ask a Question",
        R.drawable.baseline_question_answer_24,
        3,
        R.color.white,
        R.color.RedOrange,
        {
            AddEditDreamEvent.ToggleDreamQuestionPopUpState(
                it
            )
        }),
    STORY("Create Story",
        "Generate Story",
        R.drawable.baseline_auto_fix_high_24,
        4,
        R.color.white,
        R.color.lighter_yellow,
        {
            AddEditDreamEvent.ToggleDreamStoryPopUpState(
                it
            )
        }),
    MOOD("Analyze Mood",
        "Generate Mood Analysis",
        R.drawable.baseline_mood_24,
        5,
        R.color.white,
        R.color.green,
        {
            AddEditDreamEvent.ToggleDreamMoodPopUpState(
                it
            )
        });
}
