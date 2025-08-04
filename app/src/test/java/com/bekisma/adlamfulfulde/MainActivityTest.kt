package com.bekisma.adlamfulfulde

import com.bekisma.adlamfulfulde.data.MenuItem
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@LooperMode(LooperMode.Mode.PAUSED)
class MainActivityTest {

    @Before
    fun setUp() {
        // Setup test environment
    }

    @Test
    fun `shouldShowInterstitialBeforeNavigation should return true for quiz`() {
        // Create a temporary MainActivity instance for testing
        val activity = MainActivity()
        
        // Use reflection to access private method for testing
        val method = activity.javaClass.getDeclaredMethod(
            "shouldShowInterstitialBeforeNavigation", 
            String::class.java
        )
        method.isAccessible = true
        
        val result = method.invoke(activity, "quiz") as Boolean
        assert(result)
    }

    @Test
    fun `shouldShowInterstitialBeforeNavigation should return false for other destinations`() {
        // Create a temporary MainActivity instance for testing
        val activity = MainActivity()
        
        // Use reflection to access private method for testing
        val method = activity.javaClass.getDeclaredMethod(
            "shouldShowInterstitialBeforeNavigation", 
            String::class.java
        )
        method.isAccessible = true
        
        val destinations = listOf("alphabet", "numbers", "writing", "practice")
        destinations.forEach { destination ->
            val result = method.invoke(activity, destination) as Boolean
            assert(!result) { "Should return false for destination: $destination" }
        }
    }

    @Test
    fun `MenuItem should have correct properties`() {
        val testItem = MenuItem(
            R.drawable.abc64,
            R.string.alphabet_learning,
            R.string.discover_the_adlam_alphabet,
            "alphabet"
        )
        
        assert(testItem.imageRes == R.drawable.abc64)
        assert(testItem.titleRes == R.string.alphabet_learning)
        assert(testItem.subtitleRes == R.string.discover_the_adlam_alphabet)
        assert(testItem.destination == "alphabet")
    }
}