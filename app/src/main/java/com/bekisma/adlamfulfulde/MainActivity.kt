package com.bekisma.adlamfulfulde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.navigation.AppNavigation
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme
import com.bekisma.adlamfulfulde.data.MenuItem
import kotlinx.coroutines.launch

import com.google.android.gms.ads.MobileAds
import com.bekisma.adlamfulfulde.ads.AdMobManager
import com.bekisma.adlamfulfulde.ads.SimpleInterstitialManager
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var proManager: ProManager
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        proManager = ProManager(this)
        
        // Initialize AdMob with consent management (only for free version)
        if (BuildConfig.ENABLE_ADS && !BuildConfig.IS_PRO_VERSION) {
            initializeAdsWithConsent()
        } else {
            Log.d(TAG, "Ads disabled - Pro version or ads not enabled")
        }
        

        setContent {
            val themeManager = remember { ThemeManager(this) }
            val navController = rememberNavController()

            AdlamFulfuldeTheme {
                AppNavigation(
                    navController = navController,
                    onNavigation = { item ->
                        handleNavigation(navController, item)
                    },
                    themeManager = themeManager,
                    proManager = proManager
                )
            }
        }
    }

    private fun initializeAdsWithConsent() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Initializing ads...")
                
                // Initialize AdMob with consent
                val initialized = AdMobManager.initializeWithConsent(this@MainActivity)
                
                if (initialized) {
                    Log.d(TAG, "AdMob initialized successfully")
                    
                    // Précharger les publicités interstitielles
                    SimpleInterstitialManager.loadAd(this@MainActivity)
                    
                } else {
                    Log.w(TAG, "AdMob initialization failed")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing ads: ${e.message}")
            }
        }
    }

    private fun handleNavigation(navController: androidx.navigation.NavController, item: MenuItem) {
        try {
            // Possibilité d'afficher une pub interstitielle avant certaines navigations
            if (shouldShowInterstitialBeforeNavigation(item.destination)) {
                SimpleInterstitialManager.showAd(
                    this,
                    onAdDismissed = {
                        navController.navigate(item.destination)
                    },
                    onAdFailed = {
                        navController.navigate(item.destination)
                    }
                )
            } else {
                // Navigate directly
                navController.navigate(item.destination)
            }
        } catch (e: Exception) {
            // Fallback to direct navigation if there's any error
            navController.navigate(item.destination)
        }
    }
    
    private fun shouldShowInterstitialBeforeNavigation(destination: String): Boolean {
        // Show interstitials only for major learning milestones to avoid disrupting education
        // Reduced frequency for better learning experience
        return when (destination) {
            "quiz" -> true  // Only show for quiz completion
            else -> false   // Removed "writing" to reduce ad frequency
        }
    }
}
