package com.bekisma.adlamfulfulde.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typographie adaptée à l'apprentissage de l'écriture Adlam
 * Optimisée pour la lisibilité, l'accessibilité et l'affichage multilingue
 * Suit les recommandations WCAG 2.1 pour la taille et l'espacement des caractères
 */

// Polices recommandées pour l'écriture Adlam
// Les polices suivantes supportent les caractères Adlam :
// - Kigelia (par SIL International) - recommandée
// - ADLaM Display (par Google Fonts)
// - Source Code Pro (avec support Adlam)
//
// Exemple d'implémentation :
// val AdlamFontFamily = FontFamily(
//     Font(R.font.kigelia_adlam, FontWeight.Normal),
//     Font(R.font.kigelia_adlam_bold, FontWeight.Bold)
// )

val Typography = Typography(
    // Titres principaux - pour les en-têtes d'écrans
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    
    // Titres de sections
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    
    // Titres de cartes et modules
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    
    // Corps de texte - optimisé pour la lecture et l'apprentissage
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp, // Augmenté pour meilleure lisibilité
        lineHeight = 28.sp, // Augmenté pour l'espacement
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp, // Augmenté pour meilleure lisibilité
        lineHeight = 24.sp, // Augmenté pour l'espacement
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp, // Augmenté pour meilleure lisibilité
        lineHeight = 20.sp, // Augmenté pour l'espacement
        letterSpacing = 0.4.sp
    ),
    
    // Labels et éléments d'interface
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// === STYLES SPÉCIALISÉS POUR L'ÉCRITURE ADLAM ===

/**
 * Style typographique spécialement conçu pour l'affichage de l'écriture Adlam
 * Utilise une taille plus grande et un espacement optimisé pour la lisibilité
 */
val AdlamDisplayStyle = TextStyle(
    fontFamily = FontFamily.Default, // À remplacer par AdlamFontFamily quand disponible
    fontWeight = FontWeight.Normal,
    fontSize = 24.sp,
    lineHeight = 36.sp,
    letterSpacing = 0.5.sp
)

/**
 * Style pour les exercices d'écriture Adlam
 */
val AdlamPracticeStyle = TextStyle(
    fontFamily = FontFamily.Default, // À remplacer par AdlamFontFamily quand disponible
    fontWeight = FontWeight.Normal,
    fontSize = 32.sp,
    lineHeight = 48.sp,
    letterSpacing = 1.0.sp
)

/**
 * Style pour les titres en écriture Adlam
 */
val AdlamTitleStyle = TextStyle(
    fontFamily = FontFamily.Default, // À remplacer par AdlamFontFamily quand disponible
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 40.sp,
    letterSpacing = 0.75.sp
)