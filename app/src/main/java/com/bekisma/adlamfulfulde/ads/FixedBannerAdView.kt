package com.bekisma.adlamfulfulde.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bekisma.adlamfulfulde.BuildConfig
import com.bekisma.adlamfulfulde.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun FixedBannerAdView(
    modifier: Modifier = Modifier,
    showAdLabel: Boolean = true,
    isFixed: Boolean = false
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
    
    val fixedHeight = if (isFixed) 50.dp else 60.dp
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(fixedHeight),
        shape = if (isFixed) RoundedCornerShape(0.dp) else RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFixed) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isFixed) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFixed) 4.dp else 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Label publicitaire
            if (showAdLabel && !isFixed) {
                Text(
                    text = "Publicité",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            
            // Contenu principal
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (hasError) {
                    // Ne rien afficher en cas d'erreur
                    Spacer(modifier = Modifier.height(0.dp))
                } else {
                    AndroidView(
                        factory = { ctx ->
                            AdView(ctx).apply {
                                // Utiliser une taille fixe pour éviter les problèmes de layout
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
}

@Composable
fun FixedBottomBannerAd(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        FixedBannerAdView(
            showAdLabel = false,
            isFixed = true
        )
    }
}

@Composable
fun FixedInlineBannerAd(
    modifier: Modifier = Modifier,
    verticalPadding: Int = 16
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding.dp)
    ) {
        FixedBannerAdView(
            showAdLabel = true,
            isFixed = false
        )
    }
}

@Composable
fun FixedTopBannerAd(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        FixedBannerAdView(
            showAdLabel = false,
            isFixed = true
        )
    }
}