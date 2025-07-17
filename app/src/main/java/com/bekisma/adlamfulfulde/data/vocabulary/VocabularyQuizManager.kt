package com.bekisma.adlamfulfulde.data.vocabulary

import com.bekisma.adlamfulfulde.data.VocabularyItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

enum class VocabularyQuizType {
    MULTIPLE_CHOICE_TRANSLATION,    // Given Adlam word, choose translation
    MULTIPLE_CHOICE_ADLAM,          // Given translation, choose Adlam word
    FILL_IN_THE_BLANK,              // Complete the sentence
    MATCHING,                       // Match Adlam words to translations
    TRUE_FALSE,                     // True/false statements about vocabulary
    LISTENING,                      // Audio-based questions
    CATEGORY_QUIZ,                  // Questions about word categories
    SPELLING_CHALLENGE              // Spell the Adlam word
}

data class VocabularyQuizQuestion(
    val id: String = UUID.randomUUID().toString(),
    val type: VocabularyQuizType,
    val question: String,
    val correctAnswer: String,
    val options: List<String> = emptyList(),
    val vocabularyItem: VocabularyItem,
    val audioUrl: String? = null,
    val imageUrl: String? = null,
    val explanation: String? = null,
    val category: String? = null,
    val difficulty: QuizDifficulty = QuizDifficulty.MEDIUM
)

enum class QuizDifficulty {
    EASY, MEDIUM, HARD
}

data class VocabularyQuizSession(
    val id: String = UUID.randomUUID().toString(),
    val quizType: VocabularyQuizType,
    val questions: List<VocabularyQuizQuestion>,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val answers: MutableMap<String, String> = mutableMapOf(),
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val timeSpent: Map<String, Long> = emptyMap(),
    val category: String? = null,
    val difficulty: QuizDifficulty = QuizDifficulty.MEDIUM
)

data class VocabularyQuizStats(
    val totalQuizzesTaken: Int,
    val averageScore: Float,
    val bestScore: Int,
    val totalTimeSpent: Long,
    val quizTypeStats: Map<VocabularyQuizType, QuizTypeStats>,
    val categoryStats: Map<String, CategoryStats>,
    val difficultyStats: Map<QuizDifficulty, DifficultyStats>,
    val recentSessions: List<VocabularyQuizSession>
)

data class QuizTypeStats(
    val totalQuizzes: Int,
    val averageScore: Float,
    val bestScore: Int,
    val averageTime: Long
)

data class CategoryStats(
    val totalQuizzes: Int,
    val averageScore: Float,
    val weakWords: List<VocabularyItem>,
    val strongWords: List<VocabularyItem>
)

data class DifficultyStats(
    val totalQuizzes: Int,
    val averageScore: Float,
    val completionRate: Float
)

class VocabularyQuizManager {
    private val _currentSession = MutableStateFlow<VocabularyQuizSession?>(null)
    val currentSession: StateFlow<VocabularyQuizSession?> = _currentSession.asStateFlow()
    
    private val _quizStats = MutableStateFlow(VocabularyQuizStats(
        totalQuizzesTaken = 0,
        averageScore = 0f,
        bestScore = 0,
        totalTimeSpent = 0L,
        quizTypeStats = emptyMap(),
        categoryStats = emptyMap(),
        difficultyStats = emptyMap(),
        recentSessions = emptyList()
    ))
    val quizStats: StateFlow<VocabularyQuizStats> = _quizStats.asStateFlow()
    
    private val completedSessions = mutableListOf<VocabularyQuizSession>()
    
    fun createQuiz(
        vocabularyItems: List<VocabularyItem>,
        quizType: VocabularyQuizType,
        questionCount: Int = 10,
        difficulty: QuizDifficulty = QuizDifficulty.MEDIUM,
        category: String? = null
    ): VocabularyQuizSession {
        val filteredItems = if (category != null) {
            vocabularyItems.filter { it.category == category }
        } else {
            vocabularyItems
        }.shuffled().take(questionCount)
        
        val questions = filteredItems.map { item ->
            generateQuestion(item, quizType, difficulty, vocabularyItems)
        }
        
        val session = VocabularyQuizSession(
            quizType = quizType,
            questions = questions,
            totalQuestions = questions.size,
            category = category,
            difficulty = difficulty
        )
        
        _currentSession.value = session
        return session
    }
    
    private fun generateQuestion(
        item: VocabularyItem,
        quizType: VocabularyQuizType,
        difficulty: QuizDifficulty,
        allItems: List<VocabularyItem>
    ): VocabularyQuizQuestion {
        return when (quizType) {
            VocabularyQuizType.MULTIPLE_CHOICE_TRANSLATION -> {
                createMultipleChoiceTranslation(item, allItems, difficulty)
            }
            VocabularyQuizType.MULTIPLE_CHOICE_ADLAM -> {
                createMultipleChoiceAdlam(item, allItems, difficulty)
            }
            VocabularyQuizType.FILL_IN_THE_BLANK -> {
                createFillInTheBlank(item, difficulty)
            }
            VocabularyQuizType.MATCHING -> {
                createMatchingQuestion(item, allItems, difficulty)
            }
            VocabularyQuizType.TRUE_FALSE -> {
                createTrueFalse(item, allItems, difficulty)
            }
            VocabularyQuizType.LISTENING -> {
                createListeningQuestion(item, allItems, difficulty)
            }
            VocabularyQuizType.CATEGORY_QUIZ -> {
                createCategoryQuestion(item, allItems, difficulty)
            }
            VocabularyQuizType.SPELLING_CHALLENGE -> {
                createSpellingChallenge(item, difficulty)
            }
        }
    }
    
    private fun createMultipleChoiceTranslation(
        item: VocabularyItem,
        allItems: List<VocabularyItem>,
        difficulty: QuizDifficulty
    ): VocabularyQuizQuestion {
        val distractors = allItems
            .filter { it.id != item.id }
            .shuffled()
            .take(3)
            .map { it.translation }
        
        val options = (distractors + item.translation).shuffled()
        
        return VocabularyQuizQuestion(
            type = VocabularyQuizType.MULTIPLE_CHOICE_TRANSLATION,
            question = "What does '${item.adlamWord}' mean?",
            correctAnswer = item.translation,
            options = options,
            vocabularyItem = item,
            difficulty = difficulty,
            explanation = "The word '${item.adlamWord}' means '${item.translation}' in English."
        )
    }
    
    private fun createMultipleChoiceAdlam(
        item: VocabularyItem,
        allItems: List<VocabularyItem>,
        difficulty: QuizDifficulty
    ): VocabularyQuizQuestion {
        val distractors = allItems
            .filter { it.id != item.id }
            .shuffled()
            .take(3)
            .map { it.adlamWord }
        
        val options = (distractors + item.adlamWord).shuffled()
        
        return VocabularyQuizQuestion(
            type = VocabularyQuizType.MULTIPLE_CHOICE_ADLAM,
            question = "How do you write '${item.translation}' in Adlam script?",
            correctAnswer = item.adlamWord,
            options = options,
            vocabularyItem = item,
            difficulty = difficulty,
            explanation = "The word '${item.translation}' is written as '${item.adlamWord}' in Adlam script."
        )
    }
    
    private fun createFillInTheBlank(
        item: VocabularyItem,
        difficulty: QuizDifficulty
    ): VocabularyQuizQuestion {
        val sentence = item.exampleSentenceAdlam ?: "${item.adlamWord} is a word in Adlam script."
        val blankSentence = sentence.replace(item.adlamWord, "____")
        
        return VocabularyQuizQuestion(
            type = VocabularyQuizType.FILL_IN_THE_BLANK,
            question = "Complete the sentence: $blankSentence",
            correctAnswer = item.adlamWord,
            vocabularyItem = item,
            difficulty = difficulty,
            explanation = "The missing word is '${item.adlamWord}' which means '${item.translation}'."
        )
    }
    
    private fun createMatchingQuestion(
        item: VocabularyItem,
        allItems: List<VocabularyItem>,
        difficulty: QuizDifficulty
    ): VocabularyQuizQuestion {
        val matchItems = allItems
            .filter { it.category == item.category }
            .shuffled()
            .take(4)
        
        val adlamWords = matchItems.map { it.adlamWord }
        val translations = matchItems.map { it.translation }.shuffled()
        
        return VocabularyQuizQuestion(
            type = VocabularyQuizType.MATCHING,
            question = "Match the Adlam word '${item.adlamWord}' with its correct translation:",
            correctAnswer = item.translation,
            options = translations,
            vocabularyItem = item,
            difficulty = difficulty,
            explanation = "The word '${item.adlamWord}' matches with '${item.translation}'."
        )
    }
    
    private fun createTrueFalse(
        item: VocabularyItem,
        allItems: List<VocabularyItem>,
        difficulty: QuizDifficulty
    ): VocabularyQuizQuestion {
        val isTrue = Random().nextBoolean()
        
        val (question, correctAnswer) = if (isTrue) {
            "The word '${item.adlamWord}' means '${item.translation}'" to "True"
        } else {
            val wrongTranslation = allItems
                .filter { it.id != item.id }
                .random()
                .translation
            "The word '${item.adlamWord}' means '$wrongTranslation'" to "False"
        }
        
        return VocabularyQuizQuestion(
            type = VocabularyQuizType.TRUE_FALSE,
            question = question,
            correctAnswer = correctAnswer,
            options = listOf("True", "False"),
            vocabularyItem = item,
            difficulty = difficulty,
            explanation = "The word '${item.adlamWord}' actually means '${item.translation}'."
        )
    }
    
    private fun createListeningQuestion(
        item: VocabularyItem,
        allItems: List<VocabularyItem>,
        difficulty: QuizDifficulty
    ): VocabularyQuizQuestion {
        val distractors = allItems
            .filter { it.id != item.id }
            .shuffled()
            .take(3)
            .map { it.translation }
        
        val options = (distractors + item.translation).shuffled()
        
        return VocabularyQuizQuestion(
            type = VocabularyQuizType.LISTENING,
            question = "Listen to the audio and choose the correct translation:",
            correctAnswer = item.translation,
            options = options,
            vocabularyItem = item,
            audioUrl = "audio_${item.id}",
            difficulty = difficulty,
            explanation = "The audio says '${item.adlamWord}' which means '${item.translation}'."
        )
    }
    
    private fun createCategoryQuestion(
        item: VocabularyItem,
        allItems: List<VocabularyItem>,
        difficulty: QuizDifficulty
    ): VocabularyQuizQuestion {
        val categories = allItems.mapNotNull { it.category }.distinct()
        val wrongCategories = categories.filter { it != item.category }.shuffled().take(3)
        val options = (wrongCategories + (item.category ?: "Unknown")).shuffled()
        
        return VocabularyQuizQuestion(
            type = VocabularyQuizType.CATEGORY_QUIZ,
            question = "Which category does the word '${item.adlamWord}' (${item.translation}) belong to?",
            correctAnswer = item.category ?: "Unknown",
            options = options,
            vocabularyItem = item,
            difficulty = difficulty,
            explanation = "The word '${item.adlamWord}' belongs to the '${item.category}' category."
        )
    }
    
    private fun createSpellingChallenge(
        item: VocabularyItem,
        difficulty: QuizDifficulty
    ): VocabularyQuizQuestion {
        // Create spelling options with common mistakes
        val correctSpelling = item.adlamWord
        val wrongSpellings = generateSpellingMistakes(correctSpelling)
        val options = (wrongSpellings + correctSpelling).shuffled()
        
        return VocabularyQuizQuestion(
            type = VocabularyQuizType.SPELLING_CHALLENGE,
            question = "How do you spell '${item.translation}' in Adlam script?",
            correctAnswer = correctSpelling,
            options = options,
            vocabularyItem = item,
            difficulty = difficulty,
            explanation = "The correct spelling is '${correctSpelling}'."
        )
    }
    
    private fun generateSpellingMistakes(word: String): List<String> {
        // Simple spelling mistake generator
        val mistakes = mutableListOf<String>()
        
        // Character substitution
        if (word.length > 1) {
            val chars = word.toCharArray()
            chars[0] = if (chars[0] == word[0]) word[1] else word[0]
            mistakes.add(String(chars))
        }
        
        // Character removal
        if (word.length > 2) {
            mistakes.add(word.substring(1))
        }
        
        // Character addition
        mistakes.add(word + word.last())
        
        return mistakes.take(3)
    }
    
    fun submitAnswer(questionId: String, answer: String): Boolean {
        val session = _currentSession.value ?: return false
        val question = session.questions.find { it.id == questionId } ?: return false
        
        session.answers[questionId] = answer
        val isCorrect = answer == question.correctAnswer
        
        val updatedSession = session.copy(
            correctAnswers = session.correctAnswers + if (isCorrect) 1 else 0,
            score = session.score + if (isCorrect) 10 else 0
        )
        
        _currentSession.value = updatedSession
        return isCorrect
    }
    
    fun completeQuiz(): VocabularyQuizSession {
        val session = _currentSession.value ?: return VocabularyQuizSession(
            quizType = VocabularyQuizType.MULTIPLE_CHOICE_TRANSLATION,
            questions = emptyList()
        )
        
        val completedSession = session.copy(
            endTime = System.currentTimeMillis()
        )
        
        completedSessions.add(completedSession)
        _currentSession.value = null
        
        updateStats()
        return completedSession
    }
    
    private fun updateStats() {
        if (completedSessions.isEmpty()) return
        
        val totalQuizzes = completedSessions.size
        val averageScore = completedSessions.map { 
            it.score.toFloat() / (it.totalQuestions * 10) * 100 
        }.average().toFloat()
        val bestScore = completedSessions.maxOfOrNull { it.score } ?: 0
        val totalTimeSpent = completedSessions.sumOf { session ->
            (session.endTime ?: System.currentTimeMillis()) - session.startTime
        }
        
        // Quiz type stats
        val quizTypeStats = completedSessions.groupBy { it.quizType }
            .mapValues { (_, sessions) ->
                QuizTypeStats(
                    totalQuizzes = sessions.size,
                    averageScore = sessions.map { it.score.toFloat() / (it.totalQuestions * 10) * 100 }.average().toFloat(),
                    bestScore = sessions.maxOfOrNull { it.score } ?: 0,
                    averageTime = sessions.map { 
                        (it.endTime ?: System.currentTimeMillis()) - it.startTime 
                    }.average().toLong()
                )
            }
        
        // Category stats
        val categoryStats = completedSessions.groupBy { it.category }
            .filter { it.key != null }
            .mapKeys { it.key!! }
            .mapValues { (_, sessions) ->
                CategoryStats(
                    totalQuizzes = sessions.size,
                    averageScore = sessions.map { it.score.toFloat() / (it.totalQuestions * 10) * 100 }.average().toFloat(),
                    weakWords = emptyList(), // TODO: Calculate weak words
                    strongWords = emptyList() // TODO: Calculate strong words
                )
            }
        
        // Difficulty stats
        val difficultyStats = completedSessions.groupBy { it.difficulty }
            .mapValues { (_, sessions) ->
                DifficultyStats(
                    totalQuizzes = sessions.size,
                    averageScore = sessions.map { it.score.toFloat() / (it.totalQuestions * 10) * 100 }.average().toFloat(),
                    completionRate = sessions.count { it.endTime != null }.toFloat() / sessions.size * 100
                )
            }
        
        _quizStats.value = VocabularyQuizStats(
            totalQuizzesTaken = totalQuizzes,
            averageScore = averageScore,
            bestScore = bestScore,
            totalTimeSpent = totalTimeSpent,
            quizTypeStats = quizTypeStats,
            categoryStats = categoryStats,
            difficultyStats = difficultyStats,
            recentSessions = completedSessions.takeLast(10)
        )
    }
    
    fun getQuizRecommendations(vocabularyItems: List<VocabularyItem>): List<VocabularyQuizType> {
        val stats = _quizStats.value
        
        // Recommend quiz types based on performance
        val recommendations = mutableListOf<VocabularyQuizType>()
        
        // Add types with lower performance first
        val sortedTypes = stats.quizTypeStats.toList().sortedBy { it.second.averageScore }
        sortedTypes.forEach { (type, _) ->
            recommendations.add(type)
        }
        
        // Add types that haven't been tried
        VocabularyQuizType.values().forEach { type ->
            if (!stats.quizTypeStats.containsKey(type)) {
                recommendations.add(type)
            }
        }
        
        return recommendations.take(5)
    }
    
    fun getWeakCategories(): List<String> {
        return _quizStats.value.categoryStats
            .filter { it.value.averageScore < 70 }
            .map { it.key }
            .sortedBy { _quizStats.value.categoryStats[it]?.averageScore ?: 0f }
    }
    
    fun getQuizSuggestion(vocabularyItems: List<VocabularyItem>): Pair<VocabularyQuizType, String?> {
        val weakCategories = getWeakCategories()
        val recommendations = getQuizRecommendations(vocabularyItems)
        
        return if (weakCategories.isNotEmpty() && recommendations.isNotEmpty()) {
            recommendations.first() to weakCategories.first()
        } else if (recommendations.isNotEmpty()) {
            recommendations.first() to null
        } else {
            VocabularyQuizType.MULTIPLE_CHOICE_TRANSLATION to null
        }
    }
}