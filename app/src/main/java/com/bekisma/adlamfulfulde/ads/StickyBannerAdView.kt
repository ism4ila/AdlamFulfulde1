package com.bekisma.adlamfulfulde.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.bekisma.adlamfulfulde.BuildConfig
import com.bekisma.adlamfulfulde.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun StickyTopBannerAd(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isAdLoaded by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    
    // Ne pas afficher les pubs dans la version pro
    if (!BuildConfig.ENABLE_ADS || BuildConfig.IS_PRO_VERSION) {
        return
    }
    
    // Vérifier si on peut afficher des pubs
    if (!AdMobManager.canRequestAds()) {
        return
    }
    
    // Bannière fixe en haut avec zIndex élevé pour rester au-dessus du contenu
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .zIndex(999f) // Z-index élevé pour rester au-dessus
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (!hasError) {
                AndroidView(
                    factory = { ctx ->
                        AdView(ctx).apply {
                            setAdSize(AdSize.BANNER)
                            setAdUnitId(ctx.getString(R.string.ad_mob_banner_id))
                            
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    isAdLoaded = true
                                    hasError = false
                                }
                                
                                override fun onAdFailedToLoad(adError: LoadAdError) {
                                    isAdLoaded = false
                                    hasError = true
                                }
                            }
                            
                            loadAd(AdRequest.Builder().build())
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun StickyBottomBannerAd(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isAdLoaded by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    
    // Ne pas afficher les pubs dans la version pro
    if (!BuildConfig.ENABLE_ADS || BuildConfig.IS_PRO_VERSION) {
        return
    }
    
    // Vérifier si on peut afficher des pubs
    if (!AdMobManager.canRequestAds()) {
        return
    }
    
    // Bannière fixe en bas avec zIndex élevé pour rester au-dessus du contenu
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .zIndex(999f) // Z-index élevé pour rester au-dessus
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (!hasError) {
                AndroidView(
                    factory = { ctx ->
                        AdView(ctx).apply {
                            setAdSize(AdSize.BANNER)
                            setAdUnitId(ctx.getString(R.string.ad_mob_banner_id))
                            
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    isAdLoaded = true
                                    hasError = false
                                }
                                
                                override fun onAdFailedToLoad(adError: LoadAdError) {
                                    isAdLoaded = false
                                    hasError = true
                                }
                            }
                            
                            loadAd(AdRequest.Builder().build())
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}