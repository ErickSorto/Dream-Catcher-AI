package org.ballistic.dreamjournalai.shared.core.data

import com.mikhailovskii.inappreview.InAppReviewDelegate
import com.mikhailovskii.inappreview.ReviewCode
import kotlinx.coroutines.flow.Flow
import org.ballistic.dreamjournalai.shared.core.domain.ReviewHelper

class ReviewHelperImpl(
    private val reviewDelegate: InAppReviewDelegate
) : ReviewHelper {

    override fun requestInAppReview(): Flow<ReviewCode> {
        // The library returns a Flow<ReviewCode> (an Int describing result code)
        return reviewDelegate.requestInAppReview()
    }
}
