package com.bekisma.adlamfulfulde.screens.vocabulary

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.data.VocabularyItem
import com.bekisma.adlamfulfulde.ui.components.PremiumBadge
import com.bekisma.adlamfulfulde.ui.components.BadgeSize
import com.bekisma.adlamfulfulde.viewmodel.VocabularyViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyQuizScreen(
    onNavigateBack: () -> Unit,
    vocabularyViewModel: VocabularyViewModel = viewModel()
) {
    var showQuizTypeSelector by remember { mutableStateOf(true) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var showFinalResults by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Vocabulary Quiz")
                        Spacer(modifier = Modifier.width(8.dp))
                        PremiumBadge(size = BadgeSize.SMALL)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!showQuizTypeSelector && !showFinalResults) {
                        Text(
                            text = "${currentQuestionIndex + 1}/10",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (showQuizTypeSelector) {
                item {
                    QuizTypeSelector(
                        onQuizTypeSelected = {
                            showQuizTypeSelector = false
                            currentQuestionIndex = 0
                        }
                    )
                }
            } else if (showFinalResults) {
                item {
                    QuizResults(
                        onRetakeQuiz = {
                            showFinalResults = false
                            showQuizTypeSelector = true
                            currentQuestionIndex = 0
                        },
                        onFinish = onNavigateBack
                    )
                }
            } else {
                item {
                    QuizQuestionCard(
                        question = "What is the translation of 'Adlam'?",
                        options = listOf("Alphabet", "Language", "Culture", "Book"),
                        selectedAnswer = selectedAnswer,
                        showResult = showResult,
                        isAnswerCorrect = isAnswerCorrect,
                        onAnswerSelected = { answer ->
                            selectedAnswer = answer
                            isAnswerCorrect = answer == "Alphabet"
                            showResult = true
                        },
                        onNextQuestion = {
                            if (currentQuestionIndex < 9) {
                                currentQuestionIndex++
                                selectedAnswer = null
                                showResult = false
                            } else {
                                showFinalResults = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuizTypeSelector(
    onQuizTypeSelected: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Choose Quiz Type",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = onQuizTypeSelected,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Start Quiz",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuizQuestionCard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    showResult: Boolean,
    isAnswerCorrect: Boolean,
    onAnswerSelected: (String) -> Unit,
    onNextQuestion: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(30) }

    LaunchedEffect(question) {
        timeLeft = 30
        while (timeLeft > 0 && !showResult) {
            delay(1000)
            timeLeft--
        }
        if (timeLeft == 0 && !showResult) {
            onAnswerSelected("")
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!showResult) {
            LinearProgressIndicator(
                progress = timeLeft / 30f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                color = if (timeLeft <= 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                AnswerOption(
                    text = option,
                    isSelected = selectedAnswer == option,
                    showResult = showResult,
                    isCorrect = option == "Alphabet",
                    onClick = { 
                        if (!showResult) {
                            onAnswerSelected(option)
                        }
                    }
                )
            }
        }
        if (showResult) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAnswerCorrect) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAnswerCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isAnswerCorrect) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAnswerCorrect) "Correct!" else "Incorrect",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNextQuestion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next Question")
            }
        }
    }
}

@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    showResult: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        showResult && isCorrect -> MaterialTheme.colorScheme.primaryContainer
        showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
        isSelected -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        showResult && isCorrect -> MaterialTheme.colorScheme.primary
        showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.outline
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            if (showResult && isCorrect) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Correct",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else if (showResult && isSelected && !isCorrect) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Incorrect",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun QuizResults(
    onRetakeQuiz: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Quiz Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onRetakeQuiz,
                modifier = Modifier.weight(1f)
            ) {
                Text("Try Again")
            }
            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f)
            ) {
                Text("Finish")
            }
        }
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
