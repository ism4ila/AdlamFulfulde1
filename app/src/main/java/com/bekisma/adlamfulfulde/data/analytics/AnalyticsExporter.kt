package com.bekisma.adlamfulfulde.data.analytics

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

enum class ExportFormat {
    TEXT, CSV, JSON
}

class AnalyticsExporter(private val context: Context) {
    
    fun exportAnalytics(analytics: LearningAnalytics, format: ExportFormat): File {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
        val fileName = "adlam_analytics_$timestamp.${format.name.lowercase()}"
        
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        
        when (format) {
            ExportFormat.TEXT -> exportToText(analytics, file)
            ExportFormat.CSV -> exportToCSV(analytics, file)
            ExportFormat.JSON -> exportToJSON(analytics, file)
        }
        
        return file
    }
    
    private fun exportToText(analytics: LearningAnalytics, file: File) {
        val content = buildString {
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
                appendLine("  Difficulty: ${moduleAnalytics.difficultyLevel}")
                appendLine()
            }
            
            appendLine("ACHIEVEMENTS:")
            analytics.achievements.forEach { achievement ->
                appendLine("${achievement.title} - ${achievement.description}")
                appendLine("  Category: ${achievement.category}")
                appendLine("  Unlocked: ${Date(achievement.unlockedAt)}")
                appendLine()
            }
            
            appendLine("WEEKLY PROGRESS:")
            analytics.weeklyProgress.forEach { progress ->
                appendLine("${progress.date}: ${formatTime(progress.studyTime)}, ${progress.sessionsCompleted} sessions, ${String.format("%.1f", progress.accuracy * 100)}% accuracy")
            }
        }
        
        file.writeText(content)
    }
    
    private fun exportToCSV(analytics: LearningAnalytics, file: File) {
        val content = buildString {
            // Overall stats
            appendLine("Section,Metric,Value")
            appendLine("Overall,Total Study Time,${formatTime(analytics.totalStudyTime)}")
            appendLine("Overall,Total Sessions,${analytics.totalSessions}")
            appendLine("Overall,Average Accuracy,${String.format("%.1f", analytics.averageAccuracy * 100)}%")
            appendLine("Overall,Current Streak,${analytics.studyStreak.currentStreak}")
            appendLine("Overall,Longest Streak,${analytics.studyStreak.longestStreak}")
            appendLine()
            
            // Module analytics
            appendLine("Module,Progress,Time Spent,Sessions,Accuracy,Best Score,Difficulty")
            analytics.moduleAnalytics.forEach { (moduleId, moduleAnalytics) ->
                appendLine("$moduleId,${String.format("%.1f", moduleAnalytics.progressPercentage * 100)}%,${formatTime(moduleAnalytics.totalTimeSpent)},${moduleAnalytics.totalSessions},${String.format("%.1f", moduleAnalytics.averageAccuracy * 100)}%,${moduleAnalytics.bestScore},${moduleAnalytics.difficultyLevel}")
            }
            appendLine()
            
            // Daily progress
            appendLine("Date,Study Time,Sessions,Accuracy,Modules Studied")
            analytics.weeklyProgress.forEach { progress ->
                appendLine("${progress.date},${formatTime(progress.studyTime)},${progress.sessionsCompleted},${String.format("%.1f", progress.accuracy * 100)}%,${progress.modulesStudied.joinToString(";")}")
            }
            appendLine()
            
            // Achievements
            appendLine("Achievement,Description,Category,Unlocked Date")
            analytics.achievements.forEach { achievement ->
                appendLine("${achievement.title},${achievement.description},${achievement.category},${Date(achievement.unlockedAt)}")
            }
        }
        
        file.writeText(content)
    }
    
    private fun exportToJSON(analytics: LearningAnalytics, file: File) {
        val content = buildString {
            appendLine("{")
            appendLine("  \"generated\": \"${Date()}\",")
            appendLine("  \"overall_statistics\": {")
            appendLine("    \"total_study_time_ms\": ${analytics.totalStudyTime},")
            appendLine("    \"total_study_time_formatted\": \"${formatTime(analytics.totalStudyTime)}\",")
            appendLine("    \"total_sessions\": ${analytics.totalSessions},")
            appendLine("    \"average_accuracy\": ${analytics.averageAccuracy},")
            appendLine("    \"current_streak\": ${analytics.studyStreak.currentStreak},")
            appendLine("    \"longest_streak\": ${analytics.studyStreak.longestStreak}")
            appendLine("  },")
            appendLine("  \"modules\": {")
            analytics.moduleAnalytics.entries.forEachIndexed { index, (moduleId, moduleAnalytics) ->
                appendLine("    \"$moduleId\": {")
                appendLine("      \"progress_percentage\": ${moduleAnalytics.progressPercentage},")
                appendLine("      \"total_time_spent_ms\": ${moduleAnalytics.totalTimeSpent},")
                appendLine("      \"total_time_spent_formatted\": \"${formatTime(moduleAnalytics.totalTimeSpent)}\",")
                appendLine("      \"total_sessions\": ${moduleAnalytics.totalSessions},")
                appendLine("      \"average_accuracy\": ${moduleAnalytics.averageAccuracy},")
                appendLine("      \"completion_rate\": ${moduleAnalytics.completionRate},")
                appendLine("      \"best_score\": ${moduleAnalytics.bestScore},")
                appendLine("      \"difficulty_level\": \"${moduleAnalytics.difficultyLevel}\",")
                appendLine("      \"last_accessed\": ${moduleAnalytics.lastAccessed}")
                append("    }")
                if (index < analytics.moduleAnalytics.size - 1) appendLine(",")
                else appendLine()
            }
            appendLine("  },")
            appendLine("  \"achievements\": [")
            analytics.achievements.forEachIndexed { index, achievement ->
                appendLine("    {")
                appendLine("      \"id\": \"${achievement.id}\",")
                appendLine("      \"title\": \"${achievement.title}\",")
                appendLine("      \"description\": \"${achievement.description}\",")
                appendLine("      \"category\": \"${achievement.category}\",")
                appendLine("      \"unlocked_at\": ${achievement.unlockedAt}")
                append("    }")
                if (index < analytics.achievements.size - 1) appendLine(",")
                else appendLine()
            }
            appendLine("  ],")
            appendLine("  \"weekly_progress\": [")
            analytics.weeklyProgress.forEachIndexed { index, progress ->
                appendLine("    {")
                appendLine("      \"date\": \"${progress.date}\",")
                appendLine("      \"study_time_ms\": ${progress.studyTime},")
                appendLine("      \"study_time_formatted\": \"${formatTime(progress.studyTime)}\",")
                appendLine("      \"sessions_completed\": ${progress.sessionsCompleted},")
                appendLine("      \"accuracy\": ${progress.accuracy},")
                appendLine("      \"modules_studied\": [${progress.modulesStudied.joinToString("\", \"") { "\"$it\"" }}]")
                append("    }")
                if (index < analytics.weeklyProgress.size - 1) appendLine(",")
                else appendLine()
            }
            appendLine("  ]")
            appendLine("}")
        }
        
        file.writeText(content)
    }
    
    fun shareAnalytics(file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = when (file.extension) {
                "csv" -> "text/csv"
                "json" -> "application/json"
                else -> "text/plain"
            }
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Adlam Fulfulde Learning Analytics")
            putExtra(Intent.EXTRA_TEXT, "Here are my learning analytics from Adlam Fulfulde app.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share Analytics"))
    }
    
    private fun formatTime(milliseconds: Long): String {
        val hours = milliseconds / 3600000
        val minutes = (milliseconds % 3600000) / 60000
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "<1m"
        }
    }
}