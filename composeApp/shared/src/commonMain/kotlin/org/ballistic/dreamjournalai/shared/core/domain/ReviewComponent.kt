package org.ballistic.dreamjournalai.shared.core.domain

import com.mikhailovskii.inappreview.InAppReviewDelegate
import com.mikhailovskii.inappreview.ReviewCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class ReviewComponent(
    private val inAppReviewDelegate: InAppReviewDelegate
) {

    /**
     * Optionally call init() if the delegate requires it on certain stores (e.g. AppGallery).
     */
    fun init() {
        inAppReviewDelegate.init()
    }

    /**
     * Launch the in-app review flow. The returned Flow emits a [ReviewCode]
     * that indicates how the review request concluded.
     */
    fun requestInAppReview(): Flow<ReviewCode> {
        return inAppReviewDelegate.requestInAppReview()
            .catch { it.printStackTrace() } // optional error handling
    }

    /**
     * Alternatively, launch an in-market review (opens the store listing).
     */
    fun requestInMarketReview(): Flow<ReviewCode> {
        return inAppReviewDelegate.requestInMarketReview()
            .catch { it.printStackTrace() } // optional error handling
    }
}