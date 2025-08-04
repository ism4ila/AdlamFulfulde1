package com.bekisma.adlamfulfulde.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object AdMobManager {
    private var isInitialized = false
    private var hasConsent = false
    private const val TAG = "AdMobManager"
    
    suspend fun initialize(application: Application): Boolean {
        if (isInitialized) {
            Log.d(TAG, "AdMob already initialized")
            return true
        }
        
        return try {
            Log.d(TAG, "Initializing AdMob SDK...")
            
            // Educational ad helper removed - only using basic banner and interstitial ads
            
            // Utiliser Dispatchers.IO pour l'initialisation selon les meilleures pratiques
            val initializationStatus = suspendCancellableCoroutine { continuation ->
                MobileAds.initialize(application) { status ->
                    if (continuation.isActive) {
                        continuation.resume(status)
                    }
                }
            }
            
            initializationStatus.adapterStatusMap.forEach { (adapter, status) ->
                Log.d(TAG, "AdMob Adapter: $adapter, Status: ${status.initializationState}")
            }
            
            // Configure for educational app
            setChildDirectedTreatment(false) // Adjust based on your target audience
            
            isInitialized = true
            Log.d(TAG, "AdMob SDK initialized successfully")
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AdMob SDK: ${e.message}")
            false
        }
    }
    
    suspend fun initializeWithConsent(activity: Activity): Boolean {
        if (isInitialized && hasConsent) {
            Log.d(TAG, "AdMob already initialized with consent")
            return true
        }
        
        return try {
            Log.d(TAG, "Initializing consent management...")
            
            // Initialize consent first
            when (val consentResult = ConsentManager.initialize(activity)) {
                is ConsentManager.ConsentResult.Success -> {
                    hasConsent = ConsentManager.canRequestAds()
                    Log.d(TAG, "Consent initialization successful. Can request ads: $hasConsent")
                }
                is ConsentManager.ConsentResult.Error -> {
                    Log.e(TAG, "Consent initialization failed: ${consentResult.message}")
                    hasConsent = false
                }
            }
            
            // Then initialize AdMob
            val adMobInitialized = initialize(activity.application)
            
            if (adMobInitialized && hasConsent) {
                preloadAllAds(activity)
            }
            
            adMobInitialized
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AdMob with consent: ${e.message}")
            false
        }
    }
    
    fun setTestConfiguration(testDeviceIds: List<String> = emptyList()) {
        if (testDeviceIds.isNotEmpty()) {
            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
            Log.d(TAG, "Test configuration set with device IDs: $testDeviceIds")
        }
    }
    
    private fun preloadAds(context: Context) {
        try {
            Log.d(TAG, "Preloading banner and interstitial ads...")
            
            // Only preload interstitial ads for simplified implementation
            SimpleInterstitialManager.preloadAd(context)
            Log.d(TAG, "Interstitial ad preload initiated")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error preloading ads: ${e.message}")
        }
    }
    
    fun preloadAllAds(context: Context) {
        if (!isInitialized) {
            Log.w(TAG, "AdMob not initialized, cannot preload ads")
            return
        }
        
        CoroutineScope(Dispatchers.Main).launch {
            preloadAds(context)
        }
    }
    
    fun setChildDirectedTreatment(isChildDirected: Boolean) {
        val requestConfiguration = RequestConfiguration.Builder()
            .setTagForChildDirectedTreatment(
                if (isChildDirected) 
                    RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
                else 
                    RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE
            )
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        Log.d(TAG, "Child directed treatment set to: $isChildDirected")
    }
    
    fun canRequestAds(): Boolean {
        return isInitialized && hasConsent && ConsentManager.canRequestAds()
    }
    
    fun hasConsentForAds(): Boolean = hasConsent
    
    fun isInitialized(): Boolean = isInitialized
}