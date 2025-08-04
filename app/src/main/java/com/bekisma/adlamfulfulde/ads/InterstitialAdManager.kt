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

object InterstitialAdManager {
    private var mInterstitialAd: InterstitialAd? = null
    private const val TAG = "InterstitialAdManager"
    private var isLoading = false

    fun loadAd(context: Context, onAdLoaded: (() -> Unit)? = null, onAdFailedToLoad: ((LoadAdError) -> Unit)? = null) {
        // Éviter les chargements multiples simultanés
        if (isLoading) {
            Log.d(TAG, "Ad already loading, skipping")
            return
        }
        
        // Ne pas recharger si une pub est déjà prête
        if (mInterstitialAd != null) {
            Log.d(TAG, "Ad already loaded")
            onAdLoaded?.invoke()
            return
        }
        
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        val adUnitId = context.getString(com.bekisma.adlamfulfulde.R.string.ad_mob_interstitial_id)
        
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "Failed to load interstitial ad: ${adError.message}")
                isLoading = false
                mInterstitialAd = null
                onAdFailedToLoad?.invoke(adError)
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Interstitial ad loaded successfully")
                isLoading = false
                mInterstitialAd = interstitialAd
                onAdLoaded?.invoke()
            }
        })
    }

    fun showAd(
        activity: Activity, 
        onAdDismissed: () -> Unit,
        onAdFailedToShow: ((AdError) -> Unit)? = null,
        onAdShowed: (() -> Unit)? = null
    ) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed")
                    mInterstitialAd = null
                    // Précharger la prochaine pub de manière asynchrone
                    loadAd(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Failed to show interstitial ad: ${adError.message}")
                    mInterstitialAd = null
                    // Réessayer de charger après un échec d'affichage
                    loadAd(activity)
                    onAdFailedToShow?.invoke(adError)
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad showed")
                    onAdShowed?.invoke()
                }
                
                override fun onAdClicked() {
                    Log.d(TAG, "Interstitial ad clicked")
                }
                
                override fun onAdImpression() {
                    Log.d(TAG, "Interstitial ad impression")
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            Log.w(TAG, "Interstitial ad not ready")
            onAdDismissed()
        }
    }
    
    fun isAdReady(): Boolean {
        return mInterstitialAd != null
    }
}
