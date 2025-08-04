package com.bekisma.adlamfulfulde.ads

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Helper object pour gérer le placement optimal des publicités dans l'app
 * selon les meilleures pratiques UX/UI
 */
object AdPlacementHelper {
    
    /**
     * Espacement recommandé entre les éléments de contenu et les publicités
     */
    val CONTENT_AD_SPACING = 16.dp
    val SECTION_AD_SPACING = 24.dp
    
    /**
     * Composable pour placer une bannière entre les sections de contenu
     */
    @Composable
    fun SectionBannerAd(
        modifier: Modifier = Modifier,
        showTopSpacing: Boolean = true,
        showBottomSpacing: Boolean = true
    ) {
        if (showTopSpacing) {
            Spacer(modifier = Modifier.height(SECTION_AD_SPACING))
        }
        
        FixedInlineBannerAd(
            modifier = modifier.padding(horizontal = 16.dp)
        )
        
        if (showBottomSpacing) {
            Spacer(modifier = Modifier.height(SECTION_AD_SPACING))
        }
    }
    
    /**
     * Native ads removed - keeping only banner and interstitial ads
     */
    @Composable
    fun ListNativeAd(
        modifier: Modifier = Modifier,
        itemSpacing: Int = 16
    ) {
        // No-op: Native ads have been removed
    }
    
    /**
     * Composable pour banner flottante en bas d'écran
     */
    @Composable
    fun FloatingBottomBanner(
        modifier: Modifier = Modifier
    ) {
        FixedBottomBannerAd(
            modifier = modifier
        )
    }
    
    /**
     * Détermine si on doit afficher une pub native dans une liste
     * selon la position de l'item (recommandations AdMob)
     */
    fun shouldShowNativeAdAtPosition(
        position: Int,
        totalItems: Int,
        frequency: Int = 5 // Tous les 5 items
    ): Boolean {
        return position > 0 && 
               position % frequency == 0 && 
               position < totalItems - 1 // Pas en dernière position
    }
    
    /**
     * Calcule la position optimale pour une pub interstitielle
     */
    fun shouldShowInterstitialAd(
        userActionCount: Int,
        minActions: Int = 3
    ): Boolean {
        return userActionCount >= minActions && userActionCount % minActions == 0
    }
}