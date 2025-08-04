package com.bekisma.adlamfulfulde.ads

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.imePadding
import com.bekisma.adlamfulfulde.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun ImprovedBannerAdView(
    modifier: Modifier = Modifier,
    showAdLabel: Boolean = true,
    cornerRadius: Int = 12,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var isAdLoaded by remember { mutableStateOf(false) }
    
    val adView = remember {
        AdView(context).apply {
            val displayMetrics = context.resources.displayMetrics
            val adWidthPixels = if (configuration.screenWidthDp != 0) {
                val screenWidthDp = configuration.screenWidthDp
                (screenWidthDp * density.density).toInt()
            } else {
                displayMetrics.widthPixels
            }
            
            val adWidth = (adWidthPixels / density.density).toInt()
            val adaptiveAdSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                context, 
                adWidth
            )
            
            setAdSize(adaptiveAdSize)
            setAdUnitId(context.getString(R.string.ad_mob_banner_id))
            
            adListener = object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isLoading = false
                    hasError = true
                    isAdLoaded = false
                }
                
                override fun onAdLoaded() {
                    isLoading = false
                    hasError = false
                    isAdLoaded = true
                }
                
                override fun onAdOpened() {
                    // Analytics: ad clicked
                }
            }
        }
    }
    
    DisposableEffect(adView) {
        adView.loadAd(AdRequest.Builder().build())
        
        onDispose {
            adView.destroy()
        }
    }
    
    // Ne s'affiche que si la pub est chargée ou en cours de chargement
    AnimatedVisibility(
        visible = isAdLoaded || isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerRadius.dp))
                .background(backgroundColor)
        ) {
            // Indicateur "Publicité" optionnel
            if (showAdLabel && (isAdLoaded || isLoading)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Publicité",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Light
                    )
                }
            }
            
            // Contenu principal
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        // État de chargement avec shimmer
                        ShimmerBannerPlaceholder()
                    }
                    hasError -> {
                        // État d'erreur discret - ne s'affiche pas
                        Spacer(modifier = Modifier.height(0.dp))
                    }
                    isAdLoaded -> {
                        // Publicité chargée avec animation
                        AndroidView(
                            modifier = Modifier.fillMaxWidth(),
                            factory = { adView }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShimmerBannerPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
    }
}

// Version avec SafeArea pour placement en bas d'écran
@Composable
fun SafeBannerAdView(
    modifier: Modifier = Modifier,
    showAdLabel: Boolean = false
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(), // Évite le clavier virtuel
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        ImprovedBannerAdView(
            showAdLabel = showAdLabel,
            backgroundColor = Color.Transparent,
            cornerRadius = 0
        )
    }
}

// Version pour placement dans le contenu avec espacement approprié
@Composable
fun InlineBannerAdView(
    modifier: Modifier = Modifier,
    showAdLabel: Boolean = true,
    verticalPadding: Int = 16
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding.dp)
    ) {
        ImprovedBannerAdView(
            showAdLabel = showAdLabel,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            cornerRadius = 12
        )
    }
}