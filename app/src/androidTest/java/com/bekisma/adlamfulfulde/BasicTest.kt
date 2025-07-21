package com.bekisma.adlamfulfulde

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BasicTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun testAppDoesNotCrash() {
        // Wait for the app to load
        composeTestRule.waitForIdle()
        
        // Print the entire UI tree to see what's displayed
        composeTestRule.onRoot().printToLog("UI_TREE")
    }
}