package org.ballistic.dreamjournalai.dream_ad.data

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import org.ballistic.dreamjournalai.dream_ad.domain.AdCallback
import org.ballistic.dreamjournalai.dream_ad.domain.AdManagerRepository

class AdManagerRepositoryImpl(
    private val adUnitId: String
) : AdManagerRepository {

    private var rewardedAd: RewardedAd? = null


    override fun loadRewardedAd(
        activity: Activity, callback: () -> Unit) {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            activity,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    callback()
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    callback()
                }
            }
        )
    }

    override fun showRewardedAd(activity: Activity, callback: AdCallback) {
        if (rewardedAd != null) {
            rewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    callback.onAdClosed()
                    rewardedAd = null
                    loadRewardedAd(activity) {}
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    callback.onAdFailedToLoad(p0.code)
                }

                override fun onAdShowedFullScreenContent() {
                    callback.onAdOpened()
                }
            }

            rewardedAd!!.show(activity) {
                callback.onAdRewarded(it)
            }
        } else {
            callback.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR)
        }
    }
}
