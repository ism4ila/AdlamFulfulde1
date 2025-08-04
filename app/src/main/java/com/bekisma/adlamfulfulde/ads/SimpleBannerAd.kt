package com.bekisma.adlamfulfulde.ads

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bekisma.adlamfulfulde.BuildConfig
import com.bekisma.adlamfulfulde.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun SimpleBannerAd(
    modifier: Modifier = Modifier,
    showAdLabel: Boolean = true,
    maxRetries: Int = 2,
    retryDelayMs: Long = 3000L,
    adSize: AdSize = AdSize.BANNER
) {
    val context = LocalContext.current
    var isAdLoaded by remember { mutableStateOf(false) }
    var adError by remember { mutableStateOf<String?>(null) }
    var retryCount by remember { mutableStateOf(0) }
    var adView by remember { mutableStateOf<AdView?>(null) }
    
    // Afficher les pubs seulement dans la version gratuite
    if (!BuildConfig.ENABLE_ADS || BuildConfig.IS_PRO_VERSION) {
        return
    }
    
    // Check if ads can be requested
    if (!AdMobManager.canRequestAds()) {
        return
    }
    
    // Retry logic for failed ads
    LaunchedEffect(adError, retryCount) {
        if (adError != null && retryCount < maxRetries) {
            kotlinx.coroutines.delay(retryDelayMs)
            retryCount++
            adError = null
            adView?.loadAd(AdRequest.Builder().build())
        }
    }
    
    // Only show the ad container if we haven't exceeded retry attempts or if ad is loaded
    if (adError != null && retryCount >= maxRetries) {
        // Don't show anything if ad failed and we've exhausted retries
        return
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Étiquette publicitaire
            if (showAdLabel) {
                Text(
                    text = "Publicité",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            
            // Show loading or error state
            when {
                !isAdLoaded && adError == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                adError != null && retryCount < maxRetries -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chargement de la publicité... (${retryCount + 1}/$maxRetries)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                else -> {
                    // Bannière publicitaire
                    AndroidView(
                        factory = { ctx ->
                            AdView(ctx).apply {
                                adView = this
                                setAdSize(adSize)
                                setAdUnitId(ctx.getString(R.string.ad_mob_banner_id))
                                
                                adListener = object : com.google.android.gms.ads.AdListener() {
                                    override fun onAdLoaded() {
                                        super.onAdLoaded()
                                        isAdLoaded = true
                                        adError = null
                                        retryCount = 0
                                    }
                                    
                                    override fun onAdFailedToLoad(loadAdError: com.google.android.gms.ads.LoadAdError) {
                                        super.onAdFailedToLoad(loadAdError)
                                        isAdLoaded = false
                                        adError = "Code: ${loadAdError.code}, Message: ${loadAdError.message}"
                                    }
                                    
                                    override fun onAdImpression() {
                                        super.onAdImpression()
                                        // Track ad impression
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
}

@Composable
fun SimpleAdaptiveBannerAd(
    modifier: Modifier = Modifier,
    showAdLabel: Boolean = true
) {
    val context = LocalContext.current
    
    // Afficher les pubs seulement dans la version gratuite
    if (!BuildConfig.ENABLE_ADS || BuildConfig.IS_PRO_VERSION) {
        return
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showAdLabel) {
                Text(
                    text = "Publicité",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            
            AndroidView(
                factory = { ctx ->
                    AdView(ctx).apply {
                        // Taille adaptative pour bannière
                        val display = (ctx as android.app.Activity).windowManager.defaultDisplay
                        val outMetrics = android.util.DisplayMetrics()
                        display.getMetrics(outMetrics)
                        
                        val density = outMetrics.density
                        val adWidth = (outMetrics.widthPixels / density).toInt()
                        
                        setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidth))
                        setAdUnitId(ctx.getString(R.string.ad_mob_banner_id))
                        loadAd(AdRequest.Builder().build())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}