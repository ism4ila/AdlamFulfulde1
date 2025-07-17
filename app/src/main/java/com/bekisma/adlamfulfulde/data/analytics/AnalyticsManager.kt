package com.bekisma.adlamfulfulde.data.analytics

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

data class LearningSession(
    val id: String = UUID.randomUUID().toString(),
    val moduleId: String,
    val startTime: Long,
    val endTime: Long,
    val questionsAnswered: Int,
    val correctAnswers: Int,
    val timeSpent: Long,
    val completed: Boolean
)

data class StudyStreak(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastStudyDate: Long?
)

data class ModuleAnalytics(
    val moduleId: String,
    val totalTimeSpent: Long,
    val totalSessions: Int,
    val averageAccuracy: Float,
    val completionRate: Float,
    val lastAccessed: Long,
    val progressPercentage: Float,
    val averageSessionTime: Long,
    val bestScore: Int,
    val difficultyLevel: String
)

data class LearningAnalytics(
    val totalStudyTime: Long,
    val totalSessions: Int,
    val averageAccuracy: Float,
    val studyStreak: StudyStreak,
    val moduleAnalytics: Map<String, ModuleAnalytics>,
    val weeklyProgress: List<DailyProgress>,
    val monthlyProgress: List<DailyProgress>,
    val achievements: List<Achievement>
)

data class DailyProgress(
    val date: String,
    val studyTime: Long,
    val sessionsCompleted: Int,
    val accuracy: Float,
    val modulesStudied: Set<String>
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int,
    val unlockedAt: Long,
    val category: AchievementCategory
)

enum class AchievementCategory {
    STREAK, ACCURACY, TIME_SPENT, COMPLETION, MILESTONE
}

class AnalyticsManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("analytics_prefs", Context.MODE_PRIVATE)
    private val _analytics = MutableStateFlow(loadAnalytics())
    val analytics: StateFlow<LearningAnalytics> = _analytics.asStateFlow()

    private fun loadAnalytics(): LearningAnalytics {
        // Load from SharedPreferences - in real app, this would be from a database
        val totalStudyTime = prefs.getLong("total_study_time", 0L)
        val totalSessions = prefs.getInt("total_sessions", 0)
        val averageAccuracy = prefs.getFloat("average_accuracy", 0f)
        
        val currentStreak = prefs.getInt("current_streak", 0)
        val longestStreak = prefs.getInt("longest_streak", 0)
        val lastStudyDate = prefs.getLong("last_study_date", 0L)
        
        return LearningAnalytics(
            totalStudyTime = totalStudyTime,
            totalSessions = totalSessions,
            averageAccuracy = averageAccuracy,
            studyStreak = StudyStreak(currentStreak, longestStreak, if (lastStudyDate > 0) lastStudyDate else null),
            moduleAnalytics = loadModuleAnalytics(),
            weeklyProgress = generateWeeklyProgress(),
            monthlyProgress = generateMonthlyProgress(),
            achievements = loadAchievements()
        )
    }

    private fun loadModuleAnalytics(): Map<String, ModuleAnalytics> {
        // Sample data - in real app, this would be loaded from database
        return mapOf(
            "alphabet" to ModuleAnalytics(
                moduleId = "alphabet",
                totalTimeSpent = 3600000L, // 1 hour
                totalSessions = 15,
                averageAccuracy = 0.85f,
                completionRate = 0.75f,
                lastAccessed = System.currentTimeMillis() - 86400000L, // 1 day ago
                progressPercentage = 0.75f,
                averageSessionTime = 240000L, // 4 minutes
                bestScore = 95,
                difficultyLevel = "Intermediate"
            ),
            "numbers" to ModuleAnalytics(
                moduleId = "numbers",
                totalTimeSpent = 1800000L, // 30 minutes
                totalSessions = 8,
                averageAccuracy = 0.90f,
                completionRate = 0.45f,
                lastAccessed = System.currentTimeMillis() - 172800000L, // 2 days ago
                progressPercentage = 0.45f,
                averageSessionTime = 225000L, // 3.75 minutes
                bestScore = 88,
                difficultyLevel = "Beginner"
            ),
            "writing" to ModuleAnalytics(
                moduleId = "writing",
                totalTimeSpent = 2700000L, // 45 minutes
                totalSessions = 12,
                averageAccuracy = 0.78f,
                completionRate = 0.30f,
                lastAccessed = System.currentTimeMillis() - 259200000L, // 3 days ago
                progressPercentage = 0.30f,
                averageSessionTime = 225000L, // 3.75 minutes
                bestScore = 82,
                difficultyLevel = "Intermediate"
            ),
            "quiz" to ModuleAnalytics(
                moduleId = "quiz",
                totalTimeSpent = 1200000L, // 20 minutes
                totalSessions = 6,
                averageAccuracy = 0.92f,
                completionRate = 0.60f,
                lastAccessed = System.currentTimeMillis() - 345600000L, // 4 days ago
                progressPercentage = 0.60f,
                averageSessionTime = 200000L, // 3.33 minutes
                bestScore = 98,
                difficultyLevel = "Advanced"
            )
        )
    }

    private fun generateWeeklyProgress(): List<DailyProgress> {
        val calendar = Calendar.getInstance()
        val weeklyProgress = mutableListOf<DailyProgress>()
        
        for (i in 6 downTo 0) {
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}"
            
            weeklyProgress.add(DailyProgress(
                date = date,
                studyTime = (Math.random() * 3600000).toLong(), // Random study time up to 1 hour
                sessionsCompleted = (Math.random() * 5).toInt() + 1,
                accuracy = (Math.random() * 0.3 + 0.7).toFloat(), // 70-100% accuracy
                modulesStudied = setOf("alphabet", "numbers").take((Math.random() * 2).toInt() + 1).toSet()
            ))
            
            if (i > 0) calendar.add(Calendar.DAY_OF_YEAR, i)
        }
        
        return weeklyProgress
    }

    private fun generateMonthlyProgress(): List<DailyProgress> {
        val calendar = Calendar.getInstance()
        val monthlyProgress = mutableListOf<DailyProgress>()
        
        for (i in 29 downTo 0) {
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}"
            
            monthlyProgress.add(DailyProgress(
                date = date,
                studyTime = if (Math.random() > 0.3) (Math.random() * 3600000).toLong() else 0L,
                sessionsCompleted = if (Math.random() > 0.3) (Math.random() * 5).toInt() + 1 else 0,
                accuracy = if (Math.random() > 0.3) (Math.random() * 0.3 + 0.7).toFloat() else 0f,
                modulesStudied = if (Math.random() > 0.3) setOf("alphabet", "numbers", "writing").take((Math.random() * 3).toInt() + 1).toSet() else emptySet()
            ))
            
            if (i > 0) calendar.add(Calendar.DAY_OF_YEAR, i)
        }
        
        return monthlyProgress
    }

    private fun loadAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "first_lesson",
                title = "First Steps",
                description = "Complete your first lesson",
                iconRes = com.bekisma.adlamfulfulde.R.drawable.ic_best_score,
                unlockedAt = System.currentTimeMillis() - 604800000L, // 1 week ago
                category = AchievementCategory.MILESTONE
            ),
            Achievement(
                id = "week_streak",
                title = "Week Warrior",
                description = "Study for 7 consecutive days",
                iconRes = com.bekisma.adlamfulfulde.R.drawable.ic_best_score,
                unlockedAt = System.currentTimeMillis() - 259200000L, // 3 days ago
                category = AchievementCategory.STREAK
            ),
            Achievement(
                id = "accuracy_master",
                title = "Accuracy Master",
                description = "Achieve 95% accuracy in a quiz",
                iconRes = com.bekisma.adlamfulfulde.R.drawable.ic_best_score,
                unlockedAt = System.currentTimeMillis() - 86400000L, // 1 day ago
                category = AchievementCategory.ACCURACY
            )
        )
    }

    fun recordLearningSession(session: LearningSession) {
        // Update analytics with new session
        val currentAnalytics = _analytics.value
        val newTotalTime = currentAnalytics.totalStudyTime + session.timeSpent
        val newTotalSessions = currentAnalytics.totalSessions + 1
        val newAverageAccuracy = if (session.questionsAnswered > 0) {
            val accuracy = session.correctAnswers.toFloat() / session.questionsAnswered
            (currentAnalytics.averageAccuracy * currentAnalytics.totalSessions + accuracy) / newTotalSessions
        } else currentAnalytics.averageAccuracy

        // Update streak
        val newStreak = updateStreak(currentAnalytics.studyStreak)

        // Save to preferences
        prefs.edit()
            .putLong("total_study_time", newTotalTime)
            .putInt("total_sessions", newTotalSessions)
            .putFloat("average_accuracy", newAverageAccuracy)
            .putInt("current_streak", newStreak.currentStreak)
            .putInt("longest_streak", newStreak.longestStreak)
            .putLong("last_study_date", System.currentTimeMillis())
            .apply()

        // Update state
        _analytics.value = currentAnalytics.copy(
            totalStudyTime = newTotalTime,
            totalSessions = newTotalSessions,
            averageAccuracy = newAverageAccuracy,
            studyStreak = newStreak
        )
    }

    private fun updateStreak(currentStreak: StudyStreak): StudyStreak {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val lastStudyDay = currentStreak.lastStudyDate?.let {
            Calendar.getInstance().apply {
                timeInMillis = it
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        val newStreak = when {
            lastStudyDay == null -> 1
            lastStudyDay == today -> currentStreak.currentStreak
            lastStudyDay == today - 86400000L -> currentStreak.currentStreak + 1
            else -> 1
        }

        return StudyStreak(
            currentStreak = newStreak,
            longestStreak = maxOf(newStreak, currentStreak.longestStreak),
            lastStudyDate = System.currentTimeMillis()
        )
    }

    fun exportAnalyticsData(): String {
        val analytics = _analytics.value
        return buildString {
            appendLine("=== ADLAM FULFULDE LEARNING ANALYTICS ===")
            appendLine("Generated: ${Date()}")
            appendLine()
            
            appendLine("OVERALL STATISTICS:")
            appendLine("Total Study Time: ${formatTime(analytics.totalStudyTime)}")
            appendLine("Total Sessions: ${analytics.totalSessions}")
            appendLine("Average Accuracy: ${String.format("%.1f", analytics.averageAccuracy * 100)}%")
            appendLine("Current Streak: ${analytics.studyStreak.currentStreak} days")
            appendLine("Longest Streak: ${analytics.studyStreak.longestStreak} days")
            appendLine()
            
            appendLine("MODULE ANALYTICS:")
            analytics.moduleAnalytics.forEach { (moduleId, moduleAnalytics) ->
                appendLine("${moduleId.uppercase()}:")
                appendLine("  Progress: ${String.format("%.1f", moduleAnalytics.progressPercentage * 100)}%")
                appendLine("  Time Spent: ${formatTime(moduleAnalytics.totalTimeSpent)}")
                appendLine("  Sessions: ${moduleAnalytics.totalSessions}")
                appendLine("  Accuracy: ${String.format("%.1f", moduleAnalytics.averageAccuracy * 100)}%")
                appendLine("  Best Score: ${moduleAnalytics.bestScore}")
                appendLine()
            }
            
            appendLine("ACHIEVEMENTS:")
            analytics.achievements.forEach { achievement ->
                appendLine("${achievement.title} - ${achievement.description}")
                appendLine("  Unlocked: ${Date(achievement.unlockedAt)}")
                appendLine()
            }
        }
    }

    private fun formatTime(milliseconds: Long): String {
        val hours = milliseconds / 3600000
        val minutes = (milliseconds % 3600000) / 60000
        return "${hours}h ${minutes}m"
    }
}