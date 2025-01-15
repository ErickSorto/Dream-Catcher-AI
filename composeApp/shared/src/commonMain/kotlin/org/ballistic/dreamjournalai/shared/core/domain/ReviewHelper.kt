package org.ballistic.dreamjournalai.shared.core.domain

import com.mikhailovskii.inappreview.ReviewCode
import kotlinx.coroutines.flow.Flow

interface ReviewHelper {
    // Provide a method to request in-app review
    fun requestInAppReview(): Flow<ReviewCode>
}