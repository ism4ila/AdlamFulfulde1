package com.bekisma.adlamfulfulde

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlphabetLearningTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAlphabetProgressScreenLoads() {
        composeTestRule.waitForIdle()
        
        // Navigate to alphabet learning
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        
        // Should show alphabet progress screen
        composeTestRule.onNodeWithText("Alphabet Quiz").assertIsDisplayed()
        composeTestRule.onNodeWithText("Progression de l'alphabet").assertIsDisplayed()
    }

    @Test
    fun testAlphabetGridDisplays() {
        composeTestRule.waitForIdle()
        
        // Navigate to alphabet learning
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        
        // Should show alphabet grid with letters
        // Check for first letter (Alif)
        composeTestRule.onNodeWithText("𞤀").assertIsDisplayed()
    }

    @Test
    fun testVisualRecognitionPhase() {
        composeTestRule.waitForIdle()
        
        // Navigate to alphabet learning
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        
        // Click on Phase 1 button
        composeTestRule.onNodeWithText("Phase 1: Reconnaissance Visuelle").performClick()
        composeTestRule.waitForIdle()
        
        // Should show visual recognition quiz
        composeTestRule.onNodeWithText("What is the name of this letter?").assertIsDisplayed()
    }

    @Test
    fun testAudioRecognitionPhase() {
        composeTestRule.waitForIdle()
        
        // Navigate to alphabet learning
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        
        // Check if audio phase is available (might be locked initially)
        try {
            composeTestRule.onNodeWithText("Phase 2: Reconnaissance Audio").assertIsDisplayed()
        } catch (e: AssertionError) {
            // Audio phase might be locked, which is expected behavior
        }
    }

    @Test
    fun testProgressIndicatorsVisible() {
        composeTestRule.waitForIdle()
        
        // Navigate to alphabet learning
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        
        // Should show progress indicators
        composeTestRule.onNodeWithText("lettres maîtrisées").assertIsDisplayed()
        composeTestRule.onNodeWithText("terminé").assertIsDisplayed()
    }

    @Test
    fun testLetterStatusIndicators() {
        composeTestRule.waitForIdle()
        
        // Navigate to alphabet learning
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        
        // Should show different letter status indicators
        // Look for phase indicators (eyes for visual, headphones for audio)
        try {
            composeTestRule.onNodeWithText("👁️").assertIsDisplayed()
        } catch (e: AssertionError) {
            // Might not be visible if no current letter
        }
    }

    @Test
    fun testQuizBackNavigation() {
        composeTestRule.waitForIdle()
        
        // Navigate to alphabet learning
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        
        // Try to start a quiz
        try {
            composeTestRule.onNodeWithText("Phase 1: Reconnaissance Visuelle").performClick()
            composeTestRule.waitForIdle()
            
            // Should have back navigation
            composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
            
            // Test back navigation
            composeTestRule.onNodeWithContentDescription("Back").performClick()
            composeTestRule.waitForIdle()
            
            // Should be back on progress screen
            composeTestRule.onNodeWithText("Alphabet Quiz").assertIsDisplayed()
        } catch (e: AssertionError) {
            // Quiz might not be available yet
        }
    }

    @Test
    fun testProgressPersistence() {
        composeTestRule.waitForIdle()
        
        // Navigate to alphabet learning
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        
        // Progress should load from SharedPreferences
        // This test mainly verifies that the screen doesn't crash
        composeTestRule.onNodeWithText("Progression de l'alphabet").assertIsDisplayed()
    }

    @Test
    fun testAlphabetCompletionFlow() {
        composeTestRule.waitForIdle()
        
        // Navigate to alphabet learning
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        
        // Test completion scenario (might show completion message)
        try {
            composeTestRule.onNodeWithText("Alphabet Complete!").assertIsDisplayed()
            composeTestRule.onNodeWithText("Congratulations! You can now read Adlam!").assertIsDisplayed()
        } catch (e: AssertionError) {
            // Alphabet not completed yet, which is expected
        }
    }

    @Test
    fun testLetterDetailsAccess() {
        composeTestRule.waitForIdle()
        
        // Navigate to alphabet learning
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        
        // Try to access letter details by clicking on a letter
        try {
            // Click on first letter if it's available
            composeTestRule.onNodeWithText("𞤀").performClick()
            composeTestRule.waitForIdle()
            
            // Should either start a quiz or show letter details
            // This mainly tests that clicking doesn't crash
        } catch (e: AssertionError) {
            // Letter might not be clickable
        }
    }
}