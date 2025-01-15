package org.ballistic.dreamjournalai.shared.core.domain


import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class Flag(
    val dreamId: String? = null,            // Nullable dream ID
    val imageAddress: String,               // URL of the associated image
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val status: String = "pending"          // Flag status: pending, reviewed, resolved
) {
    // For dev.gitlive firebase, you might keep a no-arg constructor if desired
    constructor() : this(null, "", Clock.System.now().toEpochMilliseconds(), "pending")
}