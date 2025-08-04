package com.bekisma.adlamfulfulde

import android.content.Context
import androidx.compose.material3.darkColorScheme // Import necessary color functions
import androidx.compose.material3.lightColorScheme // Import necessary color functions
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension function for DataStore (Keep this)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// --- Define Enums HERE as the single source of truth ---
enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class ColorTheme {
    TRADITIONAL_PEUL, OCRE_EARTH, INDIGO_WISDOM, GOLD_PROSPERITY, GREEN_PASTURE, MODERN_DARK
}
// --- End Enum Definitions ---


class ThemeManager(private val context: Context) {

    // Keys for DataStore (Keep this)
    companion object {
        val THEME_MODE_KEY = intPreferencesKey("theme_mode")
        val COLOR_THEME_KEY = intPreferencesKey("color_theme")
    }

    // Get the saved theme mode (Keep this)
    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            // Handle potential null or unknown values by defaulting
            when(preferences[THEME_MODE_KEY]) {
                ThemeMode.LIGHT.ordinal -> ThemeMode.LIGHT
                ThemeMode.DARK.ordinal -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM // Default to SYSTEM
            }
        }

    // Get the saved color theme (Keep this)
    val colorTheme: Flow<ColorTheme> = context.dataStore.data
        .map { preferences ->
            // Handle potential null or unknown values by defaulting
            when(preferences[COLOR_THEME_KEY]) {
                ColorTheme.OCRE_EARTH.ordinal -> ColorTheme.OCRE_EARTH
                ColorTheme.INDIGO_WISDOM.ordinal -> ColorTheme.INDIGO_WISDOM
                ColorTheme.GOLD_PROSPERITY.ordinal -> ColorTheme.GOLD_PROSPERITY
                ColorTheme.GREEN_PASTURE.ordinal -> ColorTheme.GREEN_PASTURE
                ColorTheme.MODERN_DARK.ordinal -> ColorTheme.MODERN_DARK
                else -> ColorTheme.TRADITIONAL_PEUL // Default to TRADITIONAL_PEUL
            }
        }

    // Save theme mode (Keep this)
    suspend fun saveThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.ordinal
        }
    }

    // Save color theme (Keep this)
    suspend fun saveColorTheme(colorTheme: ColorTheme) {
        context.dataStore.edit { preferences ->
            preferences[COLOR_THEME_KEY] = colorTheme.ordinal
        }
    }
}

// Schémas de couleurs culturellement authentiques pour la culture peule
object ColorSchemes {
    
    // === THÈME TRADITIONNEL PEUL (Thème par défaut) ===
    val TraditionalPeulLightColors = lightColorScheme(
        primary = Color(0xFFB8763E),           // Ocre principal - contrast amélioré
        onPrimary = Color(0xFFFFFFFF),         // Blanc sur ocre pour meilleur contraste
        primaryContainer = Color(0xFFF5E6D3),  // Container ocre clair
        onPrimaryContainer = Color(0xFF5D2F0C), // Texte sur container
        
        secondary = Color(0xFF1E4A5F),         // Indigo - contrast amélioré
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE8F1F5), // Container indigo clair
        onSecondaryContainer = Color(0xFF0F2633),
        
        tertiary = Color(0xFFD1A500),          // Or - contrast amélioré
        onTertiary = Color(0xFF3A332B),
        tertiaryContainer = Color(0xFFFAF5E6),
        onTertiaryContainer = Color(0xFF9C7800),
        
        background = Color(0xFFFFFBF7),        // Blanc peul
        onBackground = Color(0xFF3A332B),
        surface = Color(0xFFFAF7F2),           // Surface douce
        onSurface = Color(0xFF3A332B),
        surfaceVariant = Color(0xFFF5F2EE),    // Neutre clair
        onSurfaceVariant = Color(0xFF6B5D52),
        
        // Nouvelles couleurs Material 3
        outline = Color(0xFF79716B),
        outlineVariant = Color(0xFFCAC4BD),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF322F2B),
        inverseOnSurface = Color(0xFFF2EFE9),
        inversePrimary = Color(0xFFE6B584),
        
        // Couleurs d'erreur améliorées
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002)
    )
    
    val TraditionalPeulDarkColors = darkColorScheme(
        primary = Color(0xFFE6B584),           // Ocre clair pour sombre
        onPrimary = Color(0xFF3A332B),
        primaryContainer = Color(0xFF8B5A2B),  // Ocre foncé - amélioré
        onPrimaryContainer = Color(0xFFF5E6D3),
        
        secondary = Color(0xFF6B8FA3),         // Indigo clair
        onSecondary = Color(0xFF0F2633),
        secondaryContainer = Color(0xFF1E4A5F),
        onSecondaryContainer = Color(0xFFE8F1F5),
        
        tertiary = Color(0xFFF2D64D),          // Or clair
        onTertiary = Color(0xFF3A332B),
        tertiaryContainer = Color(0xFF9C7800),
        onTertiaryContainer = Color(0xFFFAF5E6),
        
        background = Color(0xFF1A1612),        // Surface sombre
        onBackground = Color(0xFFF5F2EE),
        surface = Color(0xFF1A1612),
        onSurface = Color(0xFFF5F2EE),
        surfaceVariant = Color(0xFF3A332B),
        onSurfaceVariant = Color(0xFFE0D7D0),
        
        // Nouvelles couleurs Material 3 pour mode sombre
        outline = Color(0xFF938F89),
        outlineVariant = Color(0xFF49453F),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFF2EFE9),
        inverseOnSurface = Color(0xFF322F2B),
        inversePrimary = Color(0xFFB8763E),
        
        // Couleurs d'erreur améliorées pour mode sombre
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6)
    )
    
    // === THÈME TERRE OCRE ===
    val OcreEarthLightColors = lightColorScheme(
        primary = Color(0xFFB85C3E),           // Rouge terre
        onPrimary = Color.White,
        primaryContainer = Color(0xFFF0E0DB),
        onPrimaryContainer = Color(0xFF8B3A1F),
        
        secondary = Color(0xFF8B4513),         // Marron cuir
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF2E8E2),
        onSecondaryContainer = Color(0xFF5D2F0C),
        
        tertiary = Color(0xFFCC8854),          // Ocre
        onTertiary = Color(0xFF3A332B),
        tertiaryContainer = Color(0xFFF5E6D3),
        onTertiaryContainer = Color(0xFFA0622B),
        
        background = Color(0xFFFAF7F2),
        onBackground = Color(0xFF3A332B),
        surface = Color(0xFFF5F2EE),
        onSurface = Color(0xFF3A332B),
        surfaceVariant = Color(0xFFE0D7D0),
        onSurfaceVariant = Color(0xFF6B5D52)
    )
    
    val OcreEarthDarkColors = darkColorScheme(
        primary = Color(0xFFD4896F),
        onPrimary = Color(0xFF8B3A1F),
        primaryContainer = Color(0xFFB85C3E),
        onPrimaryContainer = Color(0xFFF0E0DB),
        
        secondary = Color(0xFFB87556),
        onSecondary = Color(0xFF5D2F0C),
        secondaryContainer = Color(0xFF8B4513),
        onSecondaryContainer = Color(0xFFF2E8E2),
        
        tertiary = Color(0xFFE6B584),
        onTertiary = Color(0xFF3A332B),
        tertiaryContainer = Color(0xFFA0622B),
        onTertiaryContainer = Color(0xFFF5E6D3),
        
        background = Color(0xFF1A1612),
        onBackground = Color(0xFFF5F2EE),
        surface = Color(0xFF1A1612),
        onSurface = Color(0xFFF5F2EE),
        surfaceVariant = Color(0xFF3A332B),
        onSurfaceVariant = Color(0xFFE0D7D0)
    )
    
    // === THÈME SAGESSE INDIGO ===
    val IndigoWisdomLightColors = lightColorScheme(
        primary = Color(0xFF2C5F7C),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE8F1F5),
        onPrimaryContainer = Color(0xFF1A3D52),
        
        secondary = Color(0xFF7BA3C0),
        onSecondary = Color(0xFF1A3D52),
        secondaryContainer = Color(0xFFE8F1F5),
        onSecondaryContainer = Color(0xFF2C5F7C),
        
        tertiary = Color(0xFFCC8854),
        onTertiary = Color(0xFF3A332B),
        tertiaryContainer = Color(0xFFF5E6D3),
        onTertiaryContainer = Color(0xFFA0622B),
        
        background = Color(0xFFFFFBF7),
        onBackground = Color(0xFF3A332B),
        surface = Color(0xFFFAF7F2),
        onSurface = Color(0xFF3A332B),
        surfaceVariant = Color(0xFFF5F2EE),
        onSurfaceVariant = Color(0xFF6B5D52)
    )
    
    val IndigoWisdomDarkColors = darkColorScheme(
        primary = Color(0xFF6B8FA3),
        onPrimary = Color(0xFF1A3D52),
        primaryContainer = Color(0xFF2C5F7C),
        onPrimaryContainer = Color(0xFFE8F1F5),
        
        secondary = Color(0xFF7BA3C0),
        onSecondary = Color(0xFF1A3D52),
        secondaryContainer = Color(0xFF2C5F7C),
        onSecondaryContainer = Color(0xFFE8F1F5),
        
        tertiary = Color(0xFFE6B584),
        onTertiary = Color(0xFF3A332B),
        tertiaryContainer = Color(0xFFA0622B),
        onTertiaryContainer = Color(0xFFF5E6D3),
        
        background = Color(0xFF1A1612),
        onBackground = Color(0xFFF5F2EE),
        surface = Color(0xFF1A1612),
        onSurface = Color(0xFFF5F2EE),
        surfaceVariant = Color(0xFF3A332B),
        onSurfaceVariant = Color(0xFFE0D7D0)
    )
    
    // === THÈME PROSPÉRITÉ OR ===
    val GoldProsperityLightColors = lightColorScheme(
        primary = Color(0xFFE6B800),
        onPrimary = Color(0xFF3A332B),
        primaryContainer = Color(0xFFFAF5E6),
        onPrimaryContainer = Color(0xFFB8900D),
        
        secondary = Color(0xFFCC8854),
        onSecondary = Color(0xFF3A332B),
        secondaryContainer = Color(0xFFF5E6D3),
        onSecondaryContainer = Color(0xFFA0622B),
        
        tertiary = Color(0xFF2C5F7C),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFE8F1F5),
        onTertiaryContainer = Color(0xFF1A3D52),
        
        background = Color(0xFFFFFBF7),
        onBackground = Color(0xFF3A332B),
        surface = Color(0xFFFAF7F2),
        onSurface = Color(0xFF3A332B),
        surfaceVariant = Color(0xFFF5F2EE),
        onSurfaceVariant = Color(0xFF6B5D52)
    )
    
    val GoldProsperityDarkColors = darkColorScheme(
        primary = Color(0xFFF2D64D),
        onPrimary = Color(0xFF3A332B),
        primaryContainer = Color(0xFFB8900D),
        onPrimaryContainer = Color(0xFFFAF5E6),
        
        secondary = Color(0xFFE6B584),
        onSecondary = Color(0xFF3A332B),
        secondaryContainer = Color(0xFFA0622B),
        onSecondaryContainer = Color(0xFFF5E6D3),
        
        tertiary = Color(0xFF6B8FA3),
        onTertiary = Color(0xFF1A3D52),
        tertiaryContainer = Color(0xFF2C5F7C),
        onTertiaryContainer = Color(0xFFE8F1F5),
        
        background = Color(0xFF1A1612),
        onBackground = Color(0xFFF5F2EE),
        surface = Color(0xFF1A1612),
        onSurface = Color(0xFFF5F2EE),
        surfaceVariant = Color(0xFF3A332B),
        onSurfaceVariant = Color(0xFFE0D7D0)
    )
    
    // === THÈME PÂTURAGE VERT ===
    val GreenPastureLightColors = lightColorScheme(
        primary = Color(0xFF5C8A3A),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFEDF3E7),
        onPrimaryContainer = Color(0xFF3E5C26),
        
        secondary = Color(0xFFCC8854),
        onSecondary = Color(0xFF3A332B),
        secondaryContainer = Color(0xFFF5E6D3),
        onSecondaryContainer = Color(0xFFA0622B),
        
        tertiary = Color(0xFFE6B800),
        onTertiary = Color(0xFF3A332B),
        tertiaryContainer = Color(0xFFFAF5E6),
        onTertiaryContainer = Color(0xFFB8900D),
        
        background = Color(0xFFFFFBF7),
        onBackground = Color(0xFF3A332B),
        surface = Color(0xFFFAF7F2),
        onSurface = Color(0xFF3A332B),
        surfaceVariant = Color(0xFFF5F2EE),
        onSurfaceVariant = Color(0xFF6B5D52)
    )
    
    val GreenPastureDarkColors = darkColorScheme(
        primary = Color(0xFF8DB66B),
        onPrimary = Color(0xFF3E5C26),
        primaryContainer = Color(0xFF5C8A3A),
        onPrimaryContainer = Color(0xFFEDF3E7),
        
        secondary = Color(0xFFE6B584),
        onSecondary = Color(0xFF3A332B),
        secondaryContainer = Color(0xFFA0622B),
        onSecondaryContainer = Color(0xFFF5E6D3),
        
        tertiary = Color(0xFFF2D64D),
        onTertiary = Color(0xFF3A332B),
        tertiaryContainer = Color(0xFFB8900D),
        onTertiaryContainer = Color(0xFFFAF5E6),
        
        background = Color(0xFF1A1612),
        onBackground = Color(0xFFF5F2EE),
        surface = Color(0xFF1A1612),
        onSurface = Color(0xFFF5F2EE),
        surfaceVariant = Color(0xFF3A332B),
        onSurfaceVariant = Color(0xFFE0D7D0)
    )
    
    // === THÈME MODERNE SOMBRE ===
    val ModernDarkLightColors = lightColorScheme(
        primary = Color(0xFF3A332B),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFF5F2EE),
        onPrimaryContainer = Color(0xFF3A332B),
        
        secondary = Color(0xFF6B5D52),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE0D7D0),
        onSecondaryContainer = Color(0xFF3A332B),
        
        tertiary = Color(0xFFD4A574),
        onTertiary = Color(0xFF3A332B),
        tertiaryContainer = Color(0xFFF5E6D3),
        onTertiaryContainer = Color(0xFFA0622B),
        
        background = Color(0xFFFFFBF7),
        onBackground = Color(0xFF3A332B),
        surface = Color(0xFFFAF7F2),
        onSurface = Color(0xFF3A332B),
        surfaceVariant = Color(0xFFF5F2EE),
        onSurfaceVariant = Color(0xFF6B5D52)
    )
    
    val ModernDarkDarkColors = darkColorScheme(
        primary = Color(0xFFE0D7D0),
        onPrimary = Color(0xFF3A332B),
        primaryContainer = Color(0xFF6B5D52),
        onPrimaryContainer = Color(0xFFF5F2EE),
        
        secondary = Color(0xFFE0D7D0),
        onSecondary = Color(0xFF3A332B),
        secondaryContainer = Color(0xFF6B5D52),
        onSecondaryContainer = Color(0xFFF5F2EE),
        
        tertiary = Color(0xFFE6B584),
        onTertiary = Color(0xFF3A332B),
        tertiaryContainer = Color(0xFFA0622B),
        onTertiaryContainer = Color(0xFFF5E6D3),
        
        background = Color(0xFF1A1612),
        onBackground = Color(0xFFF5F2EE),
        surface = Color(0xFF1A1612),
        onSurface = Color(0xFFF5F2EE),
        surfaceVariant = Color(0xFF3A332B),
        onSurfaceVariant = Color(0xFFE0D7D0)
    )
}