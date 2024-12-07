package org.ballistic.dreamjournalai.dream_tools.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MassInterpretation(
    val interpretation: String,
    val listOfDreamIDs: List<String?>,
    val date: Long,
    val model: String,
    val id: String?
): Parcelable {

    constructor() : this(
        "",
        emptyList(),
        0L,
        "",
        null
    )
}
