package com.bekisma.adlamfulfulde.screens.alphabet

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.data.alphabet.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetQuizScreen(
    navController: NavController,
    alphabetManager: AdlamAlphabetManager = AdlamAlphabetManager(LocalContext.current)
) {
    val progressState by alphabetManager.progressState.collectAsState()
    val quizState by alphabetManager.currentQuizState.collectAsState()
    
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<AdlamLetter?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var questions by remember { mutableStateOf<List<VisualRecognitionQuestion>>(emptyList()) }
    var showMasteryDialog by remember { mutableStateOf(false) }
    
    val currentLetter = alphabetManager.getCurrentLetter()
    
    // Initialiser les questions au démarrage
    LaunchedEffect(currentLetter) {
        if (currentLetter != null) {
            questions = alphabetManager.startVisualRecognitionQuiz()
            alphabetManager.resetCurrentQuiz()
        }
    }
    
    // Vérifier la maîtrise après chaque réponse
    LaunchedEffect(quizState.answeredQuestions) {
        if (quizState.answeredQuestions >= 8) {
            val masteryResult = alphabetManager.checkLetterMastery()
            if (masteryResult.isMastered) {
                showMasteryDialog = true
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (currentLetter != null) {
                            "${stringResource(R.string.visual_recognition_phase)} - ${stringResource(currentLetter.displayNameRes)}"
                        } else {
                            stringResource(R.string.alphabet_quiz_title)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barre de progression de l'alphabet
            AlphabetProgressBar(
                current = progressState.masteredLetters.size,
                total = progressState.totalLetters,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Progression de la lettre actuelle
            if (currentLetter != null) {
                LetterProgressCard(
                    letter = currentLetter,
                    progress = quizState.answeredQuestions,
                    total = 10,
                    accuracy = if (quizState.answeredQuestions > 0) {
                        (quizState.correctAnswers.toFloat() / quizState.answeredQuestions.toFloat()) * 100
                    } else 0f,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            
            // Question actuelle
            if (questions.isNotEmpty() && currentQuestionIndex < questions.size) {
                val currentQuestion = questions[currentQuestionIndex]
                
                QuestionCard(
                    question = currentQuestion,
                    selectedAnswer = selectedAnswer,
                    showFeedback = showFeedback,
                    isCorrect = isCorrect,
                    onAnswerSelected = { answer ->
                        selectedAnswer = answer
                        val result = alphabetManager.submitQuizAnswer(
                            currentQuestion.questionNumber,
                            answer
                        )
                        isCorrect = result == AnswerResult.CORRECT
                        showFeedback = true
                    }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bouton suivant
            if (showFeedback && currentQuestionIndex < questions.size - 1) {
                Button(
                    onClick = {
                        currentQuestionIndex++
                        selectedAnswer = null
                        showFeedback = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Question suivante")
                }
            }
        }
    }
    
    // Dialog de maîtrise
    if (showMasteryDialog) {
        MasteryDialog(
            letter = currentLetter!!,
            onContinue = {
                showMasteryDialog = false
                val hasNextPhase = alphabetManager.advanceToNextPhase()
                if (hasNextPhase) {
                    // Passer à la phase audio
                    navController.navigate("audio_quiz")
                } else {
                    // Alphabet terminé !
                    navController.popBackStack()
                }
            }
        )
    }
}

@Composable
fun AlphabetProgressBar(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Progression de l'alphabet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { current.toFloat() / total.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "$current / $total lettres maîtrisées",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LetterProgressCard(
    letter: AdlamLetter,
    progress: Int,
    total: Int,
    accuracy: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Affichage de la lettre
            Text(
                text = letter.unicode,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = stringResource(letter.displayNameRes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = letter.phoneticSound,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Statistiques
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Progrès",
                    value = "$progress/$total",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                StatItem(
                    label = "Précision",
                    value = "${accuracy.toInt()}%",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

@Composable
fun QuestionCard(
    question: VisualRecognitionQuestion,
    selectedAnswer: AdlamLetter?,
    showFeedback: Boolean,
    isCorrect: Boolean,
    onAnswerSelected: (AdlamLetter) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Affichage de la lettre à identifier
            Text(
                text = stringResource(R.string.question_prompt),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Grande lettre à identifier
            Text(
                text = question.targetLetter.unicode,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Options de réponse
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                question.options.forEach { option ->
                    AnswerOption(
                        letter = option,
                        isSelected = selectedAnswer == option,
                        isCorrect = showFeedback && option == question.correctAnswer,
                        isWrong = showFeedback && selectedAnswer == option && option != question.correctAnswer,
                        enabled = !showFeedback,
                        onClick = { onAnswerSelected(option) }
                    )
                }
            }
            
            // Feedback
            if (showFeedback) {
                Spacer(modifier = Modifier.height(16.dp))
                
                FeedbackSection(
                    isCorrect = isCorrect,
                    correctAnswer = question.correctAnswer
                )
            }
        }
    }
}

@Composable
fun AnswerOption(
    letter: AdlamLetter,
    isSelected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.2f)
        isWrong -> Color(0xFFFF5252).copy(alpha = 0.2f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when {
        isCorrect -> Color(0xFF4CAF50)
        isWrong -> Color(0xFFFF5252)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(letter.displayNameRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = letter.phoneticSound,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (isCorrect || isWrong) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFFF5252)
                )
            }
        }
    }
}

@Composable
fun FeedbackSection(
    isCorrect: Boolean,
    correctAnswer: AdlamLetter
) {
    val backgroundColor = if (isCorrect) {
        Color(0xFF4CAF50).copy(alpha = 0.1f)
    } else {
        Color(0xFFFF5252).copy(alpha = 0.1f)
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFFF5252),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = if (isCorrect) "Correct !" else "Incorrect",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFFF5252)
                )
                
                if (!isCorrect) {
                    Text(
                        text = "La bonne réponse est : ${stringResource(correctAnswer.displayNameRes)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun MasteryDialog(
    letter: AdlamLetter,
    onContinue: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.letter_mastered),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = letter.unicode,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = stringResource(letter.displayNameRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = letter.phoneticSound,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Félicitations ! Vous maîtrisez la reconnaissance visuelle. Passons maintenant à la reconnaissance audio !",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onContinue
            ) {
                Text("Phase Audio")
            }
        }
    )
}