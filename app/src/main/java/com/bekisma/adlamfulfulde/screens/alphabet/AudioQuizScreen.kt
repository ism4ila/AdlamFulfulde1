package com.bekisma.adlamfulfulde.screens.alphabet

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
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
fun AudioQuizScreen(
    navController: NavController,
    alphabetManager: AdlamAlphabetManager = AdlamAlphabetManager(LocalContext.current)
) {
    val progressState by alphabetManager.progressState.collectAsState()
    val quizState by alphabetManager.currentQuizState.collectAsState()
    
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<AdlamLetter?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var questions by remember { mutableStateOf<List<AudioRecognitionQuestion>>(emptyList()) }
    var showMasteryDialog by remember { mutableStateOf(false) }
    var hasPlayedAudio by remember { mutableStateOf(false) }
    
    val currentLetter = alphabetManager.getCurrentLetter()
    
    // Initialiser les questions au démarrage
    LaunchedEffect(currentLetter) {
        if (currentLetter != null) {
            questions = alphabetManager.startAudioRecognitionQuiz()
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
                            "Reconnaissance Audio - ${stringResource(currentLetter.displayNameRes)}"
                        } else {
                            "Quiz Audio"
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
                    currentPhase = "Phase 2: Reconnaissance Audio",
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            
            // Question actuelle
            if (questions.isNotEmpty() && currentQuestionIndex < questions.size) {
                val currentQuestion = questions[currentQuestionIndex]
                
                AudioQuestionCard(
                    question = currentQuestion,
                    selectedAnswer = selectedAnswer,
                    showFeedback = showFeedback,
                    isCorrect = isCorrect,
                    hasPlayedAudio = hasPlayedAudio,
                    onPlayAudio = { 
                        hasPlayedAudio = true
                        // Ici, on jouerait le fichier audio
                        // Pour l'instant, on simule juste
                    },
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
                        hasPlayedAudio = false
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
        PhaseMasteryDialog(
            letter = currentLetter!!,
            phaseName = "Reconnaissance Audio",
            onContinue = {
                showMasteryDialog = false
                val hasNextPhase = alphabetManager.advanceToNextPhase()
                if (hasNextPhase) {
                    // Passer à la lettre suivante (phase visuelle)
                    navController.popBackStack()
                } else {
                    // Alphabet terminé !
                    navController.popBackStack()
                }
            }
        )
    }
}

@Composable
fun AudioQuestionCard(
    question: AudioRecognitionQuestion,
    selectedAnswer: AdlamLetter?,
    showFeedback: Boolean,
    isCorrect: Boolean,
    hasPlayedAudio: Boolean,
    onPlayAudio: () -> Unit,
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
            // Instruction
            Text(
                text = "Écoutez le son et choisissez la lettre correspondante",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Bouton de lecture audio
            AudioPlayButton(
                isPlayed = hasPlayedAudio,
                onPlay = onPlayAudio,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Affichage du son phonétique après lecture
            if (hasPlayedAudio) {
                Text(
                    text = "Son entendu : ${question.targetLetter.phoneticSound}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            
            // Options de réponse (lettres visuelles)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                question.options.forEach { option ->
                    AudioAnswerOption(
                        letter = option,
                        isSelected = selectedAnswer == option,
                        isCorrect = showFeedback && option == question.correctAnswer,
                        isWrong = showFeedback && selectedAnswer == option && option != question.correctAnswer,
                        enabled = !showFeedback && hasPlayedAudio,
                        onClick = { onAnswerSelected(option) }
                    )
                }
            }
            
            // Feedback
            if (showFeedback) {
                Spacer(modifier = Modifier.height(16.dp))
                
                AudioFeedbackSection(
                    isCorrect = isCorrect,
                    correctAnswer = question.correctAnswer
                )
            }
        }
    }
}

@Composable
fun AudioPlayButton(
    isPlayed: Boolean,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isPlayed) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.primary
    }
    
    val iconColor = if (isPlayed) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onPrimary
    }
    
    Card(
        modifier = modifier
            .size(80.dp)
            .clickable { onPlay() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlayed) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                contentDescription = if (isPlayed) "Audio joué" else "Jouer audio",
                modifier = Modifier.size(32.dp),
                tint = iconColor
            )
        }
    }
}

@Composable
fun AudioAnswerOption(
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
    
    val alpha = if (enabled) 1f else 0.5f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .border(
                width = 2.dp,
                color = borderColor.copy(alpha = alpha),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = alpha)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lettre Adlam
            Text(
                text = letter.unicode,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(letter.displayNameRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                )
                
                Text(
                    text = letter.phoneticSound,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
                )
            }
            
            if (isCorrect || isWrong) {
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
fun AudioFeedbackSection(
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
                        text = "La bonne réponse était : ${correctAnswer.unicode} (${stringResource(correctAnswer.displayNameRes)})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PhaseMasteryDialog(
    letter: AdlamLetter,
    phaseName: String,
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
                    text = "Phase Maîtrisée !",
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
                    text = "Félicitations ! Vous maîtrisez maintenant la $phaseName pour cette lettre.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onContinue
            ) {
                Text("Continuer")
            }
        }
    )
}

@Composable
fun LetterProgressCard(
    letter: AdlamLetter,
    progress: Int,
    total: Int,
    accuracy: Float,
    currentPhase: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Text(
                text = stringResource(letter.displayNameRes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Text(
                text = letter.phoneticSound,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = currentPhase,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium
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
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                StatItem(
                    label = "Précision",
                    value = "${accuracy.toInt()}%",
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}