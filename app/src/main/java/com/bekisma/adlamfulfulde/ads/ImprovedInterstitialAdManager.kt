package com.bekisma.adlamfulfulde.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.concurrent.TimeUnit

class ImprovedInterstitialAdManager(
    private val context: Context,
    private val adUnitId: String
) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    val MIN_TIME_BETWEEN_ADS_MS = TimeUnit.MINUTES.toMillis(3)
    private val MAX_AD_LOADS_PER_HOUR = 10
    private var lastAdShowTime: Long = 0
    private var adLoadsThisHour = 0
    private var hourStartTime: Long = System.currentTimeMillis()

    init {
        resetAdLoadCounterHourly()
    }

    private fun resetAdLoadCounterHourly() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - hourStartTime >= TimeUnit.HOURS.toMillis(1)) {
            adLoadsThisHour = 0
            hourStartTime = currentTime
        }
    }

    fun preloadAd() {
        if (shouldLoadNewAd()) {
            loadAd()
        }
    }

    private fun shouldLoadNewAd(): Boolean {
        resetAdLoadCounterHourly()
        if (isLoading || interstitialAd != null) {
            Log.d("ImprovedAdManager", "Not loading ad: isLoading=$isLoading, adLoaded=${interstitialAd != null}")
            return false
        }
        if (adLoadsThisHour >= MAX_AD_LOADS_PER_HOUR) {
            Log.d("ImprovedAdManager", "Hourly ad load limit reached ($adLoadsThisHour)")
            return false
        }
        Log.d("ImprovedAdManager", "Allowed to load ad. Ad loads this hour: $adLoadsThisHour")
        return true
    }

    private fun loadAd() {
        if (isLoading) return
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        adLoadsThisHour++
        InterstitialAd.load(
            context, adUnitId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    Log.d("ImprovedAdManager", "Ad loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    Log.d("ImprovedAdManager", "Ad failed to load: ${error.message}. Retrying in 1 min.")
                    scheduleAdLoadRetry()
                }
            }
        )
    }

    private fun scheduleAdLoadRetry() {
        if (shouldLoadNewAd()) {
            android.os.Handler(context.mainLooper).postDelayed({
                Log.d("ImprovedAdManager", "Retrying ad load...")
                loadAd()
            }, TimeUnit.MINUTES.toMillis(1))
        } else {
            Log.d("ImprovedAdManager", "Retry scheduled but conditions not met for load.")
        }
    }

    fun canShowAd(): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastAd = currentTime - lastAdShowTime
        val canShow = interstitialAd != null && timeSinceLastAd >= MIN_TIME_BETWEEN_ADS_MS
        if (!canShow) {
            val timeRemaining = MIN_TIME_BETWEEN_ADS_MS - timeSinceLastAd
            val adLoadedStatus = interstitialAd != null
            Log.d("ImprovedAdManager", "Cannot show ad. Ad Loaded: $adLoadedStatus, Time since last: ${timeSinceLastAd/1000}s (Need ${MIN_TIME_BETWEEN_ADS_MS/1000}s, Remaining: ${if (timeRemaining > 0) timeRemaining/1000 else 0}s)")
        }
        return canShow
    }


    fun showAd(onAdDismissed: () -> Unit) {
        val activity = context as? Activity
        if (activity == null) {
            Log.e("ImprovedAdManager", "Context is not an Activity, cannot show ad")
            onAdDismissed()
            return
        }
        val ad = interstitialAd
        if (ad == null) {
            Log.e("ImprovedAdManager", "Ad is not loaded, cannot show ad")
            onAdDismissed()
            return
        }
        lastAdShowTime = System.currentTimeMillis()
        Log.d("ImprovedAdManager", "Showing ad. Last ad show time updated.")
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("ImprovedAdManager", "Ad dismissed fullscreen content")
                interstitialAd = null
                onAdDismissed()
                android.os.Handler(context.mainLooper).postDelayed({ preloadAd() }, TimeUnit.SECONDS.toMillis(30))
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e("ImprovedAdManager", "Failed to show ad: ${error.message}")
                interstitialAd = null
                onAdDismissed()
                android.os.Handler(context.mainLooper).postDelayed({ preloadAd() }, TimeUnit.MINUTES.toMillis(1))
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("ImprovedAdManager", "Ad showed fullscreen content.")
            }
        }
        try {
            ad.show(activity)
        } catch (e: Exception) {
            Log.e("ImprovedAdManager", "Exception while trying to show ad", e)
            interstitialAd = null
            onAdDismissed()
        }
    }
}