package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen

import android.app.Activity
import java.time.LocalDate
import java.time.LocalTime

sealed class AddEditDreamEvent {
    data class EnteredTitle(val value: String) : AddEditDreamEvent()
    data class EnteredContent(val value: String) : AddEditDreamEvent()
    data class ChangeDreamBackgroundImage(val dreamBackGroundImage: Int) : AddEditDreamEvent()
    data class ChangeLucidity(val lucidity: Int) : AddEditDreamEvent()
    data class ChangeVividness(val vividness: Int) : AddEditDreamEvent()
    data class ChangeRecurrence(val boolean: Boolean) : AddEditDreamEvent()
    data class ChangeNightmare(val boolean: Boolean) : AddEditDreamEvent()
    data class ChangeIsLucid(val boolean: Boolean) : AddEditDreamEvent()
    data class ChangeMood(val mood: Int) : AddEditDreamEvent()
    data class ChangeFalseAwakening(val boolean: Boolean) : AddEditDreamEvent()
    data class ChangeTimeOfDay(val timeOfDay: String) : AddEditDreamEvent()
    data class ChangeFavorite(val boolean: Boolean) : AddEditDreamEvent()
    data class ClickGenerateAIResponse(val value: String, val activity: Activity, val isAd: Boolean,
                                       val cost: Int) : AddEditDreamEvent()
    data class ClickGenerateAIImage(val value: String, val activity: Activity, val isAd: Boolean) : AddEditDreamEvent()
    data class ClickGenerateDetails(val value: String) : AddEditDreamEvent()
    data class ClickGenerateFromDescription(val value: Boolean) : AddEditDreamEvent()
    data class ChangeDetailsOfDream(val value: String) : AddEditDreamEvent()
    data class ChangeDreamWakeTime(val value: LocalTime) : AddEditDreamEvent()
    data class ChangeDreamSleepTime(val value: LocalTime) : AddEditDreamEvent()
    data class ChangeDreamDate(val value: LocalDate) : AddEditDreamEvent()

    data class ChangeQuestionOfDream(val value: String) : AddEditDreamEvent()
    data class SaveDream(val onSaveSuccess : () -> Unit) : AddEditDreamEvent()
    object DeleteDream : AddEditDreamEvent()
}