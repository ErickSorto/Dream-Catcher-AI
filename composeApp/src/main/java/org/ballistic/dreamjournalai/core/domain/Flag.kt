package org.ballistic.dreamjournalai.core.domain


import android.os.Parcelable
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.parcelize.Parcelize
import org.ballistic.dreamjournalai.dream_journal_list.domain.model.Dream

@Parcelize
data class Flag(
    val dreamId: String? = null,            // Nullable dream ID
    val imageAddress: String,               // URL of the associated image
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending"          // Flag status: pending, reviewed, resolved
) : Parcelable   {
    constructor() : this(
        null,
        "",
        System.currentTimeMillis(),
        "pending"
    )
}