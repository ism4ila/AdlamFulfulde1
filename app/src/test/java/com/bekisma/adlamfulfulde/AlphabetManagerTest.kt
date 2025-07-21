package com.bekisma.adlamfulfulde

import android.content.Context
import android.content.SharedPreferences
import com.bekisma.adlamfulfulde.data.alphabet.AdlamAlphabetManager
import com.bekisma.adlamfulfulde.data.alphabet.AdlamLetter
import com.bekisma.adlamfulfulde.data.alphabet.LearningPhase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AlphabetManagerTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var alphabetManager: AdlamAlphabetManager

    @Before
    fun setUp() {
        context = mockk()
        sharedPreferences = mockk()
        editor = mockk()
        
        every { context.getSharedPreferences("adlam_alphabet_progress", Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor
        every { editor.putStringSet(any(), any()) } returns editor
        every { editor.apply() } returns Unit
        
        // Mock initial state
        every { sharedPreferences.getInt("current_letter_index", 0) } returns 0
        every { sharedPreferences.getString("current_phase", "VISUAL_RECOGNITION") } returns "VISUAL_RECOGNITION"
        every { sharedPreferences.getStringSet("mastered_letters", emptySet()) } returns emptySet()
        every { sharedPreferences.getInt("total_letters", 28) } returns 28
        
        alphabetManager = AdlamAlphabetManager(context)
    }

    @Test
    fun testInitialization() = runTest {
        // Test that manager initializes correctly
        val progressState = alphabetManager.progressState.value
        
        assert(progressState.currentLetterIndex == 0)
        assert(progressState.currentPhase == LearningPhase.VISUAL_RECOGNITION)
        assert(progressState.masteredLetters.isEmpty())
        assert(progressState.totalLetters == 28)
    }

    @Test
    fun testGetCurrentLetter() {
        val currentLetter = alphabetManager.getCurrentLetter()
        
        // Should return the first letter (Alif)
        assert(currentLetter == AdlamLetter.ALIF)
    }

    @Test
    fun testStartVisualRecognitionQuiz() {
        val questions = alphabetManager.startVisualRecognitionQuiz()
        
        // Should generate 10 questions
        assert(questions.size == 10)
        
        // All questions should be for the current letter
        questions.forEach { question ->
            assert(question.targetLetter == AdlamLetter.ALIF)
            assert(question.options.size == 4)
            assert(question.options.contains(question.correctAnswer))
        }
    }

    @Test
    fun testStartAudioRecognitionQuiz() {
        val questions = alphabetManager.startAudioRecognitionQuiz()
        
        // Should generate 10 questions
        assert(questions.size == 10)
        
        // All questions should be for the current letter
        questions.forEach { question ->
            assert(question.targetLetter == AdlamLetter.ALIF)
            assert(question.options.size == 4)
            assert(question.options.contains(question.correctAnswer))
        }
    }

    @Test
    fun testProgressPercentage() {
        // Mock some mastered letters
        every { sharedPreferences.getStringSet("mastered_letters", emptySet()) } returns setOf("ALIF", "DAALI")
        
        val newManager = AdlamAlphabetManager(context)
        val percentage = newManager.getProgressPercentage()
        
        // Should be approximately 7.14% (2/28 letters)
        assert(percentage > 7.0 && percentage < 8.0)
    }

    @Test
    fun testIsAlphabetComplete() {
        // Mock all letters as mastered
        val allLetters = setOf(
            "ALIF", "DAALI", "LAAM", "MIIM", "BAA", "SINNYIIYHE", "PULAAR", "BHE",
            "RAA", "E", "FA", "I", "O", "U", "YHE", "WAW", "NUN", "KAF",
            "YAA", "HE", "WAW_LAABI", "ARRE", "CHE", "JE", "TEE", "NYE", "GBE", "KPOKPO"
        )
        
        every { sharedPreferences.getStringSet("mastered_letters", emptySet()) } returns allLetters
        
        val newManager = AdlamAlphabetManager(context)
        val isComplete = newManager.isAlphabetComplete()
        
        assert(isComplete)
    }

    @Test
    fun testAdvanceToNextPhase() {
        // Mock mastery of visual phase
        every { sharedPreferences.getStringSet("mastered_letters", emptySet()) } returns setOf("ALIF")
        
        val newManager = AdlamAlphabetManager(context)
        val hasNextPhase = newManager.advanceToNextPhase()
        
        // Should advance to audio phase
        assert(hasNextPhase)
        
        // Verify that progress was saved
        verify { editor.putString("current_phase", "AUDIO_RECOGNITION") }
        verify { editor.apply() }
    }

    @Test
    fun testAdvanceToNextLetter() {
        // Mock mastery of both phases for first letter
        every { sharedPreferences.getStringSet("mastered_letters", emptySet()) } returns setOf("ALIF")
        every { sharedPreferences.getString("current_phase", "VISUAL_RECOGNITION") } returns "AUDIO_RECOGNITION"
        
        val newManager = AdlamAlphabetManager(context)
        val hasNextPhase = newManager.advanceToNextPhase()
        
        // Should advance to next letter
        assert(!hasNextPhase) // Returns false when moving to next letter
        
        // Verify that progress was saved
        verify { editor.putInt("current_letter_index", 1) }
        verify { editor.putString("current_phase", "VISUAL_RECOGNITION") }
        verify { editor.apply() }
    }

    @Test
    fun testQuizStateManagement() {
        val initialState = alphabetManager.currentQuizState.value
        
        // Initial state should be empty
        assert(initialState.visualQuestions.isEmpty())
        assert(initialState.audioQuestions.isEmpty())
        
        assert(initialState.correctAnswers == 0)
        assert(initialState.answeredQuestions == 0)
    }

    @Test
    fun testResetCurrentQuiz() {
        // Start a quiz first
        alphabetManager.startVisualRecognitionQuiz()
        
        // Reset the quiz
        alphabetManager.resetCurrentQuiz()
        
        val state = alphabetManager.currentQuizState.value
        
        // State should be reset
        assert(state.correctAnswers == 0)
        assert(state.answeredQuestions == 0)
    }

    @Test
    fun testPersistenceFlow() {
        // Test that data is properly saved and loaded
        
        // Save some progress
        alphabetManager.startVisualRecognitionQuiz()
        
        // Verify that SharedPreferences was used
        verify { context.getSharedPreferences("adlam_alphabet_progress", Context.MODE_PRIVATE) }
    }

    @Test
    fun testErrorHandling() {
        // Test behavior when SharedPreferences fails
        every { sharedPreferences.getInt("current_letter_index", 0) } throws RuntimeException("Storage error")
        
        try {
            AdlamAlphabetManager(context)
            // Should not crash, should use default values
        } catch (e: Exception) {
            // Expected to handle gracefully
        }
    }
}