package org.ballistic.dreamjournalai.shared.dream_tools.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MassInterpretation(
    val interpretation: String = "",
    val listOfDreamIDs: List<String?> = emptyList(),
    val date: Long = 0L,
    val model: String = "",
    val id: String? = null
) {
    // For Firestore or other libraries that might require no-arg
    constructor() : this("", emptyList(), 0L, "", null)
}