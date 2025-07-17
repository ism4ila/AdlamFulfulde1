package com.bekisma.adlamfulfulde.data.vocabulary

import android.content.Context
import com.bekisma.adlamfulfulde.data.VocabularyItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

// Spaced repetition intervals (in days)
private val SPACED_REPETITION_INTERVALS = listOf(1, 3, 7, 14, 30, 90)

enum class CardDifficulty {
    AGAIN,    // Show again soon
    HARD,     // Show with shorter interval
    GOOD,     // Show with normal interval
    EASY      // Show with longer interval
}

enum class MasteryLevel {
    NEW,         // Never studied
    LEARNING,    // Currently learning
    REVIEW,      // In review phase
    MASTERED     // Fully mastered
}

data class VocabularyCard(
    val vocabularyItem: VocabularyItem,
    val masteryLevel: MasteryLevel = MasteryLevel.NEW,
    val repetitionCount: Int = 0,
    val easinessFactor: Float = 2.5f,
    val interval: Int = 1, // days
    val nextReviewDate: Long = System.currentTimeMillis(),
    val lastReviewDate: Long? = null,
    val correctStreak: Int = 0,
    val totalReviews: Int = 0,
    val correctReviews: Int = 0,
    val averageResponseTime: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val customTags: List<String> = emptyList()
)

data class StudySession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val cardsStudied: List<VocabularyCard> = emptyList(),
    val totalCards: Int = 0,
    val correctAnswers: Int = 0,
    val averageResponseTime: Long = 0L,
    val studyType: StudyType = StudyType.FLASHCARDS
)

enum class StudyType {
    FLASHCARDS,
    QUIZ,
    REVIEW,
    CUSTOM_LIST
}

data class VocabularyStats(
    val totalWords: Int,
    val newWords: Int,
    val learningWords: Int,
    val reviewWords: Int,
    val masteredWords: Int,
    val todaysDue: Int,
    val studyStreak: Int,
    val totalStudyTime: Long,
    val averageAccuracy: Float,
    val wordsLearnedToday: Int,
    val bestCategory: String?,
    val weakestCategory: String?
)

class VocabularyLearningManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("vocabulary_learning", Context.MODE_PRIVATE)
    
    private val _vocabularyCards = MutableStateFlow<List<VocabularyCard>>(emptyList())
    val vocabularyCards: StateFlow<List<VocabularyCard>> = _vocabularyCards.asStateFlow()
    
    private val _currentStudySession = MutableStateFlow<StudySession?>(null)
    val currentStudySession: StateFlow<StudySession?> = _currentStudySession.asStateFlow()
    
    private val _vocabularyStats = MutableStateFlow(VocabularyStats(
        totalWords = 0,
        newWords = 0,
        learningWords = 0,
        reviewWords = 0,
        masteredWords = 0,
        todaysDue = 0,
        studyStreak = 0,
        totalStudyTime = 0L,
        averageAccuracy = 0f,
        wordsLearnedToday = 0,
        bestCategory = null,
        weakestCategory = null
    ))
    val vocabularyStats: StateFlow<VocabularyStats> = _vocabularyStats.asStateFlow()
    
    private val _favoriteWords = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteWords: StateFlow<Set<Int>> = _favoriteWords.asStateFlow()
    
    private val studySessions = mutableListOf<StudySession>()
    
    init {
        loadVocabularyData()
        loadFavorites()
        updateStats()
    }
    
    private fun loadVocabularyData() {
        // Load vocabulary cards from SharedPreferences
        // In a real app, this would load from a database
        val savedCards = loadCardsFromPrefs()
        _vocabularyCards.value = savedCards
    }
    
    private fun loadCardsFromPrefs(): List<VocabularyCard> {
        // Implementation would load from SharedPreferences
        // For now, return empty list - will be populated when vocabulary is initialized
        return emptyList()
    }
    
    private fun loadFavorites() {
        val favoritesString = prefs.getString("favorite_words", "")
        val favorites = if (favoritesString?.isNotEmpty() == true) {
            favoritesString.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        } else {
            emptySet()
        }
        _favoriteWords.value = favorites
    }
    
    fun initializeVocabularyCards(vocabularyItems: List<VocabularyItem>) {
        val existingCards = _vocabularyCards.value
        val newCards = vocabularyItems.mapNotNull { item ->
            existingCards.find { it.vocabularyItem.id == item.id }
                ?: VocabularyCard(
                    vocabularyItem = item,
                    isFavorite = _favoriteWords.value.contains(item.id)
                )
        }
        
        _vocabularyCards.value = newCards
        updateStats()
        saveCardsToPrefs(newCards)
    }
    
    private fun saveCardsToPrefs(cards: List<VocabularyCard>) {
        // Implementation would save to SharedPreferences
        // For now, just update stats
        updateStats()
    }
    
    fun startStudySession(studyType: StudyType = StudyType.FLASHCARDS): StudySession {
        val dueCards = getDueCards()
        val session = StudySession(
            studyType = studyType,
            totalCards = dueCards.size
        )
        
        _currentStudySession.value = session
        return session
    }
    
    fun getDueCards(): List<VocabularyCard> {
        val now = System.currentTimeMillis()
        return _vocabularyCards.value.filter { card ->
            card.nextReviewDate <= now
        }.sortedBy { it.nextReviewDate }
    }
    
    fun getNewCards(limit: Int = 10): List<VocabularyCard> {
        return _vocabularyCards.value
            .filter { it.masteryLevel == MasteryLevel.NEW }
            .take(limit)
    }
    
    fun getReviewCards(): List<VocabularyCard> {
        return _vocabularyCards.value
            .filter { it.masteryLevel == MasteryLevel.REVIEW }
            .sortedBy { it.nextReviewDate }
    }
    
    fun reviewCard(card: VocabularyCard, difficulty: CardDifficulty, responseTime: Long): VocabularyCard {
        val now = System.currentTimeMillis()
        
        // Calculate new easiness factor (SM-2 algorithm)
        val newEasinessFactor = when (difficulty) {
            CardDifficulty.AGAIN -> maxOf(1.3f, card.easinessFactor - 0.8f)
            CardDifficulty.HARD -> maxOf(1.3f, card.easinessFactor - 0.15f)
            CardDifficulty.GOOD -> card.easinessFactor
            CardDifficulty.EASY -> card.easinessFactor + 0.15f
        }
        
        // Calculate new interval
        val newInterval = when (difficulty) {
            CardDifficulty.AGAIN -> 1
            CardDifficulty.HARD -> maxOf(1, (card.interval * 0.8).toInt())
            CardDifficulty.GOOD -> (card.interval * newEasinessFactor).toInt()
            CardDifficulty.EASY -> (card.interval * newEasinessFactor * 1.3).toInt()
        }
        
        // Update mastery level
        val newMasteryLevel = when {
            difficulty == CardDifficulty.AGAIN -> MasteryLevel.LEARNING
            card.repetitionCount >= 3 && difficulty >= CardDifficulty.GOOD -> MasteryLevel.REVIEW
            card.repetitionCount >= 6 && difficulty >= CardDifficulty.GOOD -> MasteryLevel.MASTERED
            else -> MasteryLevel.LEARNING
        }
        
        // Calculate next review date
        val nextReviewDate = now + (newInterval * 24 * 60 * 60 * 1000L)
        
        // Update streak
        val newCorrectStreak = if (difficulty >= CardDifficulty.GOOD) {
            card.correctStreak + 1
        } else {
            0
        }
        
        val updatedCard = card.copy(
            masteryLevel = newMasteryLevel,
            repetitionCount = card.repetitionCount + 1,
            easinessFactor = newEasinessFactor,
            interval = newInterval,
            nextReviewDate = nextReviewDate,
            lastReviewDate = now,
            correctStreak = newCorrectStreak,
            totalReviews = card.totalReviews + 1,
            correctReviews = card.correctReviews + if (difficulty >= CardDifficulty.GOOD) 1 else 0,
            averageResponseTime = (card.averageResponseTime * card.totalReviews + responseTime) / (card.totalReviews + 1)
        )
        
        // Update the card in the list
        val updatedCards = _vocabularyCards.value.map { 
            if (it.vocabularyItem.id == card.vocabularyItem.id) updatedCard else it 
        }
        _vocabularyCards.value = updatedCards
        
        // Update current study session
        val currentSession = _currentStudySession.value
        if (currentSession != null) {
            val updatedSession = currentSession.copy(
                cardsStudied = currentSession.cardsStudied + updatedCard,
                correctAnswers = currentSession.correctAnswers + if (difficulty >= CardDifficulty.GOOD) 1 else 0,
                averageResponseTime = (currentSession.averageResponseTime * currentSession.cardsStudied.size + responseTime) / (currentSession.cardsStudied.size + 1)
            )
            _currentStudySession.value = updatedSession
        }
        
        saveCardsToPrefs(updatedCards)
        updateStats()
        
        return updatedCard
    }
    
    fun endStudySession(): StudySession? {
        val session = _currentStudySession.value
        if (session != null) {
            val completedSession = session.copy(
                endTime = System.currentTimeMillis()
            )
            
            studySessions.add(completedSession)
            _currentStudySession.value = null
            
            // Update study streak
            updateStudyStreak()
            
            return completedSession
        }
        return null
    }
    
    private fun updateStudyStreak() {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val lastStudyDate = prefs.getLong("last_study_date", 0L)
        val currentStreak = prefs.getInt("study_streak", 0)
        
        val newStreak = if (lastStudyDate == 0L) {
            1
        } else {
            val lastStudyDay = Calendar.getInstance().apply { timeInMillis = lastStudyDate }
            val daysDiff = ((today.timeInMillis - lastStudyDay.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()
            
            when (daysDiff) {
                0 -> currentStreak // Same day
                1 -> currentStreak + 1 // Next day
                else -> 1 // Break in streak
            }
        }
        
        prefs.edit()
            .putLong("last_study_date", today.timeInMillis)
            .putInt("study_streak", newStreak)
            .apply()
    }
    
    fun toggleFavorite(wordId: Int) {
        val currentFavorites = _favoriteWords.value.toMutableSet()
        
        if (currentFavorites.contains(wordId)) {
            currentFavorites.remove(wordId)
        } else {
            currentFavorites.add(wordId)
        }
        
        _favoriteWords.value = currentFavorites
        
        // Update the card's favorite status
        val updatedCards = _vocabularyCards.value.map { card ->
            if (card.vocabularyItem.id == wordId) {
                card.copy(isFavorite = currentFavorites.contains(wordId))
            } else {
                card
            }
        }
        _vocabularyCards.value = updatedCards
        
        // Save to SharedPreferences
        val favoritesString = currentFavorites.joinToString(",")
        prefs.edit().putString("favorite_words", favoritesString).apply()
        
        saveCardsToPrefs(updatedCards)
    }
    
    fun getFavoriteCards(): List<VocabularyCard> {
        return _vocabularyCards.value.filter { it.isFavorite }
    }
    
    fun getCardsByCategory(category: String): List<VocabularyCard> {
        return _vocabularyCards.value.filter { 
            it.vocabularyItem.category == category 
        }
    }
    
    fun getCardsByMasteryLevel(masteryLevel: MasteryLevel): List<VocabularyCard> {
        return _vocabularyCards.value.filter { it.masteryLevel == masteryLevel }
    }
    
    fun searchCards(query: String): List<VocabularyCard> {
        return _vocabularyCards.value.filter { card ->
            card.vocabularyItem.adlamWord.contains(query, ignoreCase = true) ||
            card.vocabularyItem.translation.contains(query, ignoreCase = true) ||
            card.vocabularyItem.category?.contains(query, ignoreCase = true) == true
        }
    }
    
    private fun updateStats() {
        val cards = _vocabularyCards.value
        val now = System.currentTimeMillis()
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val totalWords = cards.size
        val newWords = cards.count { it.masteryLevel == MasteryLevel.NEW }
        val learningWords = cards.count { it.masteryLevel == MasteryLevel.LEARNING }
        val reviewWords = cards.count { it.masteryLevel == MasteryLevel.REVIEW }
        val masteredWords = cards.count { it.masteryLevel == MasteryLevel.MASTERED }
        val todaysDue = cards.count { it.nextReviewDate <= now }
        
        val wordsLearnedToday = cards.count { card ->
            card.lastReviewDate != null && card.lastReviewDate >= todayStart
        }
        
        val averageAccuracy = if (cards.isNotEmpty()) {
            cards.map { card ->
                if (card.totalReviews > 0) {
                    card.correctReviews.toFloat() / card.totalReviews
                } else {
                    0f
                }
            }.average().toFloat()
        } else {
            0f
        }
        
        // Calculate category performance
        val categoryPerformance = cards.groupBy { it.vocabularyItem.category }
            .mapValues { (_, categoryCards) ->
                if (categoryCards.isNotEmpty()) {
                    categoryCards.map { card ->
                        if (card.totalReviews > 0) {
                            card.correctReviews.toFloat() / card.totalReviews
                        } else {
                            0f
                        }
                    }.average()
                } else {
                    0.0
                }
            }
        
        val bestCategory = categoryPerformance.maxByOrNull { it.value }?.key
        val weakestCategory = categoryPerformance.minByOrNull { it.value }?.key
        
        val studyStreak = prefs.getInt("study_streak", 0)
        val totalStudyTime = studySessions.sumOf { session ->
            (session.endTime ?: System.currentTimeMillis()) - session.startTime
        }
        
        _vocabularyStats.value = VocabularyStats(
            totalWords = totalWords,
            newWords = newWords,
            learningWords = learningWords,
            reviewWords = reviewWords,
            masteredWords = masteredWords,
            todaysDue = todaysDue,
            studyStreak = studyStreak,
            totalStudyTime = totalStudyTime,
            averageAccuracy = averageAccuracy,
            wordsLearnedToday = wordsLearnedToday,
            bestCategory = bestCategory,
            weakestCategory = weakestCategory
        )
    }
    
    fun resetProgress() {
        val resetCards = _vocabularyCards.value.map { card ->
            card.copy(
                masteryLevel = MasteryLevel.NEW,
                repetitionCount = 0,
                easinessFactor = 2.5f,
                interval = 1,
                nextReviewDate = System.currentTimeMillis(),
                lastReviewDate = null,
                correctStreak = 0,
                totalReviews = 0,
                correctReviews = 0,
                averageResponseTime = 0L
            )
        }
        
        _vocabularyCards.value = resetCards
        studySessions.clear()
        
        prefs.edit()
            .putInt("study_streak", 0)
            .putLong("last_study_date", 0L)
            .apply()
        
        saveCardsToPrefs(resetCards)
        updateStats()
    }
}