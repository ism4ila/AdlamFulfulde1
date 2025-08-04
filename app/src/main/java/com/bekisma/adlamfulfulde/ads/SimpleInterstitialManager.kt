package com.bekisma.adlamfulfulde.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.bekisma.adlamfulfulde.BuildConfig
import com.bekisma.adlamfulfulde.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object SimpleInterstitialManager {
    private const val TAG = "SimpleInterstitial"
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var lastAdTime = 0L
    private val MIN_INTERVAL = 5 * 60 * 1000L // 5 minutes between ads for better UX

    fun loadAd(context: Context) {
        // Ne pas charger si c'est la version pro ou si les pubs sont désactivées
        if (!BuildConfig.ENABLE_ADS || BuildConfig.IS_PRO_VERSION) {
            return
        }

        if (isLoading) {
            Log.d(TAG, "Ad already loading")
            return
        }

        if (interstitialAd != null) {
            Log.d(TAG, "Ad already loaded")
            return
        }

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            context.getString(R.string.ad_mob_interstitial_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Failed to load interstitial ad: ${adError.message}")
                    Log.e(TAG, "Error code: ${adError.code}")
                    Log.e(TAG, "Error domain: ${adError.domain}")
                    Log.e(TAG, "Error cause: ${adError.cause}")
                    interstitialAd = null
                    isLoading = false
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded successfully")
                    interstitialAd = ad
                    isLoading = false
                }
            }
        )
    }

    fun showAd(
        activity: Activity,
        onAdDismissed: () -> Unit = {},
        onAdFailed: () -> Unit = {}
    ) {
        // Ne pas afficher si c'est la version pro ou si les pubs sont désactivées
        if (!BuildConfig.ENABLE_ADS || BuildConfig.IS_PRO_VERSION) {
            onAdDismissed()
            return
        }

        // Vérifier l'intervalle minimum
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAdTime < MIN_INTERVAL) {
            Log.d(TAG, "Ad interval not reached yet")
            onAdDismissed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    Log.d(TAG, "Interstitial ad was clicked")
                }

                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed")
                    interstitialAd = null
                    lastAdTime = System.currentTimeMillis()
                    onAdDismissed()
                    
                    // Précharger la prochaine pub
                    loadAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Failed to show interstitial ad: ${adError.message}")
                    interstitialAd = null
                    onAdFailed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad showed full screen content")
                }
            }

            ad.show(activity)
        } else {
            Log.w(TAG, "Interstitial ad not ready")
            onAdFailed()
            
            // Essayer de charger une pub pour la prochaine fois
            loadAd(activity)
        }
    }

    fun isAdReady(): Boolean {
        return interstitialAd != null && BuildConfig.ENABLE_ADS && !BuildConfig.IS_PRO_VERSION
    }

    fun preloadAd(context: Context) {
        if (!isAdReady() && !isLoading) {
            loadAd(context)
        }
    }
}