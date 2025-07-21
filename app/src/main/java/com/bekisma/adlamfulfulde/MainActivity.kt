package com.bekisma.adlamfulfulde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.navigation.AppNavigation
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme
import com.google.android.gms.ads.MobileAds

import com.bekisma.adlamfulfulde.ads.ImprovedInterstitialAdManager
import com.bekisma.adlamfulfulde.data.MenuItem

class MainActivity : ComponentActivity() {
    private var interstitialAdManager: ImprovedInterstitialAdManager? = null
    private lateinit var proManager: ProManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        proManager = ProManager(this)

        MobileAds.initialize(this) {
            if (interstitialAdManager == null) {
                interstitialAdManager = ImprovedInterstitialAdManager(this, getString(R.string.ad_mob_interstitial_id))
                interstitialAdManager?.preloadAd()
            }
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

    private var navigationCounter = 0

    private fun handleNavigation(navController: androidx.navigation.NavController, item: MenuItem) {
        try {
            navigationCounter++
            val AD_NAVIGATION_FREQUENCY = 4
            if (interstitialAdManager != null && navigationCounter >= AD_NAVIGATION_FREQUENCY && interstitialAdManager!!.canShowAd()) {
                navigationCounter = 0
                interstitialAdManager!!.showAd {
                    navController.navigate(item.destination)
                }
            } else {
                navController.navigate(item.destination)
            }
        } catch (e: Exception) {
            navController.navigate(item.destination)
        }
    }
}
