package org.ballistic.dreamjournalai.dream_store.data.api

import com.google.errorprone.annotations.Keep
import org.ballistic.dreamjournalai.dream_store.domain.entities.PurchaseVerificationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
interface PurchaseVerificationApi {
    @GET("verifyPurchases")
    suspend fun verifyPurchase(
        @Query("purchaseToken") purchaseToken: String,
        @Query("purchaseTime") purchaseTime: Long,
        @Query("orderId") orderId: String,
        @Query("userId") userId: String,
        @Query("dreamTokens") dreamTokens: Int
    ): Response<PurchaseVerificationResponse>
}