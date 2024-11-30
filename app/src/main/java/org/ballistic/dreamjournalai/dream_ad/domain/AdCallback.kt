package org.ballistic.dreamjournalai.dream_ad.domain

import com.google.android.gms.ads.rewarded.RewardItem

interface AdCallback {
    fun onAdLoaded()
    fun onAdFailedToLoad(errorCode: Int)
    fun onAdOpened()
    fun onAdClosed()
    fun onAdRewarded(reward: RewardItem)
    fun onAdLeftApplication()
}