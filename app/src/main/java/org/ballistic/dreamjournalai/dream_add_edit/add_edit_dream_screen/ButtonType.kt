package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen

import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditDreamEvent

enum class ButtonType(
    val title: Int,
    val description: Int,
    val drawableId: Int,
    val pageIndex: Int,
    val baseColorId: Int,
    val longTextColorId: Int,
    val eventCreator: (Boolean) -> AddEditDreamEvent
) {
    PAINT(
        R.string.paint_dream,
        R.string.generate_painting,
        R.drawable.baseline_brush_24,
        0, R.color.white,
        R.color.sky_blue,
        {
            AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(
                it
            )
        }),
    INTERPRET(
        R.string.interpret_dream,
        R.string.generate_interpretation,
        R.drawable.interpret_vector,
        1,
        R.color.white,
        R.color.purple,
        {
            AddEditDreamEvent.ToggleDreamInterpretationPopUpState(
                it
            )
        }),
    ADVICE(
        R.string.get_advice,
        R.string.generate_advice,
        R.drawable.baseline_lightbulb_24,
        2,
        R.color.white,
        R.color.Yellow,
        {
            AddEditDreamEvent.ToggleDreamAdvicePopUpState(
                it
            )
        }),
    QUESTION(
        R.string.ask_a_question_title,
        R.string.ask_a_question_description,
        R.drawable.baseline_question_answer_24,
        3,
        R.color.white,
        R.color.RedOrange,
        {
            AddEditDreamEvent.ToggleDreamQuestionPopUpState(
                it
            )
        }),
    STORY(
        R.string.create_story_title,
        R.string.create_story_description,
        R.drawable.baseline_auto_fix_high_24,
        4,
        R.color.white,
        R.color.lighter_yellow,
        {
            AddEditDreamEvent.ToggleDreamStoryPopUpState(
                it
            )
        }),
    MOOD(
        R.string.analyze_mood_title,
        R.string.analyze_mood_description,
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
