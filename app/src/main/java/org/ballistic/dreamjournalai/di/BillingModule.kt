package org.ballistic.dreamjournalai.di

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import org.ballistic.dreamjournalai.dream_store.data.repository.BillingRepositoryImpl
import org.ballistic.dreamjournalai.dream_store.domain.repository.BillingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val billingModule = module {
    single {
        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    val billingRepository: BillingRepository = get()
                    (billingRepository as? BillingRepositoryImpl)?.getPurchaseListener()?.invoke(purchase)
                }
            }
        }

        BillingClient.newBuilder(androidContext())
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    single<BillingRepository> { BillingRepositoryImpl(get()) }
}