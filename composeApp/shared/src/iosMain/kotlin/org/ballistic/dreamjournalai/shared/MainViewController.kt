package org.ballistic.dreamjournalai.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.revenuecat.purchases.kmp.Purchases
import org.ballistic.dreamjournalai.shared.di.initKoin
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.configure
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin(
            null
        )

        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            apiKey = "<YOUR_REVENUECAT_IOS_API_KEY>" //TODO: Add your RevenueCat API Key
        ) {
            appUserId = Firebase.auth.currentUser?.uid
        }
    }
) {
    App()
}