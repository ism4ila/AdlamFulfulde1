package com.bekisma.adlamfulfulde.data.quiz

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    FILL_IN_BLANK,
    MATCHING,
    AUDIO_RECOGNITION,
    WRITING_PRACTICE,
    SEQUENCE_ORDER
}

enum class DifficultyLevel {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
}

data class QuizQuestion(
    val id: String,
    val type: QuestionType,
    val difficulty: DifficultyLevel,
    val question: String,
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val explanation: String = "",
    val hints: List<String> = emptyList(),
    val timeLimit: Long = 30000, // 30 seconds default
    val points: Int = 10,
    val category: String = "general",
    val audioUrl: String? = null,
    val imageUrl: String? = null
)

data class QuizSession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val questions: List<QuizQuestion>,
    val answers: MutableMap<String, String> = mutableMapOf(),
    val score: Int = 0,
    val totalPossibleScore: Int = 0,
    val difficulty: DifficultyLevel,
    val category: String,
    val isCustom: Boolean = false,
    val timeSpent: Long = 0,
    val hintsUsed: Int = 0
)

data class QuizStatistics(
    val totalQuizzes: Int,
    val averageScore: Double,
    val bestScore: Int,
    val totalTimeSpent: Long,
    val difficultyBreakdown: Map<DifficultyLevel, Int>,
    val categoryBreakdown: Map<String, Int>,
    val accuracyByQuestionType: Map<QuestionType, Double>,
    val improvementTrend: List<Double>
)

class AdvancedQuizManager {
    private val _currentSession = MutableStateFlow<QuizSession?>(null)
    val currentSession: StateFlow<QuizSession?> = _currentSession.asStateFlow()
    
    private val _quizStatistics = MutableStateFlow(QuizStatistics(
        totalQuizzes = 0,
        averageScore = 0.0,
        bestScore = 0,
        totalTimeSpent = 0L,
        difficultyBreakdown = emptyMap(),
        categoryBreakdown = emptyMap(),
        accuracyByQuestionType = emptyMap(),
        improvementTrend = emptyList()
    ))
    val quizStatistics: StateFlow<QuizStatistics> = _quizStatistics.asStateFlow()
    
    private val questionBank = mutableListOf<QuizQuestion>()
    private val completedSessions = mutableListOf<QuizSession>()
    
    init {
        initializeQuestionBank()
    }
    
    private fun initializeQuestionBank() {
        // Alphabet questions
        questionBank.addAll(listOf(
            QuizQuestion(
                id = "alphabet_1",
                type = QuestionType.MULTIPLE_CHOICE,
                difficulty = DifficultyLevel.BEGINNER,
                question = "What is the first letter of the Adlam alphabet?",
                options = listOf("𞤢", "𞤣", "𞤤", "𞤥"),
                correctAnswer = "𞤢",
                explanation = "𞤢 (Alif) is the first letter of the Adlam alphabet, similar to 'A' in Latin script.",
                hints = listOf("Think of the first letter in Latin alphabet", "It looks like an 'A'"),
                category = "alphabet",
                points = 10
            ),
            QuizQuestion(
                id = "alphabet_2",
                type = QuestionType.FILL_IN_BLANK,
                difficulty = DifficultyLevel.INTERMEDIATE,
                question = "Complete the sequence: 𞤢, 𞤣, __, 𞤥",
                correctAnswer = "𞤤",
                explanation = "𞤤 (Lam) comes after 𞤣 (Daali) in the Adlam alphabet sequence.",
                hints = listOf("Think of alphabetical order", "It's the third letter"),
                category = "alphabet",
                points = 15
            ),
            QuizQuestion(
                id = "numbers_1",
                type = QuestionType.MULTIPLE_CHOICE,
                difficulty = DifficultyLevel.BEGINNER,
                question = "What number does 𞥑 represent?",
                options = listOf("1", "2", "3", "4"),
                correctAnswer = "1",
                explanation = "𞥑 represents the number 1 in Adlam numerals.",
                category = "numbers",
                points = 10
            ),
            QuizQuestion(
                id = "writing_1",
                type = QuestionType.WRITING_PRACTICE,
                difficulty = DifficultyLevel.INTERMEDIATE,
                question = "Write the Adlam word for 'hello'",
                correctAnswer = "𞤔𞤢𞤤𞤢𞤥",
                explanation = "𞤔𞤢𞤤𞤢𞤥 (jaamu) means 'hello' in Fulfulde using Adlam script.",
                hints = listOf("It starts with 𞤔", "It's a common greeting"),
                category = "language",
                points = 20,
                timeLimit = 60000
            ),
            QuizQuestion(
                id = "sequence_1",
                type = QuestionType.SEQUENCE_ORDER,
                difficulty = DifficultyLevel.ADVANCED,
                question = "Arrange these letters in alphabetical order",
                options = listOf("𞤥", "𞤢", "𞤤", "𞤣"),
                correctAnswer = "𞤢,𞤣,𞤤,𞤥",
                explanation = "The correct alphabetical order is 𞤢, 𞤣, 𞤤, 𞤥",
                category = "alphabet",
                points = 25
            )
        ))
    }
    
    fun createCustomQuiz(
        difficulty: DifficultyLevel,
        category: String,
        questionCount: Int,
        questionTypes: List<QuestionType> = QuestionType.values().toList()
    ): QuizSession {
        val filteredQuestions = questionBank.filter { question ->
            question.difficulty == difficulty && 
            question.category == category &&
            question.type in questionTypes
        }.shuffled().take(questionCount)
        
        val session = QuizSession(
            questions = filteredQuestions,
            difficulty = difficulty,
            category = category,
            isCustom = true,
            totalPossibleScore = filteredQuestions.sumOf { it.points }
        )
        
        _currentSession.value = session
        return session
    }
    
    fun createAdaptiveQuiz(
        previousPerformance: Map<String, Double>,
        questionCount: Int = 10
    ): QuizSession {
        val userLevel = determineUserLevel(previousPerformance)
        val weakCategories = identifyWeakCategories(previousPerformance)
        
        val selectedQuestions = mutableListOf<QuizQuestion>()
        
        // 60% from weak categories, 40% from mixed categories
        val weakCategoryQuestions = questionBank.filter { 
            it.category in weakCategories && 
            it.difficulty <= userLevel
        }.shuffled().take((questionCount * 0.6).toInt())
        
        val mixedQuestions = questionBank.filter { 
            it.difficulty <= userLevel
        }.shuffled().take(questionCount - weakCategoryQuestions.size)
        
        selectedQuestions.addAll(weakCategoryQuestions)
        selectedQuestions.addAll(mixedQuestions)
        
        val session = QuizSession(
            questions = selectedQuestions.shuffled(),
            difficulty = userLevel,
            category = "adaptive",
            totalPossibleScore = selectedQuestions.sumOf { it.points }
        )
        
        _currentSession.value = session
        return session
    }
    
    private fun determineUserLevel(performance: Map<String, Double>): DifficultyLevel {
        val averageScore = performance.values.average()
        return when {
            averageScore >= 0.9 -> DifficultyLevel.EXPERT
            averageScore >= 0.75 -> DifficultyLevel.ADVANCED
            averageScore >= 0.6 -> DifficultyLevel.INTERMEDIATE
            else -> DifficultyLevel.BEGINNER
        }
    }
    
    private fun identifyWeakCategories(performance: Map<String, Double>): List<String> {
        return performance.filter { it.value < 0.7 }.keys.toList()
    }
    
    fun submitAnswer(questionId: String, answer: String) {
        val session = _currentSession.value ?: return
        session.answers[questionId] = answer
        
        val question = session.questions.find { it.id == questionId }
        val updatedSession = if (question != null && answer == question.correctAnswer) {
            session.copy(score = session.score + question.points)
        } else {
            session
        }
        
        _currentSession.value = updatedSession
    }
    
    fun useHint(questionId: String) {
        val session = _currentSession.value ?: return
        val updatedSession = session.copy(hintsUsed = session.hintsUsed + 1)
        _currentSession.value = updatedSession
    }
    
    fun completeQuiz(): QuizSession {
        val session = _currentSession.value ?: return QuizSession(
            questions = emptyList(),
            difficulty = DifficultyLevel.BEGINNER,
            category = "general",
            totalPossibleScore = 0
        )
        
        val completedSession = session.copy(
            endTime = System.currentTimeMillis(),
            timeSpent = System.currentTimeMillis() - session.startTime
        )
        
        completedSessions.add(completedSession)
        updateStatistics()
        _currentSession.value = null
        
        return completedSession
    }
    
    private fun updateStatistics() {
        if (completedSessions.isEmpty()) return
        
        val totalQuizzes = completedSessions.size
        val averageScore = completedSessions.map { 
            it.score.toDouble() / it.totalPossibleScore 
        }.average()
        val bestScore = completedSessions.maxOfOrNull { it.score } ?: 0
        val totalTimeSpent = completedSessions.sumOf { it.timeSpent }
        
        val difficultyBreakdown = completedSessions.groupingBy { it.difficulty }.eachCount()
        val categoryBreakdown = completedSessions.groupingBy { it.category }.eachCount()
        
        val accuracyByQuestionType = mutableMapOf<QuestionType, Double>()
        QuestionType.values().forEach { type ->
            val questionsOfType = completedSessions.flatMap { session ->
                session.questions.filter { it.type == type }.map { question ->
                    question.id to (session.answers[question.id] == question.correctAnswer)
                }
            }
            if (questionsOfType.isNotEmpty()) {
                accuracyByQuestionType[type] = questionsOfType.count { it.second }.toDouble() / questionsOfType.size
            }
        }
        
        val improvementTrend = completedSessions.takeLast(10).map { 
            it.score.toDouble() / it.totalPossibleScore 
        }
        
        _quizStatistics.value = QuizStatistics(
            totalQuizzes = totalQuizzes,
            averageScore = averageScore,
            bestScore = bestScore,
            totalTimeSpent = totalTimeSpent,
            difficultyBreakdown = difficultyBreakdown,
            categoryBreakdown = categoryBreakdown,
            accuracyByQuestionType = accuracyByQuestionType,
            improvementTrend = improvementTrend
        )
    }
    
    fun getQuestionsByCategory(category: String): List<QuizQuestion> {
        return questionBank.filter { it.category == category }
    }
    
    fun getQuestionsByDifficulty(difficulty: DifficultyLevel): List<QuizQuestion> {
        return questionBank.filter { it.difficulty == difficulty }
    }
    
    fun addCustomQuestion(question: QuizQuestion) {
        questionBank.add(question)
    }
    
    fun getPerformanceAnalysis(): Map<String, Any> {
        val sessions = completedSessions.takeLast(10)
        if (sessions.isEmpty()) return emptyMap()
        
        return mapOf(
            "recent_performance" to sessions.map { it.score.toDouble() / it.totalPossibleScore },
            "category_strengths" to sessions.groupBy { it.category }.mapValues { (_, categorySessions) ->
                categorySessions.map { it.score.toDouble() / it.totalPossibleScore }.average()
            },
            "difficulty_progression" to sessions.groupBy { it.difficulty }.mapValues { (_, difficultySessions) ->
                difficultySessions.map { it.score.toDouble() / it.totalPossibleScore }.average()
            },
            "question_type_analysis" to QuestionType.values().associate { type ->
                type.name to sessions.flatMap { session ->
                    session.questions.filter { it.type == type }.map { question ->
                        session.answers[question.id] == question.correctAnswer
                    }
                }.let { results ->
                    if (results.isNotEmpty()) results.count { it }.toDouble() / results.size else 0.0
                }
            }
        )
    }
}