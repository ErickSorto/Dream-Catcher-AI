package org.ballistic.dreamjournalai.shared.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.lexilabs.basic.ads.DependsOnGoogleMobileAds
import app.lexilabs.basic.ads.RewardedAd
import coil3.compose.LocalPlatformContext

@OptIn(DependsOnGoogleMobileAds::class)
@Composable
fun RewardedAdComposable(
    adUnitId: String,
    onAdDismissed: () -> Unit,
    onRewardEarned: () -> Unit,
    onAdShown: () -> Unit = {},
    onAdImpression: () -> Unit = {},
    onAdClicked: () -> Unit = {},
    onAdFailed: () -> Unit
) {
    val showAd = remember { mutableStateOf(true) }
    val context = LocalPlatformContext.current
    if (showAd.value) {
        RewardedAd(
            activity = context,
            adUnitId = adUnitId,
            onDismissed = {
                showAd.value = false
                onAdDismissed()
            },
            onRewardEarned = {
                onRewardEarned()
            },
            onShown = {
                onAdShown()
            },
            onImpression = {
                onAdImpression()
            },
            onClick = {
                onAdClicked()
            },
            onFailure = {
                showAd.value = false
                onAdFailed()
            }
        )
    }
}