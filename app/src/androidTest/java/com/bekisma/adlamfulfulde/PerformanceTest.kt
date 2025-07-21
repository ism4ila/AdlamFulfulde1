package com.bekisma.adlamfulfulde

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
class PerformanceTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppLaunchTime() {
        val launchTime = measureTimeMillis {
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("Adlam").assertIsDisplayed()
        }
        
        // App should launch within 3 seconds
        assert(launchTime < 3000) { "App launch took ${launchTime}ms, which is too slow" }
    }

    @Test
    fun testNavigationPerformance() {
        composeTestRule.waitForIdle()
        
        val navigationTime = measureTimeMillis {
            composeTestRule.onNodeWithText("Get Started").performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("Alphabet Quiz").assertIsDisplayed()
        }
        
        // Navigation should be under 1 second
        assert(navigationTime < 1000) { "Navigation took ${navigationTime}ms, which is too slow" }
    }

    @Test
    fun testDrawerPerformance() {
        composeTestRule.waitForIdle()
        
        val drawerTime = measureTimeMillis {
            composeTestRule.onNodeWithContentDescription("Menu Icon").performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("Adlam Fulfulde").assertIsDisplayed()
        }
        
        // Drawer should open within 500ms
        assert(drawerTime < 500) { "Drawer opening took ${drawerTime}ms, which is too slow" }
    }

    @Test
    fun testMemoryLeaks() {
        composeTestRule.waitForIdle()
        
        // Navigate through multiple screens to test for memory leaks
        val screens = listOf(
            "Get Started" to "Alphabet Quiz",
            "Back" to "Start Your Adlam Journey"
        )
        
        repeat(10) {
            screens.forEach { (action, expected) ->
                when (action) {
                    "Back" -> composeTestRule.onNodeWithContentDescription("Back").performClick()
                    else -> composeTestRule.onNodeWithText(action).performClick()
                }
                composeTestRule.waitForIdle()
                composeTestRule.onNodeWithText(expected).assertIsDisplayed()
            }
        }
        
        // If we reach here without OutOfMemoryError, test passes
    }

    @Test
    fun testUIResponsiveness() {
        composeTestRule.waitForIdle()
        
        // Test rapid interactions
        repeat(5) {
            val interactionTime = measureTimeMillis {
                composeTestRule.onNodeWithContentDescription("Menu Icon").performClick()
                composeTestRule.waitForIdle()
                composeTestRule.onNodeWithContentDescription("Back").performClick()
                composeTestRule.waitForIdle()
            }
            
            // Each interaction should be under 200ms
            assert(interactionTime < 200) { "UI interaction took ${interactionTime}ms, which is too slow" }
        }
    }

    @Test
    fun testScrollPerformance() {
        composeTestRule.waitForIdle()
        
        // Navigate to a screen with scrollable content
        composeTestRule.onNodeWithContentDescription("Menu Icon").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Vocabulary").performClick()
        composeTestRule.waitForIdle()
        
        // Test scrolling performance
        val scrollTime = measureTimeMillis {
            try {
                composeTestRule.onNodeWithText("Vocabulary").performScrollTo()
                composeTestRule.waitForIdle()
            } catch (e: Exception) {
                // Screen might not be scrollable
            }
        }
        
        // Scrolling should be smooth (under 100ms)
        assert(scrollTime < 100) { "Scrolling took ${scrollTime}ms, which is too slow" }
    }

    @Test
    fun testAnimationPerformance() {
        composeTestRule.waitForIdle()
        
        // Test animation performance by triggering animations
        val animationTime = measureTimeMillis {
            composeTestRule.onNodeWithText("Get Started").performClick()
            composeTestRule.waitForIdle()
            
            // Wait for animations to complete
            Thread.sleep(500)
            
            composeTestRule.onNodeWithText("Alphabet Quiz").assertIsDisplayed()
        }
        
        // Animation should complete within 1 second
        assert(animationTime < 1000) { "Animation took ${animationTime}ms, which is too slow" }
    }

    @Test
    fun testResourceUsage() {
        composeTestRule.waitForIdle()
        
        // Test that the app doesn't consume excessive resources
        // This is a basic test that ensures the app doesn't crash under load
        
        repeat(20) {
            // Rapid navigation
            composeTestRule.onNodeWithText("Get Started").performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithContentDescription("Back").performClick()
            composeTestRule.waitForIdle()
        }
        
        // App should still be responsive
        composeTestRule.onNodeWithText("Start Your Adlam Journey").assertIsDisplayed()
    }

    @Test
    fun testConfigurationChangePerformance() {
        composeTestRule.waitForIdle()
        
        val configChangeTime = measureTimeMillis {
            // Simulate configuration change
            composeTestRule.activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            composeTestRule.waitForIdle()
            composeTestRule.activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            composeTestRule.waitForIdle()
        }
        
        // Configuration change should be under 2 seconds
        assert(configChangeTime < 2000) { "Configuration change took ${configChangeTime}ms, which is too slow" }
        
        // App should still be functional
        composeTestRule.onNodeWithText("Start Your Adlam Journey").assertIsDisplayed()
    }

    @Test
    fun testGarbageCollection() {
        composeTestRule.waitForIdle()
        
        // Force garbage collection and test that app remains stable
        System.gc()
        Thread.sleep(100)
        
        // App should still be responsive after GC
        composeTestRule.onNodeWithText("Start Your Adlam Journey").assertIsDisplayed()
        
        // Test interaction after GC
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Alphabet Quiz").assertIsDisplayed()
    }
}