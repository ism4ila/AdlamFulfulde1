package com.bekisma.adlamfulfulde

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppLaunchesSuccessfully() {
        // Verify that the app launches without crashing
        composeTestRule.waitForIdle()
        
        // Check if the main screen is displayed
        composeTestRule.onNodeWithText("Adlam").assertIsDisplayed()
    }

    @Test
    fun testMainScreenElementsAreVisible() {
        composeTestRule.waitForIdle()
        
        // Check for Welcome Card
        composeTestRule.onNodeWithText("Start Your Adlam Journey").assertIsDisplayed()
        
        // Check for Get Started button
        composeTestRule.onNodeWithText("Get Started").assertIsDisplayed()
        
        // Check for progress card
        composeTestRule.onNodeWithText("Your Progress").assertIsDisplayed()
    }

    @Test
    fun testNavigationToAlphabetModule() {
        composeTestRule.waitForIdle()
        
        // Click on Get Started button
        composeTestRule.onNodeWithText("Get Started").performClick()
        
        // Wait for navigation
        composeTestRule.waitForIdle()
        
        // Should navigate to alphabet progress screen
        composeTestRule.onNodeWithText("Alphabet Quiz").assertIsDisplayed()
    }

    @Test
    fun testMenuDrawerOpens() {
        composeTestRule.waitForIdle()
        
        // Click on menu button
        composeTestRule.onNodeWithContentDescription("Menu Icon").performClick()
        
        // Wait for drawer to open
        composeTestRule.waitForIdle()
        
        // Check if drawer content is visible
        composeTestRule.onNodeWithText("Adlam Fulfulde").assertIsDisplayed()
    }

    @Test
    fun testAlphabetModuleNavigation() {
        composeTestRule.waitForIdle()
        
        // Click on alphabet module (either from cards or drawer)
        composeTestRule.onNodeWithText("Alphabet").performClick()
        
        composeTestRule.waitForIdle()
        
        // Should show alphabet progress screen
        composeTestRule.onNodeWithText("Alphabet Quiz").assertIsDisplayed()
    }

    @Test
    fun testNumbersModuleNavigation() {
        composeTestRule.waitForIdle()
        
        // Open drawer
        composeTestRule.onNodeWithContentDescription("Menu Icon").performClick()
        composeTestRule.waitForIdle()
        
        // Click on Numbers
        composeTestRule.onNodeWithText("Numbers").performClick()
        composeTestRule.waitForIdle()
        
        // Should navigate to numbers screen
        composeTestRule.onNodeWithText("Numbers in Adlam").assertIsDisplayed()
    }

    @Test
    fun testWritingModuleNavigation() {
        composeTestRule.waitForIdle()
        
        // Open drawer
        composeTestRule.onNodeWithContentDescription("Menu Icon").performClick()
        composeTestRule.waitForIdle()
        
        // Click on Writing
        composeTestRule.onNodeWithText("Learn to write").performClick()
        composeTestRule.waitForIdle()
        
        // Should navigate to writing screen
        composeTestRule.onNodeWithText("THE ALPHABET").assertIsDisplayed()
    }

    @Test
    fun testQuizModuleNavigation() {
        composeTestRule.waitForIdle()
        
        // Open drawer
        composeTestRule.onNodeWithContentDescription("Menu Icon").performClick()
        composeTestRule.waitForIdle()
        
        // Click on Quiz
        composeTestRule.onNodeWithText("Quiz").performClick()
        composeTestRule.waitForIdle()
        
        // Should navigate to quiz screen
        composeTestRule.onNodeWithText("Quiz Instructions").assertIsDisplayed()
    }


    @Test
    fun testSettingsNavigation() {
        composeTestRule.waitForIdle()
        
        // Open drawer
        composeTestRule.onNodeWithContentDescription("Menu Icon").performClick()
        composeTestRule.waitForIdle()
        
        // Click on Settings
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()
        
        // Should navigate to settings screen
        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
    }

    @Test
    fun testAboutNavigation() {
        composeTestRule.waitForIdle()
        
        // Open drawer
        composeTestRule.onNodeWithContentDescription("Menu Icon").performClick()
        composeTestRule.waitForIdle()
        
        // Click on About
        composeTestRule.onNodeWithText("About").performClick()
        composeTestRule.waitForIdle()
        
        // Should navigate to about screen
        composeTestRule.onNodeWithText("Contributors").assertIsDisplayed()
    }

    @Test
    fun testBackNavigationWorks() {
        composeTestRule.waitForIdle()
        
        // Navigate to alphabet module
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        
        // Should be on alphabet progress screen
        composeTestRule.onNodeWithText("Alphabet Quiz").assertIsDisplayed()
        
        // Click back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
        
        // Should be back on main screen
        composeTestRule.onNodeWithText("Start Your Adlam Journey").assertIsDisplayed()
    }

    @Test
    fun testAppHandlesConfigurationChanges() {
        composeTestRule.waitForIdle()
        
        // Verify app is running
        composeTestRule.onNodeWithText("Adlam").assertIsDisplayed()
        
        // Rotate device (simulated)
        composeTestRule.activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        composeTestRule.waitForIdle()
        
        // App should still be running
        composeTestRule.onNodeWithText("Adlam").assertIsDisplayed()
        
        // Rotate back
        composeTestRule.activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        composeTestRule.waitForIdle()
        
        // App should still be running
        composeTestRule.onNodeWithText("Adlam").assertIsDisplayed()
    }

    @Test
    fun testContextAvailability() {
        // Test that context is available
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull(context)
        assertEquals("com.bekisma.adlamfulfulde", context.packageName)
    }

    @Test
    fun testThemeChanges() {
        composeTestRule.waitForIdle()
        
        // Navigate to settings
        composeTestRule.onNodeWithContentDescription("Menu Icon").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()
        
        // Test theme change functionality exists
        composeTestRule.onNodeWithText("Theme Selection").assertIsDisplayed()
    }
}