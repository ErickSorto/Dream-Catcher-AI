package org.ballistic.dreamjournalai.dream_journal_list.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.parcelize.Parcelize
import org.ballistic.dreamjournalai.R

@Keep
@Parcelize
data class Dream (
    val title: String,
    val content: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val date: String = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString(),
    val sleepTime: String = String.format("%02d:%02d", 23, 0),
    val wakeTime: String = String.format("%02d:%02d", 7, 0),
    @PropertyName("airesponse")
    val AIResponse: String,
    val isFavorite: Boolean,
    val isLucid: Boolean,
    val isNightmare: Boolean,
    val isRecurring: Boolean,
    val falseAwakening: Boolean,
    val lucidityRating: Int,
    val moodRating: Int,
    val vividnessRating: Int,
    val timeOfDay: String,
    val backgroundImage: Int,
    val generatedImage: String,
    val generatedDetails: String,
    val dreamAIAdvice: String,
    val dreamQuestion: String,
    val dreamAIQuestionAnswer: String,
    val dreamAIStory: String,
    val dreamAIMood: String,
    val id: String?,
    val uid: String?
) : Parcelable {

    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombination = listOf(
            title,
            content,
            "$title $content",
            "$content $title"
        )
        return matchingCombination.any { it.contains(query, true) }
    }

    companion object {
        val dreamBackgroundImages = listOf(
            R.drawable.purple_skies_lighthouse,
            R.drawable.red_lighthouse_background,
            R.drawable.green_lighthouse_background,
            R.drawable.blue_lighthouse,
            R.drawable.full_life_lighthouse,
            R.drawable.heaven_lighthouse,
            R.drawable.dark_night_lighthouse,
            R.drawable.yellow_lighthouse_background,
            R.drawable.beautiful_lighthouse,
            R.drawable.background_during_day,
            R.drawable.sunrise_lighthouse
        )
    }

    constructor() : this(
        "",
        "",
        0,
        "",
        "",
        "",
        "",
        false,
        false,
        false,
        false,
        false,
        0,
        0,
        0,
        "",
        0,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        null,
        null
    )
}

class InvalidDreamException(message: String) : Exception(message)