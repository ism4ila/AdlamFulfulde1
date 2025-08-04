package com.bekisma.adlamfulfulde.ads

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

/**
 * Helper pour gérer l'affichage intelligent des publicités interstitielles
 * selon les meilleures pratiques UX et les patterns de navigation
 */
object InterstitialAdHelper {
    
    private const val TAG = "InterstitialAdHelper"
    private const val PREFS_NAME = "interstitial_prefs"
    private const val KEY_LAST_SHOWN = "last_shown_timestamp"
    private const val KEY_ACTION_COUNT = "action_count"
    private const val KEY_SESSION_START = "session_start"
    
    // Configuration des fréquences
    private const val MIN_INTERVAL_MS = 3 * 60 * 1000L // 3 minutes minimum
    private const val ACTIONS_BETWEEN_ADS = 3 // Tous les 3 actions significatives
    private const val SESSION_THRESHOLD_MS = 30 * 1000L // 30s de session minimum
    
    /**
     * Points de transition où les interstitielles sont appropriées
     */
    enum class TransitionPoint(val actionWeight: Int) {
        QUIZ_COMPLETED(2),           // Fin de quiz complet
        MODULE_TRANSITION(2),        // Alphabet → Chiffres → Quiz
        WRITING_SESSION_END(1),      // Fin de session d'écriture
        VOCABULARY_SESSION_END(1),   // Fin de session vocabulaire  
        TOOL_EXIT(1),               // Sortie des outils (clavier, transcription)
        LEVEL_CHANGE(1),            // Changement de niveau de difficulté
        SETTINGS_EXIT(1)            // Sortie des paramètres
    }
    
    /**
     * Détermine si on peut afficher une interstitielle à ce point de transition
     */
    fun canShowInterstitial(
        context: Context,
        transitionPoint: TransitionPoint
    ): Boolean {
        val prefs = getPrefs(context)
        
        // Vérifier l'intervalle minimum
        val lastShown = prefs.getLong(KEY_LAST_SHOWN, 0)
        val now = System.currentTimeMillis()
        if (now - lastShown < MIN_INTERVAL_MS) {
            Log.d(TAG, "Too soon since last interstitial (${(now - lastShown) / 1000}s)")
            return false
        }
        
        // Vérifier la durée de session
        val sessionStart = prefs.getLong(KEY_SESSION_START, now)
        if (now - sessionStart < SESSION_THRESHOLD_MS) {
            Log.d(TAG, "Session too short (${(now - sessionStart) / 1000}s)")
            return false
        }
        
        // Vérifier le compteur d'actions
        val actionCount = prefs.getInt(KEY_ACTION_COUNT, 0)
        val newActionCount = actionCount + transitionPoint.actionWeight
        
        Log.d(TAG, "Action count: $newActionCount (threshold: $ACTIONS_BETWEEN_ADS)")
        
        return newActionCount >= ACTIONS_BETWEEN_ADS
    }
    
    /**
     * Affiche une interstitielle si les conditions sont remplies
     */
    fun showInterstitialIfAppropriate(
        activity: Activity,
        transitionPoint: TransitionPoint,
        onAdShown: () -> Unit = {},
        onAdDismissed: () -> Unit = {},
        onContinue: () -> Unit
    ) {
        val context = activity as Context
        
        if (!canShowInterstitial(context, transitionPoint)) {
            Log.d(TAG, "Interstitial not appropriate for $transitionPoint")
            // Incrémenter le compteur même si on ne montre pas de pub
            incrementActionCount(context, transitionPoint.actionWeight)
            onContinue()
            return
        }
        
        // Précharger la publicité si elle n'est pas prête
        if (!InterstitialAdManager.isAdReady()) {
            Log.d(TAG, "Loading interstitial ad for $transitionPoint")
            InterstitialAdManager.loadAd(
                context = context,
                onAdLoaded = {
                    showInterstitialNow(activity, transitionPoint, onAdShown, onAdDismissed, onContinue)
                },
                onAdFailedToLoad = { error ->
                    Log.w(TAG, "Failed to load interstitial: ${error.message}")
                    onContinue()
                }
            )
        } else {
            showInterstitialNow(activity, transitionPoint, onAdShown, onAdDismissed, onContinue)
        }
    }
    
    private fun showInterstitialNow(
        activity: Activity,
        transitionPoint: TransitionPoint,
        onAdShown: () -> Unit,
        onAdDismissed: () -> Unit,
        onContinue: () -> Unit
    ) {
        Log.d(TAG, "Showing interstitial for $transitionPoint")
        
        InterstitialAdManager.showAd(
            activity = activity,
            onAdDismissed = {
                markInterstitialShown(activity)
                onAdDismissed()
                onContinue()
            },
            onAdFailedToShow = { error ->
                Log.w(TAG, "Failed to show interstitial: ${error.message}")
                onContinue()
            },
            onAdShowed = {
                onAdShown()
            }
        )
    }
    
    /**
     * Marque qu'une interstitielle a été affichée
     */
    private fun markInterstitialShown(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putLong(KEY_LAST_SHOWN, System.currentTimeMillis())
            .putInt(KEY_ACTION_COUNT, 0) // Reset counter
            .apply()
        
        Log.d(TAG, "Interstitial shown, counters reset")
    }
    
    /**
     * Incrémente le compteur d'actions
     */
    private fun incrementActionCount(context: Context, weight: Int) {
        val prefs = getPrefs(context)
        val currentCount = prefs.getInt(KEY_ACTION_COUNT, 0)
        prefs.edit()
            .putInt(KEY_ACTION_COUNT, currentCount + weight)
            .apply()
        
        Log.d(TAG, "Action count incremented by $weight, new total: ${currentCount + weight}")
    }
    
    /**
     * Initialise une nouvelle session
     */
    fun startSession(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putLong(KEY_SESSION_START, System.currentTimeMillis())
            .apply()
        
        Log.d(TAG, "New session started")
    }
    
    /**
     * Réinitialise tous les compteurs (pour tests ou reset utilisateur)
     */
    fun resetCounters(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putLong(KEY_LAST_SHOWN, 0)
            .putInt(KEY_ACTION_COUNT, 0)
            .putLong(KEY_SESSION_START, System.currentTimeMillis())
            .apply()
        
        Log.d(TAG, "All counters reset")
    }
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}

/**
 * Composable helper pour gérer les interstitielles dans l'UI
 */
@Composable
fun InterstitialAdTrigger(
    transitionPoint: InterstitialAdHelper.TransitionPoint,
    shouldTrigger: Boolean,
    onAdShown: () -> Unit = {},
    onAdDismissed: () -> Unit = {},
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    
    LaunchedEffect(shouldTrigger) {
        if (shouldTrigger && context is Activity) {
            // Petit délai pour permettre à l'UI de se stabiliser
            delay(500)
            
            InterstitialAdHelper.showInterstitialIfAppropriate(
                activity = context,
                transitionPoint = transitionPoint,
                onAdShown = onAdShown,
                onAdDismissed = onAdDismissed,
                onContinue = onContinue
            )
        }
    }
}