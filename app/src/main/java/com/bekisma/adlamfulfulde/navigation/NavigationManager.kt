package com.bekisma.adlamfulfulde.navigation

import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.data.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class NavigationState(
    val currentDestination: String? = null,
    val isNavigating: Boolean = false,
    val navigationError: String? = null
)

class NavigationManager @Inject constructor() {
    
    private var navController: NavController? = null
    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()
    
    fun setNavController(controller: NavController) {
        navController = controller
        _navigationState.value = _navigationState.value.copy(navigationError = null)
    }
    
    fun navigateToModule(
        item: MenuItem, 
        showInterstitial: Boolean = false,
        onNavigationComplete: () -> Unit = {},
        onNavigationError: (String) -> Unit = {}
    ) {
        try {
            _navigationState.value = _navigationState.value.copy(isNavigating = true, navigationError = null)
            
            navController?.let { controller ->
                if (showInterstitial && shouldShowInterstitialForDestination(item.destination)) {
                    navigateWithAd(controller, item.destination, onNavigationComplete, onNavigationError)
                } else {
                    safeNavigate(controller, item.destination, onNavigationComplete, onNavigationError)
                }
            } ?: run {
                val error = "Navigation controller not initialized"
                _navigationState.value = _navigationState.value.copy(
                    isNavigating = false, 
                    navigationError = error
                )
                onNavigationError(error)
            }
        } catch (e: Exception) {
            val error = "Navigation failed: ${e.message}"
            _navigationState.value = _navigationState.value.copy(
                isNavigating = false, 
                navigationError = error
            )
            onNavigationError(error)
        }
    }
    
    fun navigateBack(
        onNavigationComplete: () -> Unit = {},
        onNavigationError: (String) -> Unit = {}
    ) {
        try {
            _navigationState.value = _navigationState.value.copy(isNavigating = true, navigationError = null)
            
            val success = navController?.popBackStack() ?: false
            if (success) {
                _navigationState.value = _navigationState.value.copy(isNavigating = false)
                onNavigationComplete()
            } else {
                val error = "Cannot navigate back"
                _navigationState.value = _navigationState.value.copy(
                    isNavigating = false, 
                    navigationError = error
                )
                onNavigationError(error)
            }
        } catch (e: Exception) {
            val error = "Back navigation failed: ${e.message}"
            _navigationState.value = _navigationState.value.copy(
                isNavigating = false, 
                navigationError = error
            )
            onNavigationError(error)
        }
    }
    
    fun navigateTo(
        destination: String,
        onNavigationComplete: () -> Unit = {},
        onNavigationError: (String) -> Unit = {}
    ) {
        try {
            _navigationState.value = _navigationState.value.copy(isNavigating = true, navigationError = null)
            navController?.let { controller ->
                safeNavigate(controller, destination, onNavigationComplete, onNavigationError)
            } ?: run {
                val error = "Navigation controller not initialized"
                _navigationState.value = _navigationState.value.copy(
                    isNavigating = false, 
                    navigationError = error
                )
                onNavigationError(error)
            }
        } catch (e: Exception) {
            val error = "Navigation failed: ${e.message}"
            _navigationState.value = _navigationState.value.copy(
                isNavigating = false, 
                navigationError = error
            )
            onNavigationError(error)
        }
    }
    
    fun clearNavigationError() {
        _navigationState.value = _navigationState.value.copy(navigationError = null)
    }
    
    private fun shouldShowInterstitialForDestination(destination: String): Boolean {
        return when (destination) {
            "quiz", "vocabulary", "writing", "flashcards" -> true
            else -> false
        }
    }
    
    private fun safeNavigate(
        controller: NavController,
        destination: String,
        onNavigationComplete: () -> Unit,
        onNavigationError: (String) -> Unit
    ) {
        try {
            controller.navigate(destination) {
                launchSingleTop = true
                restoreState = true
            }
            _navigationState.value = _navigationState.value.copy(
                isNavigating = false,
                currentDestination = destination
            )
            onNavigationComplete()
        } catch (e: Exception) {
            val error = "Failed to navigate to $destination: ${e.message}"
            _navigationState.value = _navigationState.value.copy(
                isNavigating = false,
                navigationError = error
            )
            onNavigationError(error)
        }
    }
    
    private fun navigateWithAd(
        controller: NavController,
        destination: String,
        onNavigationComplete: () -> Unit,
        onNavigationError: (String) -> Unit
    ) {
        try {
            safeNavigate(controller, destination, onNavigationComplete, onNavigationError)
        } catch (e: Exception) {
            val error = "Ad navigation failed: ${e.message}"
            _navigationState.value = _navigationState.value.copy(
                isNavigating = false,
                navigationError = error
            )
            onNavigationError(error)
        }
    }
}