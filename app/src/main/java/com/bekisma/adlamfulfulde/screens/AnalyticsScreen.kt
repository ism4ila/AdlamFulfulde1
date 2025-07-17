package com.bekisma.adlamfulfulde.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.data.analytics.AnalyticsManager
import com.bekisma.adlamfulfulde.data.analytics.LearningAnalytics
import com.bekisma.adlamfulfulde.data.analytics.ModuleAnalytics
import com.bekisma.adlamfulfulde.data.analytics.Achievement
import com.bekisma.adlamfulfulde.data.analytics.AchievementCategory
import com.bekisma.adlamfulfulde.ProManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    analyticsManager: AnalyticsManager = AnalyticsManager(LocalContext.current),
    proManager: ProManager = ProManager(LocalContext.current)
) {
    val analytics by analyticsManager.analytics.collectAsState()
    val isProUser by proManager.isProUser.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Analytics")
                        if (isProUser) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_crown),
                                contentDescription = "Pro",
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(start = 8.dp),
                                tint = Color(0xFFFFD700)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isProUser) {
                        IconButton(onClick = { 
                            // Export analytics data
                            val data = analyticsManager.exportAnalyticsData()
                            // TODO: Implement file sharing
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(onClick = { 
                            // Export to file
                            val data = analyticsManager.exportAnalyticsData()
                            // TODO: Implement file export
                        }) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Export")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overall Statistics
            item {
                OverallStatsCard(analytics = analytics)
            }
            
            // Study Streak
            item {
                StudyStreakCard(analytics = analytics)
            }
            
            // Module Analytics (Premium Feature)
            if (isProUser) {
                item {
                    Text(
                        text = "Module Progress",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(analytics.moduleAnalytics.toList()) { (moduleId, moduleAnalytics) ->
                    ModuleAnalyticsCard(
                        moduleId = moduleId,
                        analytics = moduleAnalytics
                    )
                }
                
                // Achievements
                item {
                    Text(
                        text = "Achievements",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                
                items(analytics.achievements) { achievement ->
                    AchievementCard(achievement = achievement)
                }
            } else {
                // Premium upgrade prompt
                item {
                    PremiumAnalyticsPrompt()
                }
            }
        }
    }
}

@Composable
fun OverallStatsCard(analytics: LearningAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Overall Statistics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    title = "Study Time",
                    value = formatTime(analytics.totalStudyTime),
                    icon = R.drawable.ic_clock
                )
                StatItem(
                    title = "Sessions",
                    value = analytics.totalSessions.toString(),
                    icon = R.drawable.ic_session
                )
                StatItem(
                    title = "Accuracy",
                    value = "${String.format("%.1f", analytics.averageAccuracy * 100)}%",
                    icon = R.drawable.ic_accuracy
                )
            }
        }
    }
}

@Composable
fun StudyStreakCard(analytics: LearningAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Study Streak",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${analytics.studyStreak.currentStreak}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Current",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${analytics.studyStreak.longestStreak}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Best",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleAnalyticsCard(moduleId: String, analytics: ModuleAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = moduleId.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress circle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    progress = analytics.progressPercentage,
                    modifier = Modifier.size(60.dp),
                    strokeWidth = 6.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "${String.format("%.1f", analytics.progressPercentage * 100)}% Complete",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Difficulty: ${analytics.difficultyLevel}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    title = "Time",
                    value = formatTime(analytics.totalTimeSpent),
                    icon = R.drawable.ic_clock
                )
                StatItem(
                    title = "Sessions",
                    value = analytics.totalSessions.toString(),
                    icon = R.drawable.ic_session
                )
                StatItem(
                    title = "Best Score",
                    value = "${analytics.bestScore}",
                    icon = R.drawable.ic_best_score
                )
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (achievement.category) {
                AchievementCategory.STREAK -> Color(0xFFE3F2FD)
                AchievementCategory.ACCURACY -> Color(0xFFE8F5E8)
                AchievementCategory.TIME_SPENT -> Color(0xFFFFF3E0)
                AchievementCategory.COMPLETION -> Color(0xFFF3E5F5)
                AchievementCategory.MILESTONE -> Color(0xFFFFEBEE)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = achievement.iconRes),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatItem(title: String, value: String, icon: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PremiumAnalyticsPrompt() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_crown),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFFFFD700)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Unlock Premium Analytics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Get detailed module progress, achievements, and export your learning data",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* Navigate to pro upgrade */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upgrade to Pro")
            }
        }
    }
}

fun formatTime(milliseconds: Long): String {
    val hours = milliseconds / 3600000
    val minutes = (milliseconds % 3600000) / 60000
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "<1m"
    }
}