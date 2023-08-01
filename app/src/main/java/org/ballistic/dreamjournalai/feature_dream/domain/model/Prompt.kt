package org.ballistic.dreamjournalai.feature_dream.domain.model


data class Prompt(
    val model: String,
    val prompt: String,
    val max_tokens: Int,
    val temperature: Int,
    val frequency_penalty: Int
)


