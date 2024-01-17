package org.ballistic.dreamjournalai.di

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.ballistic.dreamjournalai.dream_store.data.repository.BillingRepositoryImpl
import org.ballistic.dreamjournalai.dream_store.domain.repository.BillingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Singleton
    @Provides
    fun provideBillingClient(
        @ApplicationContext context: Context,
        billingRepository: dagger.Lazy<BillingRepository>
    ): BillingClient {
        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    (billingRepository.get() as BillingRepositoryImpl).getPurchaseListener()?.invoke(purchase)
                }
            }
        }

        return BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    @Provides
    @Singleton
    fun provideBillingRepository(
        billingClient: BillingClient
    ): BillingRepository {
        return BillingRepositoryImpl(billingClient)
    }
}