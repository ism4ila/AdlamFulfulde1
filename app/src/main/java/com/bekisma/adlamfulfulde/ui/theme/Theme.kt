package com.bekisma.adlamfulfulde.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- ADD Imports for the Enums from their single source of truth ---
import com.bekisma.adlamfulfulde.ThemeMode
import com.bekisma.adlamfulfulde.ColorTheme
// --- End ADD Imports ---

import com.bekisma.adlamfulfulde.ColorSchemes
import com.bekisma.adlamfulfulde.ThemeManager


/**
 * Thème principal de l'application Adlam Fulfulde
 * Supporte les thèmes culturels peuls et l'accessibilité
 * 
 * @param enableHighContrast Active le mode contraste élevé pour l'accessibilité
 * @param enableDynamicColors Active les couleurs dynamiques sur Android 12+ (optionnel)
 * @param content Le contenu de l'application
 */
@Composable
fun AdlamFulfuldeTheme(
    enableHighContrast: Boolean = false,
    enableDynamicColors: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    // Remember the ThemeManager at the top level of the theme composable
    val themeManager = remember { ThemeManager(context) }

    // Collect the theme states from DataStore.
    // These states changing will trigger recomposition of AdlamFulfuldeTheme
    val themeMode by themeManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    // Use the imported ColorTheme directly
    val colorTheme by themeManager.colorTheme.collectAsState(initial = ColorTheme.TRADITIONAL_PEUL)

    // Determine if dark theme should be used based on the collected themeMode
    val shouldUseDarkTheme = when(themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        // --- ADD the else branch here ---
        else -> isSystemInDarkTheme() // Fallback for unexpected enum values
        // --- End ADD ---
    }

    // Select the appropriate color scheme based on the color theme and dark mode
    val baseColorScheme = when {
        shouldUseDarkTheme -> when(colorTheme) {
            // Nouveaux thèmes culturels peuls
            ColorTheme.TRADITIONAL_PEUL -> ColorSchemes.TraditionalPeulDarkColors
            ColorTheme.OCRE_EARTH -> ColorSchemes.OcreEarthDarkColors
            ColorTheme.INDIGO_WISDOM -> ColorSchemes.IndigoWisdomDarkColors
            ColorTheme.GOLD_PROSPERITY -> ColorSchemes.GoldProsperityDarkColors
            ColorTheme.GREEN_PASTURE -> ColorSchemes.GreenPastureDarkColors
            ColorTheme.MODERN_DARK -> ColorSchemes.ModernDarkDarkColors
            else -> ColorSchemes.TraditionalPeulDarkColors // Default to Traditional Peul
        }
        else -> when(colorTheme) {
            // Nouveaux thèmes culturels peuls
            ColorTheme.TRADITIONAL_PEUL -> ColorSchemes.TraditionalPeulLightColors
            ColorTheme.OCRE_EARTH -> ColorSchemes.OcreEarthLightColors
            ColorTheme.INDIGO_WISDOM -> ColorSchemes.IndigoWisdomLightColors
            ColorTheme.GOLD_PROSPERITY -> ColorSchemes.GoldProsperityLightColors
            ColorTheme.GREEN_PASTURE -> ColorSchemes.GreenPastureLightColors
            ColorTheme.MODERN_DARK -> ColorSchemes.ModernDarkLightColors
            else -> ColorSchemes.TraditionalPeulLightColors // Default to Traditional Peul
        }
    }

    // Apply high contrast modifications if enabled
    val colorScheme = if (enableHighContrast) {
        baseColorScheme.copy(
            primary = if (shouldUseDarkTheme) Color.White else Color.Black,
            onPrimary = if (shouldUseDarkTheme) Color.Black else Color.White,
            surface = if (shouldUseDarkTheme) Color.Black else Color.White,
            onSurface = if (shouldUseDarkTheme) Color.White else Color.Black,
            background = if (shouldUseDarkTheme) Color.Black else Color.White,
            onBackground = if (shouldUseDarkTheme) Color.White else Color.Black
        )
    } else {
        baseColorScheme
    }

    // Apply the selected color scheme using MaterialTheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = com.bekisma.adlamfulfulde.ui.theme.Typography,
        content = content // Render the rest of the app's UI tree
    )
}

/**
 * NOTES D'AMÉLIORATION ET MEILLEURES PRATIQUES
 * 
 * 1. ACCESSIBILITÉ :
 * - Tous les rapports de contraste respectent maintenant WCAG 2.1 AA (4.5:1 minimum)
 * - Option de contraste élevé disponible pour les utilisateurs avec difficultés visuelles
 * - Tailles de police augmentées pour une meilleure lisibilité
 * 
 * 2. THÈMES CULTURELS :
 * - Couleurs authentiques de la culture peule/Fulani
 * - 6 thèmes distincts représentant différents aspects culturels
 * - Support pour les couleurs d'apprentissage (correct/incorrect/progression)
 * 
 * 3. MATERIAL DESIGN 3 :
 * - Tous les rôles de couleur Material 3 sont implémentés
 * - Support des couleurs outline, inverse, et scrim
 * - Couleurs d'erreur améliorées pour une meilleure UX
 * 
 * 4. SUPPORT ADLAM :
 * - Styles typographiques spécialisés pour l'écriture Adlam
 * - Couleurs optimisées pour l'affichage des caractères Adlam
 * - Préparation pour l'intégration de polices Adlam (Kigelia, ADLaM Display)
 * 
 * 5. RECOMMANDATIONS FUTURES :
 * - Ajouter la police Kigelia ou ADLaM Display pour un support natif Adlam
 * - Implémenter les couleurs dynamiques Android 12+ si souhaité
 * - Considérer l'ajout de thèmes saisonniers ou régionaux
 * - Tester l'accessibilité avec des outils comme TalkBack
 */

