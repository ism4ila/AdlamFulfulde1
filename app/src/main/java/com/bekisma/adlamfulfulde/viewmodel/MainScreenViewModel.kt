package com.bekisma.adlamfulfulde.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.bekisma.adlamfulfulde.data.MenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Stable
data class MainScreenUiState(
    val isLoading: Boolean = false,
    val selectedModule: MenuItem? = null,
    val learningProgress: Float = 0.35f,
    val isTablet: Boolean = false,
    val isDarkTheme: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class MainScreenViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()
    
    fun updateDeviceType(isTablet: Boolean) {
        _uiState.value = _uiState.value.copy(isTablet = isTablet)
    }
    
    fun updateTheme(isDarkTheme: Boolean) {
        _uiState.value = _uiState.value.copy(isDarkTheme = isDarkTheme)
    }
    
    fun selectModule(module: MenuItem) {
        _uiState.value = _uiState.value.copy(selectedModule = module)
    }
    
    fun updateLearningProgress(progress: Float) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(learningProgress = progress.coerceIn(0f, 1f))
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }
    
    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }
}