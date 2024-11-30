package org.ballistic.dreamjournalai.di


import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

class AppModuleTest {
    val appModules = module {
        includes(
            appModule,
            billingModule,
            notificationModule,
            signInModule,
            adMobModule,
            viewModelModule
        )
    }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun verifyAppModuleConfiguration() {
        appModules.verify()
    }
}