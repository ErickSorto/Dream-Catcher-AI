package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen

import androidx.annotation.Keep
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.viewmodel.AIData
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.viewmodel.AddEditDreamState

@Keep
enum class AIPageType(
    val title: String,
    val getState: (AddEditDreamState) -> AIData,  // Fetches the right AI state.
    val buttonType: ButtonType,  // Corresponding button type for actions.
) {
    PAINTER("Dream Painter", { it.dreamAIImage },
        ButtonType.PAINT
    ),
    EXPLANATION("Dream Interpretation", { it.dreamAIExplanation },
        ButtonType.INTERPRET
    ),
    ADVICE("Dream Advice", { it.dreamAIAdvice },
        ButtonType.ADVICE
    ),
    QUESTION("Dream Questions", { it.dreamAIQuestionAnswer },
        ButtonType.QUESTION
    ),
    STORY("Dream Story", { it.dreamAIStory },
        ButtonType.STORY
    ),
    MOOD("Dream Mood Analysis", { it.dreamAIMoodAnalyser },
        ButtonType.MOOD
    ),
}


