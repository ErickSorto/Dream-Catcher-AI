package org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model


import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.heaven_lighthouse
import dreamjournalai.composeapp.shared.generated.resources.purple_skies_lighthouse
import dreamjournalai.composeapp.shared.generated.resources.red_lighthouse_background
import dreamjournalai.composeapp.shared.generated.resources.green_lighthouse_background
import dreamjournalai.composeapp.shared.generated.resources.blue_lighthouse
import dreamjournalai.composeapp.shared.generated.resources.full_life_lighthouse
import dreamjournalai.composeapp.shared.generated.resources.dark_night_lighthouse
import dreamjournalai.composeapp.shared.generated.resources.yellow_lighthouse_background
import dreamjournalai.composeapp.shared.generated.resources.beautiful_lighthouse
import dreamjournalai.composeapp.shared.generated.resources.background_during_day
import dreamjournalai.composeapp.shared.generated.resources.sunrise_lighthouse
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dream(
    val title: String = "",
    val content: String = "",
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val date: String = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .toString(),  // e.g. "2023-07-07"
    val sleepTime: String = "23:00",  // "23:00"
    val wakeTime: String = "07:00",   // "07:00"
    @SerialName("airesponse") // If you want JSON key "airesponse"
    val AIResponse: String = "",
    val isFavorite: Boolean = false,
    val isLucid: Boolean = false,
    val isNightmare: Boolean = false,
    val isRecurring: Boolean = false,
    val falseAwakening: Boolean = false,

    val lucidityRating: Int = 0,
    val moodRating: Int = 0,
    val vividnessRating: Int = 0,
    val timeOfDay: String = "",

    // For a KMM-friendly approach, we store just an Int or a String reference to an image.
    // If you need actual images, you might store a URL or a resource ID differently on each platform.
    val backgroundImage: Int = 0,
    val generatedImage: String = "",
    val generatedDetails: String = "",

    val dreamAIAdvice: String = "",
    val dreamQuestion: String = "",
    val dreamAIQuestionAnswer: String = "",
    val dreamAIStory: String = "",
    val dreamAIMood: String = "",

    val id: String? = null,
    val uid: String? = null
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombination = listOf(
            title,
            content,
            "$title $content",
            "$content $title"
        )
        return matchingCombination.any { it.contains(query, ignoreCase = true) }
    }

    companion object {
        val dreamBackgroundImages = listOf(
            Res.drawable.purple_skies_lighthouse,
            Res.drawable.red_lighthouse_background,
            Res.drawable.green_lighthouse_background,
            Res.drawable.blue_lighthouse,
            Res.drawable.full_life_lighthouse,
            Res.drawable.heaven_lighthouse,
            Res.drawable.dark_night_lighthouse,
            Res.drawable.yellow_lighthouse_background,
            Res.drawable.beautiful_lighthouse,
            Res.drawable.background_during_day,
            Res.drawable.sunrise_lighthouse
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