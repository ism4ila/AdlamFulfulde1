package com.bekisma.adlamfulfulde.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

/**
 * Utilitaires pour les thèmes et l'accessibilité
 * Fournit des fonctions d'aide pour améliorer l'expérience utilisateur
 */

/**
 * Calcule le ratio de contraste entre deux couleurs
 * Basé sur les guidelines WCAG 2.1
 * @param color1 Première couleur
 * @param color2 Deuxième couleur
 * @return Ratio de contraste (1:1 à 21:1)
 */
fun contrastRatio(color1: Color, color2: Color): Float {
    val luminance1 = color1.luminance()
    val luminance2 = color2.luminance()
    val lighterLuminance = max(luminance1, luminance2)
    val darkerLuminance = min(luminance1, luminance2)
    return (lighterLuminance + 0.05f) / (darkerLuminance + 0.05f)
}

/**
 * Vérifie si une combinaison de couleurs respecte les standards WCAG AA
 * @param foreground Couleur du texte
 * @param background Couleur de fond
 * @param isLargeText true si le texte est considéré comme grand (18sp+ gras ou 24sp+ normal)
 * @return true si le contraste est suffisant
 */
fun isAccessibleContrast(
    foreground: Color,
    background: Color,
    isLargeText: Boolean = false
): Boolean {
    val ratio = contrastRatio(foreground, background)
    return if (isLargeText) ratio >= 3.0f else ratio >= 4.5f
}

/**
 * Obtient une couleur d'accentuation culturellement appropriée
 * selon le contexte d'apprentissage
 */
@Composable
@ReadOnlyComposable
fun getEducationalAccentColor(): Color {
    return when {
        isSystemInDarkTheme() -> PeulGoldLight
        else -> PeulIndigo
    }
}

/**
 * Obtient une couleur de feedback pour les réponses d'apprentissage
 */
@Composable
@ReadOnlyComposable
fun getFeedbackColor(isCorrect: Boolean, isNeutral: Boolean = false): Color {
    return when {
        isNeutral -> MaterialTheme.colorScheme.outline
        isCorrect -> PeulCorrect
        else -> PeulIncorrect
    }
}

/**
 * Couleurs de progression pour l'apprentissage
 */
@Composable
@ReadOnlyComposable
fun getProgressColors(): ProgressColors {
    val isDark = isSystemInDarkTheme()
    return ProgressColors(
        completed = if (isDark) PeulCorrect else PeulSuccess,
        inProgress = if (isDark) PeulGoldLight else PeulProgress,
        notStarted = MaterialTheme.colorScheme.outline,
        background = MaterialTheme.colorScheme.surfaceVariant
    )
}

/**
 * Data class pour les couleurs de progression
 */
data class ProgressColors(
    val completed: Color,
    val inProgress: Color,
    val notStarted: Color,
    val background: Color
)

/**
 * Obtient une couleur adaptée pour l'affichage de l'écriture Adlam
 */
@Composable
@ReadOnlyComposable
fun getAdlamTextColor(): Color {
    return when {
        isSystemInDarkTheme() -> PeulAdlamAccent
        else -> PeulAdlamPrimary
    }
}

/**
 * Extensions pour Material Theme avec couleurs culturelles
 */
object PeulThemeColors {
    /**
     * Couleur principale pour les éléments culturels
     */
    val primary: Color
        @Composable
        @ReadOnlyComposable
        get() = when {
            isSystemInDarkTheme() -> PeulOcreLight
            else -> PeulOcre
        }
    
    /**
     * Couleur d'accent pour les éléments décoratifs
     */
    val accent: Color
        @Composable
        @ReadOnlyComposable
        get() = when {
            isSystemInDarkTheme() -> PeulGoldLight
            else -> PeulGold
        }
    
    /**
     * Couleur pour les éléments de sagesse/tradition
     */
    val wisdom: Color
        @Composable
        @ReadOnlyComposable
        get() = when {
            isSystemInDarkTheme() -> PeulIndigoLight
            else -> PeulIndigo
        }
}

/**
 * Vérifie si le thème actuel est adapté aux personnes avec des difficultés visuelles
 */
@Composable
fun isHighContrastTheme(): Boolean {
    val colorScheme = MaterialTheme.colorScheme
    val primaryContrast = contrastRatio(colorScheme.onPrimary, colorScheme.primary)
    val surfaceContrast = contrastRatio(colorScheme.onSurface, colorScheme.surface)
    
    return primaryContrast >= 7.0f && surfaceContrast >= 7.0f
}

/**
 * Applique une variante à contraste élevé d'une couleur si nécessaire
 */
@Composable
fun Color.enhanceContrastIfNeeded(backgroundColor: Color): Color {
    return if (isAccessibleContrast(this, backgroundColor)) {
        this
    } else {
        // Ajuste la luminosité pour améliorer le contraste
        val backgroundLuminance = backgroundColor.luminance()
        if (backgroundLuminance > 0.5f) {
            // Fond clair, assombrir le texte
            Color(
                red = this.red * 0.7f,
                green = this.green * 0.7f,
                blue = this.blue * 0.7f,
                alpha = this.alpha
            )
        } else {
            // Fond sombre, éclaircir le texte
            Color(
                red = min(1f, this.red * 1.3f),
                green = min(1f, this.green * 1.3f),
                blue = min(1f, this.blue * 1.3f),
                alpha = this.alpha
            )
        }
    }
}