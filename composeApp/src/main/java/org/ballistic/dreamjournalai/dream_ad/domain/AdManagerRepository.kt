package org.ballistic.dreamjournalai.dream_ad.domain

import android.app.Activity

interface AdManagerRepository {
    fun loadRewardedAd(activity: Activity, callback: () -> Unit)
    fun showRewardedAd(activity: Activity, callback: AdCallback)
}
