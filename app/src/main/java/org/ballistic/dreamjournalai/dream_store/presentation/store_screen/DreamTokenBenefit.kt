package org.ballistic.dreamjournalai.dream_store.presentation.store_screen

import org.ballistic.dreamjournalai.R

enum class DreamTokenBenefit(val title: String, val description: String, val image: Int) {
    DreamPainting(
        title = "Visualize your dreams",
        description = "Transform your dreams into art with AI. Instantly turn your visions into digital paintings. " +
                "Share your dreams with the world in a new, creative way with Dream Tokens.",
        image = R.drawable.dream_benefit_painting
    ),
    DreamInterpretation(
        title = "Interpret dreams and more",
        description = "Get Dream Tokens for immediate, AI-powered dream interpretations and mood " +
                "insights. Your questions answered, your emotions explored, and your dreams " +
                "transformed into stories. ",
        image = R.drawable.dream_benefit_interpretation
    ),
    DreamDictionary(
        title = "Unlock more dictionary words",
        description = "Unlock the dream dictionary words with Dream Tokens. Discover the meanings behind " +
                "the symbols, objects, and themes that appear in your dreams. ",
        image = R.drawable.dream_benefit_words
    ),
    DreamAdFree(
        title = "Ad-Free Experience",
        description = "Enjoy an ad-free experience with Dream Tokens. Use tokens instead of watching ads. " +
                "Focus on your dreams without interruptions. ",
        image = R.drawable.dream_benefit_no_ads
    ),
}

