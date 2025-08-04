package com.bekisma.adlamfulfulde.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.data.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@LooperMode(LooperMode.Mode.PAUSED)
class MainScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainScreenViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainScreenViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial ui state should have correct default values`() = runTest {
        val initialState = viewModel.uiState.first()
        
        assert(!initialState.isLoading)
        assert(initialState.selectedModule == null)
        assert(initialState.learningProgress == 0.35f)
        assert(!initialState.isTablet)
        assert(!initialState.isDarkTheme)
        assert(initialState.errorMessage == null)
    }

    @Test
    fun `updateDeviceType should update isTablet flag`() = runTest {
        viewModel.updateDeviceType(true)
        
        val state = viewModel.uiState.first()
        assert(state.isTablet)
        
        viewModel.updateDeviceType(false)
        
        val updatedState = viewModel.uiState.first()
        assert(!updatedState.isTablet)
    }

    @Test
    fun `updateTheme should update isDarkTheme flag`() = runTest {
        viewModel.updateTheme(true)
        
        val state = viewModel.uiState.first()
        assert(state.isDarkTheme)
        
        viewModel.updateTheme(false)
        
        val updatedState = viewModel.uiState.first()
        assert(!updatedState.isDarkTheme)
    }

    @Test
    fun `selectModule should update selectedModule`() = runTest {
        val testModule = MenuItem(
            R.drawable.abc64,
            R.string.alphabet_learning,
            R.string.discover_the_adlam_alphabet,
            "alphabet"
        )
        
        viewModel.selectModule(testModule)
        
        val state = viewModel.uiState.first()
        assert(state.selectedModule == testModule)
        assert(state.selectedModule?.destination == "alphabet")
    }

    @Test
    fun `updateLearningProgress should update progress within valid range`() = runTest {
        // Test normal progress
        viewModel.updateLearningProgress(0.5f)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state1 = viewModel.uiState.first()
        assert(state1.learningProgress == 0.5f)
        
        // Test progress above 1.0 should be clamped to 1.0
        viewModel.updateLearningProgress(1.5f)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state2 = viewModel.uiState.first()
        assert(state2.learningProgress == 1.0f)
        
        // Test progress below 0.0 should be clamped to 0.0
        viewModel.updateLearningProgress(-0.5f)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state3 = viewModel.uiState.first()
        assert(state3.learningProgress == 0.0f)
    }

    @Test
    fun `setError should update error message`() = runTest {
        val errorMessage = "Test error message"
        
        viewModel.setError(errorMessage)
        
        val state = viewModel.uiState.first()
        assert(state.errorMessage == errorMessage)
    }

    @Test
    fun `clearError should remove error message`() = runTest {
        // Set error first
        viewModel.setError("Test error")
        
        val stateWithError = viewModel.uiState.first()
        assert(stateWithError.errorMessage == "Test error")
        
        // Clear error
        viewModel.clearError()
        
        val stateWithoutError = viewModel.uiState.first()
        assert(stateWithoutError.errorMessage == null)
    }

    @Test
    fun `setLoading should update loading state`() = runTest {
        viewModel.setLoading(true)
        
        val loadingState = viewModel.uiState.first()
        assert(loadingState.isLoading)
        
        viewModel.setLoading(false)
        
        val notLoadingState = viewModel.uiState.first()
        assert(!notLoadingState.isLoading)
    }

    @Test
    fun `multiple state updates should preserve other properties`() = runTest {
        val testModule = MenuItem(
            R.drawable.abc64,
            R.string.alphabet_learning,
            R.string.discover_the_adlam_alphabet,
            "alphabet"
        )
        
        // Update multiple properties
        viewModel.updateDeviceType(true)
        viewModel.updateTheme(true)
        viewModel.selectModule(testModule)
        viewModel.setLoading(true)
        viewModel.setError("Test error")
        viewModel.updateLearningProgress(0.75f)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val finalState = viewModel.uiState.first()
        
        // All properties should be updated correctly
        assert(finalState.isTablet)
        assert(finalState.isDarkTheme)
        assert(finalState.selectedModule == testModule)
        assert(finalState.isLoading)
        assert(finalState.errorMessage == "Test error")
        assert(finalState.learningProgress == 0.75f)
    }

    @Test
    fun `state updates should be immutable`() = runTest {
        val initialState = viewModel.uiState.first()
        
        viewModel.updateDeviceType(true)
        
        val updatedState = viewModel.uiState.first()
        
        // States should be different objects
        assert(initialState != updatedState)
        assert(initialState.isTablet != updatedState.isTablet)
    }

    @Test
    fun `concurrent state updates should be handled correctly`() = runTest {
        // Perform multiple updates rapidly
        viewModel.updateDeviceType(true)
        viewModel.updateTheme(true)
        viewModel.setLoading(true)
        
        val state = viewModel.uiState.first()
        
        // All updates should be applied
        assert(state.isTablet)
        assert(state.isDarkTheme)
        assert(state.isLoading)
    }

    @Test
    fun `learning progress edge cases should be handled correctly`() = runTest {
        // Test exactly 0.0
        viewModel.updateLearningProgress(0.0f)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state1 = viewModel.uiState.first()
        assert(state1.learningProgress == 0.0f)
        
        // Test exactly 1.0
        viewModel.updateLearningProgress(1.0f)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state2 = viewModel.uiState.first()
        assert(state2.learningProgress == 1.0f)
        
        // Test very small positive number
        viewModel.updateLearningProgress(0.001f)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state3 = viewModel.uiState.first()
        assert(state3.learningProgress == 0.001f)
        
        // Test very close to 1.0
        viewModel.updateLearningProgress(0.999f)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state4 = viewModel.uiState.first()
        assert(state4.learningProgress == 0.999f)
    }

    @Test
    fun `error handling should work with empty strings`() = runTest {
        viewModel.setError("")
        
        val state = viewModel.uiState.first()
        assert(state.errorMessage == "")
        
        viewModel.clearError()
        
        val clearedState = viewModel.uiState.first()
        assert(clearedState.errorMessage == null)
    }
}