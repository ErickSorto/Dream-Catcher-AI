package org.ballistic.dreamjournalai.shared.dream_store.domain

import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.dream_benefit_interpretation
import dreamjournalai.composeapp.shared.generated.resources.dream_benefit_no_ads
import dreamjournalai.composeapp.shared.generated.resources.dream_benefit_painting
import dreamjournalai.composeapp.shared.generated.resources.dream_benefit_words
import dreamjournalai.composeapp.shared.generated.resources.dream_token_benefit
import org.jetbrains.compose.resources.DrawableResource

enum class DreamTokenBenefit(
    val title: String,
    val description: String = "",
    val benefit1: String = "",
    val benefit2: String = "",
    val benefit3: String = "",
    val benefit4: String = "",
    val image: DrawableResource
) {
    DreamTokenSlideBenefit(
        title = "500 Dream Tokens can unlock:",
        benefit1 = "500 Dream interpretations",
        benefit2 = "250 Dream paintings",
        benefit3 = "250 Dream symbols",
        benefit4 = "Enough tokens for 6 months",
        image = Res.drawable.dream_token_benefit
    ),
    DreamPainting(
        title = "Visualize your dreams",
        description = "Transform your dreams into art with AI. Instantly turn your visions into digital paintings. " +
                "Share your dreams with the world in a new, creative way with Dream Tokens.",
        image = Res.drawable.dream_benefit_painting
    ),
    DreamInterpretation(
        title = "Interpret dreams and more",
        description = "Get Dream Tokens for immediate, AI-powered dream interpretations and mood " +
                "insights. Your questions answered, your emotions explored, and your dreams " +
                "transformed into stories. ",
        image = Res.drawable.dream_benefit_interpretation
    ),
    DreamDictionary(
        title = "Unlock more dream symbols",
        description = "Unlock the dream symbols with Dream Tokens. Discover the meanings behind " +
                "the symbols, objects, and themes that appear in your dreams. ",
        image = Res.drawable.dream_benefit_words
    ),
    DreamAdFree(
        title = "Ad-Free Experience",
        description = "Enjoy an ad-free experience with Dream Tokens. Use tokens instead of watching ads. " +
                "Focus on your dreams without interruptions. ",
        image = Res.drawable.dream_benefit_no_ads
    ),
}

