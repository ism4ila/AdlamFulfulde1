package com.bekisma.adlamfulfulde.data.studyplan

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

enum class StudyGoal {
    CASUAL_LEARNING,    // 15 min/day
    MODERATE_PROGRESS,  // 30 min/day
    INTENSIVE_STUDY,    // 60 min/day
    EXAM_PREPARATION    // 90 min/day
}

enum class LearningStyle {
    VISUAL,
    AUDITORY,
    KINESTHETIC,
    READING_WRITING,
    MIXED
}

enum class StudyModule {
    ALPHABET,
    NUMBERS,
    WRITING,
    READING,
    QUIZ,
    PRONUNCIATION
}

data class StudyPlan(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val goal: StudyGoal,
    val learningStyle: LearningStyle,
    val targetModules: List<StudyModule>,
    val dailyTimeMinutes: Int,
    val weeklySchedule: Map<Int, Boolean>, // Day of week (1-7) -> enabled
    val duration: Int, // in weeks
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val difficulty: String = "Intermediate",
    val customization: StudyPlanCustomization = StudyPlanCustomization()
)

data class StudyPlanCustomization(
    val focusAreas: List<String> = emptyList(),
    val avoidAreas: List<String> = emptyList(),
    val preferredQuestionTypes: List<String> = emptyList(),
    val reminderTimes: List<String> = emptyList(),
    val adaptiveDifficulty: Boolean = true,
    val includeReviews: Boolean = true
)

data class StudySession(
    val id: String = UUID.randomUUID().toString(),
    val planId: String,
    val date: Long,
    val plannedDuration: Int,
    val actualDuration: Int,
    val modulesCompleted: List<StudyModule>,
    val score: Int,
    val completed: Boolean,
    val notes: String = ""
)

data class StudyPlanProgress(
    val planId: String,
    val weekNumber: Int,
    val totalSessions: Int,
    val completedSessions: Int,
    val averageScore: Double,
    val totalTimeSpent: Long,
    val consistency: Double, // percentage of scheduled sessions completed
    val moduleProgress: Map<StudyModule, Double>
)

class StudyPlanManager {
    private val _currentPlan = MutableStateFlow<StudyPlan?>(null)
    val currentPlan: StateFlow<StudyPlan?> = _currentPlan.asStateFlow()
    
    private val _availablePlans = MutableStateFlow<List<StudyPlan>>(emptyList())
    val availablePlans: StateFlow<List<StudyPlan>> = _availablePlans.asStateFlow()
    
    private val _todaySchedule = MutableStateFlow<List<StudyModule>>(emptyList())
    val todaySchedule: StateFlow<List<StudyModule>> = _todaySchedule.asStateFlow()
    
    private val _progress = MutableStateFlow<StudyPlanProgress?>(null)
    val progress: StateFlow<StudyPlanProgress?> = _progress.asStateFlow()
    
    private val studySessions = mutableListOf<StudySession>()
    private val savedPlans = mutableListOf<StudyPlan>()
    
    init {
        loadDefaultPlans()
    }
    
    private fun loadDefaultPlans() {
        val defaultPlans = listOf(
            StudyPlan(
                name = "Beginner's Journey",
                goal = StudyGoal.CASUAL_LEARNING,
                learningStyle = LearningStyle.VISUAL,
                targetModules = listOf(StudyModule.ALPHABET, StudyModule.NUMBERS, StudyModule.WRITING),
                dailyTimeMinutes = 15,
                weeklySchedule = mapOf(1 to true, 2 to true, 3 to true, 4 to true, 5 to true, 6 to false, 7 to false),
                duration = 8,
                difficulty = "Beginner"
            ),
            StudyPlan(
                name = "Intensive Mastery",
                goal = StudyGoal.INTENSIVE_STUDY,
                learningStyle = LearningStyle.MIXED,
                targetModules = StudyModule.values().toList(),
                dailyTimeMinutes = 60,
                weeklySchedule = mapOf(1 to true, 2 to true, 3 to true, 4 to true, 5 to true, 6 to true, 7 to true),
                duration = 12,
                difficulty = "Advanced"
            ),
            StudyPlan(
                name = "Reading Focus",
                goal = StudyGoal.MODERATE_PROGRESS,
                learningStyle = LearningStyle.READING_WRITING,
                targetModules = listOf(StudyModule.READING, StudyModule.WRITING),
                dailyTimeMinutes = 30,
                weeklySchedule = mapOf(1 to true, 2 to false, 3 to true, 4 to false, 5 to true, 6 to true, 7 to false),
                duration = 10,
                difficulty = "Intermediate"
            )
        )
        
        _availablePlans.value = defaultPlans
        savedPlans.addAll(defaultPlans)
    }
    
    fun createCustomPlan(
        name: String,
        goal: StudyGoal,
        learningStyle: LearningStyle,
        targetModules: List<StudyModule>,
        weeklySchedule: Map<Int, Boolean>,
        duration: Int,
        customization: StudyPlanCustomization = StudyPlanCustomization()
    ): StudyPlan {
        val dailyTime = when (goal) {
            StudyGoal.CASUAL_LEARNING -> 15
            StudyGoal.MODERATE_PROGRESS -> 30
            StudyGoal.INTENSIVE_STUDY -> 60
            StudyGoal.EXAM_PREPARATION -> 90
        }
        
        val plan = StudyPlan(
            name = name,
            goal = goal,
            learningStyle = learningStyle,
            targetModules = targetModules,
            dailyTimeMinutes = dailyTime,
            weeklySchedule = weeklySchedule,
            duration = duration,
            customization = customization
        )
        
        savedPlans.add(plan)
        _availablePlans.value = savedPlans.toList()
        
        return plan
    }
    
    fun activatePlan(planId: String) {
        val plan = savedPlans.find { it.id == planId }
        if (plan != null) {
            _currentPlan.value = plan
            generateTodaySchedule()
            updateProgress()
        }
    }
    
    private fun generateTodaySchedule() {
        val plan = _currentPlan.value ?: return
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        
        if (plan.weeklySchedule[dayOfWeek] != true) {
            _todaySchedule.value = emptyList()
            return
        }
        
        val totalTimeMinutes = plan.dailyTimeMinutes
        val timePerModule = totalTimeMinutes / plan.targetModules.size
        
        val schedule = when (plan.learningStyle) {
            LearningStyle.VISUAL -> prioritizeVisualModules(plan.targetModules)
            LearningStyle.AUDITORY -> prioritizeAudioModules(plan.targetModules)
            LearningStyle.KINESTHETIC -> prioritizeInteractiveModules(plan.targetModules)
            LearningStyle.READING_WRITING -> prioritizeTextModules(plan.targetModules)
            LearningStyle.MIXED -> plan.targetModules.shuffled()
        }
        
        _todaySchedule.value = schedule
    }
    
    private fun prioritizeVisualModules(modules: List<StudyModule>): List<StudyModule> {
        val priority = listOf(StudyModule.ALPHABET, StudyModule.WRITING, StudyModule.NUMBERS)
        return modules.sortedBy { module -> priority.indexOf(module).takeIf { it >= 0 } ?: Int.MAX_VALUE }
    }
    
    private fun prioritizeAudioModules(modules: List<StudyModule>): List<StudyModule> {
        val priority = listOf(StudyModule.PRONUNCIATION, StudyModule.READING)
        return modules.sortedBy { module -> priority.indexOf(module).takeIf { it >= 0 } ?: Int.MAX_VALUE }
    }
    
    private fun prioritizeInteractiveModules(modules: List<StudyModule>): List<StudyModule> {
        val priority = listOf(StudyModule.QUIZ, StudyModule.WRITING, StudyModule.ALPHABET)
        return modules.sortedBy { module -> priority.indexOf(module).takeIf { it >= 0 } ?: Int.MAX_VALUE }
    }
    
    private fun prioritizeTextModules(modules: List<StudyModule>): List<StudyModule> {
        val priority = listOf(StudyModule.READING, StudyModule.WRITING)
        return modules.sortedBy { module -> priority.indexOf(module).takeIf { it >= 0 } ?: Int.MAX_VALUE }
    }
    
    fun completeSession(
        moduleCompleted: StudyModule,
        actualDuration: Int,
        score: Int,
        notes: String = ""
    ) {
        val plan = _currentPlan.value ?: return
        
        val session = StudySession(
            planId = plan.id,
            date = System.currentTimeMillis(),
            plannedDuration = plan.dailyTimeMinutes,
            actualDuration = actualDuration,
            modulesCompleted = listOf(moduleCompleted),
            score = score,
            completed = true,
            notes = notes
        )
        
        studySessions.add(session)
        updateProgress()
    }
    
    private fun updateProgress() {
        val plan = _currentPlan.value ?: return
        val planSessions = studySessions.filter { it.planId == plan.id }
        
        if (planSessions.isEmpty()) {
            _progress.value = StudyPlanProgress(
                planId = plan.id,
                weekNumber = 1,
                totalSessions = 0,
                completedSessions = 0,
                averageScore = 0.0,
                totalTimeSpent = 0L,
                consistency = 0.0,
                moduleProgress = emptyMap()
            )
            return
        }
        
        val weekNumber = getWeekNumber(plan.createdAt)
        val scheduledSessionsPerWeek = plan.weeklySchedule.values.count { it }
        val totalScheduledSessions = scheduledSessionsPerWeek * weekNumber
        
        val completedSessions = planSessions.size
        val averageScore = planSessions.map { it.score }.average()
        val totalTimeSpent = planSessions.sumOf { it.actualDuration.toLong() }
        val consistency = completedSessions.toDouble() / totalScheduledSessions
        
        val moduleProgress = plan.targetModules.associateWith { module ->
            val moduleSessions = planSessions.filter { module in it.modulesCompleted }
            moduleSessions.size.toDouble() / maxOf(1, completedSessions)
        }
        
        _progress.value = StudyPlanProgress(
            planId = plan.id,
            weekNumber = weekNumber,
            totalSessions = totalScheduledSessions,
            completedSessions = completedSessions,
            averageScore = averageScore,
            totalTimeSpent = totalTimeSpent,
            consistency = consistency,
            moduleProgress = moduleProgress
        )
    }
    
    private fun getWeekNumber(startTime: Long): Int {
        val timeDiff = System.currentTimeMillis() - startTime
        return (timeDiff / (7 * 24 * 60 * 60 * 1000)).toInt() + 1
    }
    
    fun getStudyStreak(): Int {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        var streak = 0
        var currentDay = Calendar.getInstance().apply { 
            timeInMillis = today.timeInMillis
        }
        
        while (true) {
            val dayStart = currentDay.timeInMillis
            val dayEnd = dayStart + 24 * 60 * 60 * 1000
            
            val hasSession = studySessions.any { session ->
                session.date in dayStart until dayEnd && session.completed
            }
            
            if (hasSession) {
                streak++
                currentDay.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        
        return streak
    }
    
    fun getWeeklyReport(): Map<String, Any> {
        val plan = _currentPlan.value ?: return emptyMap()
        val weekSessions = studySessions.filter { session ->
            val weekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
            session.date >= weekAgo && session.planId == plan.id
        }
        
        return mapOf(
            "sessions_completed" to weekSessions.size,
            "total_time_spent" to weekSessions.sumOf { it.actualDuration },
            "average_score" to (weekSessions.map { it.score }.average().takeIf { !it.isNaN() } ?: 0.0),
            "consistency" to (weekSessions.size.toDouble() / 7) * 100,
            "module_breakdown" to weekSessions.flatMap { it.modulesCompleted }
                .groupingBy { it }.eachCount(),
            "daily_breakdown" to weekSessions.groupBy { session ->
                Calendar.getInstance().apply { timeInMillis = session.date }.get(Calendar.DAY_OF_WEEK)
            }.mapValues { (_, sessions) -> sessions.size }
        )
    }
    
    fun adjustPlanDifficulty(newDifficulty: String) {
        val plan = _currentPlan.value ?: return
        val updatedPlan = plan.copy(difficulty = newDifficulty)
        
        val index = savedPlans.indexOfFirst { it.id == plan.id }
        if (index >= 0) {
            savedPlans[index] = updatedPlan
            _currentPlan.value = updatedPlan
            _availablePlans.value = savedPlans.toList()
        }
    }
    
    fun getRecommendations(): List<String> {
        val plan = _currentPlan.value ?: return emptyList()
        val progress = _progress.value ?: return emptyList()
        val recommendations = mutableListOf<String>()
        
        if (progress.consistency < 0.5) {
            recommendations.add("Try to maintain consistency - aim for at least 50% of scheduled sessions")
        }
        
        if (progress.averageScore < 70) {
            recommendations.add("Consider focusing on easier modules to build confidence")
        }
        
        val weakestModule = progress.moduleProgress.minByOrNull { it.value }
        if (weakestModule != null && weakestModule.value < 0.3) {
            recommendations.add("Spend more time on ${weakestModule.key.name.lowercase()} - it needs attention")
        }
        
        if (getStudyStreak() >= 7) {
            recommendations.add("Great job! You're on a ${getStudyStreak()}-day streak!")
        }
        
        return recommendations
    }
}