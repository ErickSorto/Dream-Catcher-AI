package org.ballistic.dreamjournalai.feature_dream.domain.model

import com.google.gson.annotations.SerializedName
import org.ballistic.dreamjournalai.feature_dream.data.remote.dto.gptchat.Message

data class PromptChat(
    val model: String,
    val messages: List<Message>,
    val temperature: Double,
    //top_p
    @SerializedName("top_p")
    val topP: Double,
    @SerializedName("max_tokens")
    val maxTokens: Int = 300,
    @SerializedName("presence_penalty")
    val presencePenalty: Int,
    @SerializedName("frequency_penalty")
    val frequencyPenalty: Int,
    val user: String? = null
)