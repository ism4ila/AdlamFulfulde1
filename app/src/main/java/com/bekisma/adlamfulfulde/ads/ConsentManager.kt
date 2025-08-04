package com.bekisma.adlamfulfulde.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.bekisma.adlamfulfulde.BuildConfig
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object ConsentManager {
    private const val TAG = "ConsentManager"
    private var consentInformation: ConsentInformation? = null
    private var isConsentGatheringRequired = false
    
    suspend fun initialize(activity: Activity): ConsentResult {
        return suspendCancellableCoroutine { continuation ->
            try {
                consentInformation = UserMessagingPlatform.getConsentInformation(activity)
                
                val params = if (BuildConfig.DEBUG) {
                    // Debug settings for testing only
                    val debugSettings = ConsentDebugSettings.Builder(activity)
                        .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                        .addTestDeviceHashedId("YOUR_TEST_DEVICE_ID_HERE") // Replace with actual test device ID
                        .build()
                    
                    ConsentRequestParameters.Builder()
                        .setConsentDebugSettings(debugSettings)
                        .setTagForUnderAgeOfConsent(false) // Educational app - consider your target audience
                        .build()
                } else {
                    // Production settings - no debug configuration
                    ConsentRequestParameters.Builder()
                        .setTagForUnderAgeOfConsent(false) // Set to true if targeting children under 13
                        .build()
                }
                
                consentInformation?.requestConsentInfoUpdate(
                    activity,
                    params,
                    {
                        // Consent info update succeeded
                        if (consentInformation?.isConsentFormAvailable == true) {
                            loadConsentForm(activity) { result ->
                                if (continuation.isActive) {
                                    continuation.resume(result)
                                }
                            }
                        } else {
                            // No consent form available
                            Log.d(TAG, "No consent form available")
                            if (continuation.isActive) {
                                continuation.resume(ConsentResult.Success(false))
                            }
                        }
                    },
                    { error ->
                        // Consent info update failed
                        Log.e(TAG, "Consent info update failed: ${error.message}")
                        if (continuation.isActive) {
                            continuation.resume(ConsentResult.Error(error.message ?: "Unknown error"))
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing consent: ${e.message}")
                if (continuation.isActive) {
                    continuation.resume(ConsentResult.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }
    
    private fun loadConsentForm(activity: Activity, callback: (ConsentResult) -> Unit) {
        UserMessagingPlatform.loadConsentForm(
            activity,
            { consentForm ->
                isConsentGatheringRequired = consentInformation?.consentStatus == ConsentInformation.ConsentStatus.REQUIRED
                
                if (isConsentGatheringRequired) {
                    consentForm.show(activity) { formError ->
                        if (formError != null) {
                            Log.e(TAG, "Consent form error: ${formError.message}")
                            callback(ConsentResult.Error(formError.message ?: "Form error"))
                        } else {
                            Log.d(TAG, "Consent form dismissed")
                            callback(ConsentResult.Success(true))
                        }
                    }
                } else {
                    // Consent already obtained or not required
                    callback(ConsentResult.Success(false))
                }
            },
            { formError ->
                Log.e(TAG, "Failed to load consent form: ${formError.message}")
                callback(ConsentResult.Error(formError.message ?: "Failed to load form"))
            }
        )
    }
    
    fun canRequestAds(): Boolean {
        val consentStatus = consentInformation?.consentStatus
        return consentStatus == ConsentInformation.ConsentStatus.OBTAINED ||
                consentStatus == ConsentInformation.ConsentStatus.NOT_REQUIRED
    }
    
    fun getConsentStatus(): Int? {
        return consentInformation?.consentStatus
    }
    
    fun isPrivacyOptionsRequired(): Boolean {
        return consentInformation?.privacyOptionsRequirementStatus == 
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }
    
    fun showPrivacyOptionsForm(activity: Activity, callback: (ConsentResult) -> Unit) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                Log.e(TAG, "Privacy options form error: ${formError.message}")
                callback(ConsentResult.Error(formError.message ?: "Privacy form error"))
            } else {
                Log.d(TAG, "Privacy options form dismissed")
                callback(ConsentResult.Success(true))
            }
        }
    }
    
    sealed class ConsentResult {
        data class Success(val consentShown: Boolean) : ConsentResult()
        data class Error(val message: String) : ConsentResult()
    }
}