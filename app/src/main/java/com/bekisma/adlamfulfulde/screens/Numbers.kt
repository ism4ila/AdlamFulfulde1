package com.bekisma.adlamfulfulde.screens // Ajuste si nécessaire

import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R // Assure-toi que c'est le bon chemin pour R
import com.bekisma.adlamfulfulde.ads.BannerAdView // Assure-toi que ce composant existe
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme // Ton thème
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive // Importation corrigée
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.random.Random

// --- Data Models ---

/**
 * Modèle de données pour les nombres avec leurs représentations multiples.
 */
data class NumberItem(
    val adlamDigit: String,       // Le chiffre Adlam (𞥑)
    val latinDigit: String,       // Le chiffre Latin (1)
    val fulfuldeLatin: String,    // Le nom en Fulfulde, écriture latine ("Go'o")
    val fulfuldeAdlam: String,    // Le nom en Fulfulde, écriture Adlam ("𞤘𞤮𞥋𞤮")
    val soundId: Int              // Référence audio (R.raw.ad1)
)

/**
 * Options du mode d'affichage en mode Apprentissage.
 */
enum class DisplayMode {
    ADLAM, LATIN, FULFULDE
}

/**
 * Options du mode de l'écran.
 */
enum class ScreenMode {
    LEARNING, QUIZ
}

// --- Quiz Related Data Models ---

data class QuizQuestion(
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String,
    val questionType: QuestionType
)

enum class QuestionType {
    ADLAM_TO_FULFULDE, // Montre Chiffre Adlam, demande Nom Fulfulde (en Adlam)
    LATIN_TO_FULFULDE, // Montre Chiffre Latin, demande Nom Fulfulde (en Adlam)
    FULFULDE_TO_ADLAM  // Montre Nom Fulfulde (en Adlam), demande Chiffre Adlam
}

data class QuizState(
    val questions: List<QuizQuestion>,
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val lastAnswerWasCorrect: Boolean? = null,
    val showFeedback: Boolean = false
)

// --- Main Screen Composable ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumbersScreen(navController: NavController) {
    // Initialize resources and state
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    val hapticFeedback = LocalHapticFeedback.current

    // State variables
    var currentMode by remember { mutableStateOf(ScreenMode.LEARNING) }
    var displayMode by remember { mutableStateOf(DisplayMode.ADLAM) }
    var currentNumberIndex by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Quiz state
    var quizState by remember { mutableStateOf<QuizState?>(null) }
    var showQuizResultDialog by remember { mutableStateOf(false) }

    // Charger les données
    val numberItems = getNumberItems()

    // MediaPlayer cleanup
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    // Initialize or reset quiz state when mode changes
    LaunchedEffect(currentMode, numberItems) {
        if (currentMode == ScreenMode.QUIZ && quizState == null) {
            quizState = generateQuiz(numberItems)
        } else if (currentMode == ScreenMode.LEARNING) {
            quizState = null
            showQuizResultDialog = false
            if (isPlaying) isPlaying = false
        }
    }

    // Show info dialog
    if (showInfoDialog) {
        InfoDialog(onDismiss = { showInfoDialog = false })
    }

    Scaffold(
        topBar = {
            CleanNumbersTopAppBar(
                navController = navController,
                currentMode = currentMode,
                onToggleMode = {
                    currentMode = if (currentMode == ScreenMode.LEARNING) ScreenMode.QUIZ else ScreenMode.LEARNING
                },
                onInfoClick = { showInfoDialog = true }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Mode tabs
                ModeSelector(
                    currentMode = currentMode,
                    onModeChanged = { currentMode = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                when (currentMode) {
                    ScreenMode.LEARNING -> CleanLearningContent(
                        numberItems = numberItems,
                        currentNumberIndex = currentNumberIndex,
                        isPlaying = isPlaying,
                        displayMode = displayMode,
                        onItemClick = { index ->
                            if (isPlaying) isPlaying = false
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            currentNumberIndex = index
                            playSound(context, numberItems[index].soundId, mediaPlayer)
                        },
                        onPlayPauseClick = { isPlaying = !isPlaying },
                        onDisplayModeChanged = { displayMode = it }
                    )
                    ScreenMode.QUIZ -> quizState?.let { state ->
                        CleanQuizContent(
                            quizState = state,
                            onAnswerSelected = { selectedOption ->
                                val currentQuestion = state.questions[state.currentQuestionIndex]
                                val isCorrect = selectedOption == currentQuestion.correctAnswer
                                val nextIndex = state.currentQuestionIndex + 1

                                quizState = state.copy(
                                    score = if (isCorrect) state.score + 1 else state.score,
                                    currentQuestionIndex = nextIndex,
                                    lastAnswerWasCorrect = isCorrect,
                                    showFeedback = true
                                )
                                if (nextIndex >= state.questions.size) {
                                    showQuizResultDialog = true
                                }
                            },
                            onNextQuestion = {
                                if (quizState != null && quizState!!.currentQuestionIndex < quizState!!.questions.size) {
                                    quizState = quizState?.copy(showFeedback = false)
                                }
                            }
                        )
                    } ?: Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
        }
    )

    // Autoplay handler
    if (currentMode == ScreenMode.LEARNING) {
        AutoPlayHandler(
            isPlaying = isPlaying,
            currentNumberIndex = currentNumberIndex,
            numberItems = numberItems,
            mediaPlayer = mediaPlayer,
            context = context,
            autoPlaySpeed = 1.5f,
            updateIndex = { currentNumberIndex = it },
            playSoundFn = { item -> playSound(context, item.soundId, mediaPlayer) }
        )
    }

    // Quiz result dialog
    if (showQuizResultDialog && quizState != null) {
        QuizResultDialog(
            score = quizState!!.score,
            totalQuestions = quizState!!.questions.size,
            onDismiss = {
                showQuizResultDialog = false
                currentMode = ScreenMode.LEARNING
                quizState = null
            },
            onPlayAgain = {
                showQuizResultDialog = false
                quizState = generateQuiz(numberItems)
            }
        )
    }
}

// --- Data Provider ---

/**
 * Retourne la liste des items numériques (0-19) avec transcriptions Adlam.
 * !!! Il est fortement recommandé de faire vérifier les transcriptions 'fulfuldeAdlam' !!!
 */
@Composable
private fun getNumberItems(): List<NumberItem> {
    // Transcriptions Adlam pour les noms Fulfulde (Vérification recommandée)
    return listOf(
        NumberItem("𞥐", "0", "Sifir", "𞤅𞤭𞤬𞤭𞤪", R.raw.ad0),             // Sifir
        NumberItem("𞥑", "1", "Go'o", "𞤘𞤮𞥋𞤮", R.raw.ad1),               // Go'o
        NumberItem("𞥒", "2", "Ɗiɗi", "𞤁𞤭𞤯𞤭", R.raw.ad2),               // Ɗiɗi
        NumberItem("𞥓", "3", "Tati", "𞤚𞤢𞤼𞤭", R.raw.ad3),               // Tati
        NumberItem("𞥔", "4", "Nayi", "𞤐𞤢𞤴𞤭", R.raw.ad4),               // Nayi
        NumberItem("𞥕", "5", "Jowi", "𞤔𞤮𞤱𞤭", R.raw.ad5),               // Jowi
        NumberItem("𞥖", "6", "Jeegom", "𞤔𞤫𞥅𞤺𞤮𞤥", R.raw.ad6),           // Jeegom (long ee)
        NumberItem("𞥗", "7", "Jeeɗiɗi", "𞤔𞤫𞥅𞤯𞤭𞤯𞤭", R.raw.ad7),         // Jeeɗiɗi (long ee, ɗ)
        NumberItem("𞥘", "8", "Jeetati", "𞤔𞤫𞥅𞤼𞤢𞤼𞤭", R.raw.ad8),         // Jeetati (long ee)
        NumberItem("𞥙", "9", "Jeenayi", "𞤔𞤫𞥅𞤲𞤢𞤴𞤭", R.raw.ad9),         // Jeenayi (long ee)
        NumberItem("𞥑𞥐", "10", "Sappo", "𞤅𞤢𞤨𞥆𞤮", R.raw.ad0),           // Sappo (gemination pp)
        NumberItem("𞥑𞥑", "11", "Sappo e go'o", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤘𞤮𞥋𞤮", R.raw.ad1), // Sappo e go'o
        NumberItem("𞥑𞥒", "12", "Sappo e ɗiɗi", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤁𞤭𞤯𞤭", R.raw.ad2), // Sappo e ɗiɗi
        NumberItem("𞥑𞥓", "13", "Sappo e tati", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤚𞤢𞤼𞤭", R.raw.ad3), // Sappo e tati
        NumberItem("𞥑𞥔", "14", "Sappo e nayi", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤐𞤢𞤴𞤭", R.raw.ad4), // Sappo e nayi
        NumberItem("𞥑𞥕", "15", "Sappo e jowi", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤔𞤮𞤱𞤭", R.raw.ad5), // Sappo e jowi
        NumberItem("𞥑𞥖", "16", "Sappo e jeegom", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤔𞤫𞥅𞤺𞤮𞤥", R.raw.ad6), // Sappo e jeegom
        NumberItem("𞥑𞥗", "17", "Sappo e jeeɗiɗi", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤔𞤫𞥅𞤯𞤭𞤯𞤭", R.raw.ad7), // Sappo e jeeɗiɗi
        NumberItem("𞥑𞥘", "18", "Sappo e jeetati", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤔𞤫𞥅𞤼𞤢𞤼𞤭", R.raw.ad8), // Sappo e jeetati
        NumberItem("𞥑𞥙", "19", "Sappo e jeenayi", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤔𞤫𞥅𞤲𞤢𞤴𞤭", R.raw.ad9) // Sappo e jeenayi
    )
}

// --- Learning Mode Content ---

@Composable
fun CleanLearningContent(
    numberItems: List<NumberItem>,
    currentNumberIndex: Int,
    isPlaying: Boolean,
    displayMode: DisplayMode,
    onItemClick: (Int) -> Unit,
    onPlayPauseClick: () -> Unit,
    onDisplayModeChanged: (DisplayMode) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp)
        ) {
            // Current number display
            CleanCurrentNumberDisplay(
                currentItem = numberItems.getOrElse(currentNumberIndex) { numberItems.first() },
                displayMode = displayMode,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            
            // Controls
            LearningControls(
                displayMode = displayMode,
                isPlaying = isPlaying,
                onDisplayModeChanged = onDisplayModeChanged,
                onPlayPauseClick = onPlayPauseClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Numbers grid
            CleanNumbersGrid(
                numberItems = numberItems,
                currentNumberIndex = currentNumberIndex,
                isPlaying = isPlaying,
                displayMode = displayMode,
                onItemClick = onItemClick
            )
        }
        
        BannerAdView(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

// --- Quiz Mode Content ---

/**
 * Génère les questions du quiz en utilisant les transcriptions Adlam.
 */
fun generateQuiz(items: List<NumberItem>, numberOfQuestions: Int = 10): QuizState {
    val questions = mutableListOf<QuizQuestion>()
    if (items.isEmpty()) return QuizState(emptyList())

    val availableItems = items.shuffled()
    val numQuestions = minOf(numberOfQuestions, availableItems.size)

    repeat(numQuestions) { index ->
        val correctItem = availableItems[index]
        val potentialDistractors = availableItems.filter { it != correctItem }
        if (potentialDistractors.size < 3) {
            Log.w("QuizGen", "Not enough unique items for 3 distractors for item: ${correctItem.latinDigit}")
        }
        val distractors = potentialDistractors.shuffled().take(3)
        // Vérifier si on a assez de distracteurs pour former 4 options
        if (distractors.size < 3) {
            Log.w("QuizGen", "Skipping question for ${correctItem.latinDigit} due to lack of distractors.")
            // Ou utiliser moins d'options ? Pour l'instant, on saute.
            return@repeat // Passe à l'itération suivante de repeat
        }


        val questionType = QuestionType.values().random()
        val questionText: String
        val correctAnswer: String
        val options: List<String>

        try {
            when (questionType) {
                QuestionType.ADLAM_TO_FULFULDE -> {
                    questionText = "Comment s'écrit '${correctItem.adlamDigit}' en Fulfulde (Adlam) ?"
                    correctAnswer = correctItem.fulfuldeAdlam
                    options = (distractors.map { it.fulfuldeAdlam } + correctAnswer).shuffled()
                }
                QuestionType.LATIN_TO_FULFULDE -> {
                    questionText = "Comment s'écrit '${correctItem.latinDigit}' en Fulfulde (Adlam) ?"
                    correctAnswer = correctItem.fulfuldeAdlam
                    options = (distractors.map { it.fulfuldeAdlam } + correctAnswer).shuffled()
                }
                QuestionType.FULFULDE_TO_ADLAM -> {
                    questionText = "Quel chiffre Adlam correspond à '${correctItem.fulfuldeAdlam}' ?"
                    correctAnswer = correctItem.adlamDigit
                    options = (distractors.map { it.adlamDigit } + correctAnswer).shuffled()
                }
            }
            if (options.isNotEmpty() && options.size > 1) {
                questions.add(QuizQuestion(questionText, options, correctAnswer, questionType))
            } else {
                Log.w("QuizGen", "Could not generate valid options for item: ${correctItem.latinDigit}")
            }
        } catch (e: Exception) {
            Log.e("QuizGen", "Error generating question for item ${correctItem.latinDigit}", e)
        }
    }
    // S'assurer qu'on a bien généré des questions
    if (questions.isEmpty() && items.isNotEmpty()){
        Log.e("QuizGen", "Failed to generate any quiz questions!")
        // Retourner un état vide ou avec un message d'erreur ?
    }
    return QuizState(questions = questions)
}


@Composable
fun CleanQuizContent(
    quizState: QuizState,
    onAnswerSelected: (String) -> Unit,
    onNextQuestion: () -> Unit
) {
    if (quizState.questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Impossible de générer le quiz pour le moment.",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    if (quizState.currentQuestionIndex >= quizState.questions.size) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val currentQuestion = quizState.questions[quizState.currentQuestionIndex]
    var selectedOption by remember(quizState.currentQuestionIndex) { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator
        QuizProgressIndicator(
            currentQuestion = quizState.currentQuestionIndex + 1,
            totalQuestions = quizState.questions.size,
            score = quizState.score,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Question ${quizState.currentQuestionIndex + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // Answer options
        currentQuestion.options.forEach { option ->
            val isCorrectAnswer = option == currentQuestion.correctAnswer
            val isSelected = option == selectedOption
            
            QuizAnswerCard(
                text = option,
                isSelected = isSelected,
                isCorrect = isCorrectAnswer,
                showFeedback = quizState.showFeedback,
                onClick = { 
                    if (!quizState.showFeedback) { 
                        selectedOption = option
                        onAnswerSelected(option) 
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))

        // Feedback and next button
        AnimatedVisibility(visible = quizState.showFeedback) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val isCorrect = quizState.lastAnswerWasCorrect == true
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect) 
                            Color(0xFF4CAF50).copy(alpha = 0.1f) 
                        else 
                            MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isCorrect) "Correct !" else "Incorrect",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onNextQuestion,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (quizState.currentQuestionIndex < quizState.questions.size - 1) 
                            "Question Suivante" 
                        else 
                            "Voir Résultats"
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun QuizResultDialog(
    score: Int,
    totalQuestions: Int,
    onDismiss: () -> Unit,
    onPlayAgain: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quiz Terminé !") },
        text = {
            Text(
                "Votre score est de $score / $totalQuestions.",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = { Button(onClick = onPlayAgain) { Text("Rejouer") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Retour") } }
    )
}


// --- Top App Bar and Controls ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleanNumbersTopAppBar(
    navController: NavController,
    currentMode: ScreenMode,
    onToggleMode: () -> Unit,
    onInfoClick: () -> Unit
) {
    TopAppBar(
        title = { 
            Text(
                text = stringResource(R.string.numbers_in_adlam),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = { 
            IconButton(onClick = { navController.navigateUp() }) { 
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(onClick = onInfoClick) { 
                Icon(
                    Icons.Default.Info, 
                    contentDescription = stringResource(R.string.info),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun SpeedControlButton(autoPlaySpeed: Float, onClick: () -> Unit, enabled: Boolean = true) {
    IconButton(onClick = onClick, modifier = Modifier.padding(horizontal = 4.dp), enabled = enabled) {
        Text(text = "${autoPlaySpeed}x", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = if (enabled) 1f else 0.5f))
    }
}

@Composable
fun PlayPauseButton(isPlaying: Boolean, onClick: () -> Unit, enabled: Boolean = true) {
    val targetColor = if (isPlaying) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    val animatedColor by animateColorAsState(targetValue = targetColor.copy(alpha = if (enabled) 1f else 0.5f))
    IconButton(onClick = onClick, modifier = Modifier.padding(horizontal = 4.dp).size(40.dp).clip(CircleShape).background(animatedColor), enabled = enabled) {
        Icon(painter = painterResource(id = if (isPlaying) R.drawable.pause else R.drawable.play), contentDescription = stringResource(if (isPlaying) R.string.pause else R.string.play), tint = if (isPlaying) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary, modifier = Modifier.scale(1.1f))
    }
}

@Composable
fun ToggleButton(options: Array<DisplayMode>, selectedOption: DisplayMode, onOptionSelected: (DisplayMode) -> Unit, getLabel: (DisplayMode) -> String, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val selectedColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val selectedTextColor = MaterialTheme.colorScheme.onPrimary
    Row(modifier = modifier.height(36.dp).clip(RoundedCornerShape(18.dp)).background(backgroundColor).alpha(if (enabled) 1f else 0.5f)) {
        options.forEachIndexed { index, option ->
            val isSelected = option == selectedOption
            val startPercent = if (index == 0) 50 else 0; val endPercent = if (index == options.size - 1) 50 else 0
            val shape = RoundedCornerShape(topStartPercent = startPercent, bottomStartPercent = startPercent, topEndPercent = endPercent, bottomEndPercent = endPercent)
            ToggleOption(getLabel(option), isSelected, shape, selectedColor, if (isSelected) selectedTextColor else textColor, { if (enabled) onOptionSelected(option) })
        }
    }
}

@Composable
fun ToggleOption(label: String, isSelected: Boolean, shape: RoundedCornerShape, selectedColor: Color, textColor: Color, onClick: () -> Unit) {
    val animatedBgColor by animateColorAsState(if (isSelected) selectedColor else Color.Transparent)
    Box(modifier = Modifier.widthIn(min = 40.dp).padding(horizontal = 4.dp).fillMaxHeight().clip(shape).background(animatedBgColor).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text = label, color = textColor, style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal), modifier = Modifier.padding(horizontal = 8.dp))
    }
}

// --- Dialogs ---

@Composable
fun SpeedSelectionDialog(currentSpeed: Float, onSpeedSelected: (Float) -> Unit, onDismiss: () -> Unit) {
    val speedOptions = listOf(0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Vitesse de Lecture Auto") },
        text = { Column { speedOptions.forEach { speed -> SpeedOption(speed, speed == currentSpeed) { onSpeedSelected(speed) } } } },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@Composable
fun SpeedOption(speed: Float, isSelected: Boolean, onSelected: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onSelected).padding(vertical = 8.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = isSelected, onClick = onSelected)
        Text(text = "${speed}x", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 16.dp))
    }
}

@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("À propos des chiffres Adlam") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Adlam est une écriture créée dans les années 1980 par deux adolescents, Ibrahima et Abdoulaye Barry, pour écrire la langue Peule (Fulfulde).", style = MaterialTheme.typography.bodyMedium)
                Text("Le système de numération suit le même schéma décimal que les chiffres arabes, mais avec des symboles uniques.", style = MaterialTheme.typography.bodyMedium)
                Text("Cette application vous aide à apprendre les chiffres de 0 à 19.", style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Fermer") } }
    )
}

// --- New Clean UI Components ---

@Composable
fun ModeSelector(
    currentMode: ScreenMode,
    onModeChanged: (ScreenMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            ScreenMode.values().forEach { mode ->
                val isSelected = mode == currentMode
                
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onModeChanged(mode) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (mode) {
                            ScreenMode.LEARNING -> "Apprentissage"
                            ScreenMode.QUIZ -> "Quiz"
                        },
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CleanCurrentNumberDisplay(
    currentItem: NumberItem,
    displayMode: DisplayMode,
    modifier: Modifier = Modifier
) {
    val currentText = when (displayMode) {
        DisplayMode.ADLAM -> currentItem.adlamDigit
        DisplayMode.LATIN -> currentItem.latinDigit
        DisplayMode.FULFULDE -> currentItem.fulfuldeAdlam
    }
    val secondaryText = when (displayMode) {
        DisplayMode.ADLAM -> currentItem.fulfuldeLatin
        DisplayMode.LATIN -> currentItem.fulfuldeLatin
        DisplayMode.FULFULDE -> currentItem.adlamDigit
    }
    
    Card(
        modifier = modifier.height(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = currentText,
                transitionSpec = {
                    (slideInVertically { h -> h } + fadeIn()) togetherWith 
                    (slideOutVertically { h -> -h } + fadeOut()) using 
                    SizeTransform(clip = false)
                }
            ) { text ->
                Text(
                    text = text,
                    fontSize = when {
                        displayMode == DisplayMode.FULFULDE && text.length > 10 -> 32.sp
                        displayMode == DisplayMode.FULFULDE -> 40.sp
                        text.length > 3 -> 56.sp
                        else -> 64.sp
                    },
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
            
            if (secondaryText.isNotBlank() && displayMode != DisplayMode.FULFULDE) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = secondaryText,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun LearningControls(
    displayMode: DisplayMode,
    isPlaying: Boolean,
    onDisplayModeChanged: (DisplayMode) -> Unit,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display mode selector
        CleanDisplayModeSelector(
            displayMode = displayMode,
            onDisplayModeChanged = onDisplayModeChanged,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Play/Pause button
        FloatingActionButton(
            onClick = onPlayPauseClick,
            containerColor = if (isPlaying) 
                MaterialTheme.colorScheme.secondary 
            else 
                MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.pause else R.drawable.play
                ),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = if (isPlaying) 
                    MaterialTheme.colorScheme.onSecondary 
                else 
                    MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CleanDisplayModeSelector(
    displayMode: DisplayMode,
    onDisplayModeChanged: (DisplayMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            DisplayMode.values().forEach { mode ->
                val isSelected = mode == displayMode
                
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDisplayModeChanged(mode) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (mode) {
                            DisplayMode.ADLAM -> "𞥐𞥑"
                            DisplayMode.LATIN -> "01"
                            DisplayMode.FULFULDE -> "Ff"
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CleanNumbersGrid(
    numberItems: List<NumberItem>,
    currentNumberIndex: Int,
    isPlaying: Boolean,
    displayMode: DisplayMode,
    onItemClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(numberItems.size) { index ->
            CleanNumberCard(
                item = numberItems[index],
                isCurrent = index == currentNumberIndex,
                isAutoPlaying = isPlaying && (index == currentNumberIndex),
                displayMode = displayMode,
                onClick = { onItemClick(index) }
            )
        }
    }
}

@Composable
fun CleanNumberCard(
    item: NumberItem,
    isCurrent: Boolean,
    isAutoPlaying: Boolean,
    displayMode: DisplayMode,
    onClick: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isCurrent) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    val pulseAlpha = if (isAutoPlaying) animatePulseEffect(true) else 1f
    
    val backgroundColor = when {
        isAutoPlaying -> MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)
        isCurrent -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    
    val textColor = when {
        isAutoPlaying -> MaterialTheme.colorScheme.onPrimary
        isCurrent -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    val borderStroke = if (isCurrent && !isAutoPlaying) 
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
    else null

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(animatedScale)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrent) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val cardText = when (displayMode) {
                DisplayMode.ADLAM -> item.adlamDigit
                DisplayMode.LATIN -> item.latinDigit
                DisplayMode.FULFULDE -> item.fulfuldeAdlam
            }
            
            val hintText = when (displayMode) {
                DisplayMode.ADLAM -> item.latinDigit
                DisplayMode.LATIN -> item.adlamDigit
                DisplayMode.FULFULDE -> item.latinDigit
            }
            
            Text(
                text = cardText,
                fontSize = when {
                    displayMode == DisplayMode.FULFULDE && cardText.length > 8 -> 18.sp
                    displayMode == DisplayMode.FULFULDE -> 22.sp
                    cardText.length > 2 -> 32.sp
                    else -> 40.sp
                },
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            if (hintText.isNotBlank() && displayMode != DisplayMode.FULFULDE) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = hintText,
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun QuizProgressIndicator(
    currentQuestion: Int,
    totalQuestions: Int,
    score: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Question $currentQuestion/$totalQuestions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { currentQuestion.toFloat() / totalQuestions.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
fun QuizAnswerCard(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showFeedback: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        showFeedback && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.2f)
        showFeedback && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when {
        showFeedback && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }
    
    val textColor = when {
        showFeedback && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier
            .clickable(enabled = !showFeedback) { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
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
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            
            if (showFeedback && (isCorrect || isSelected)) {
                Icon(
                    if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun animatePulseEffect(isActive: Boolean): Float {
    if (!isActive) return 1f
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse alpha"
    )
    return pulseAlpha
}

// --- Audio Playback and Autoplay ---

/**
 * Joue une ressource audio avec gestion d'erreurs robuste.
 */
@Suppress("UNUSED_PARAMETER")
fun playSound(context: Context, soundId: Int, mediaPlayer: MediaPlayer) {
    if (soundId == 0) { Log.w("playSound", "Invalid soundId (0), skipping."); return }
    val uriString = "android.resource://${context.packageName}/$soundId"
    val uri = Uri.parse(uriString)
    try {
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.setDataSource(context, uri)
        mediaPlayer.setOnPreparedListener { player -> try { player.start() } catch (ise: IllegalStateException) { Log.e("MediaPlayer", "Error starting $uriString", ise); try { player.reset() } catch (re: Exception) { Log.e("MediaPlayer", "Reset failed", re) } } }
        mediaPlayer.setOnCompletionListener { player -> /* Optional: player.reset()? */ }
        mediaPlayer.setOnErrorListener { player, what, extra -> Log.e("MediaPlayer", "Error (what=$what, extra=$extra) for $uriString"); try { player.reset() } catch (re: Exception) { Log.e("MediaPlayer", "Reset failed", re) }; true }
        mediaPlayer.prepareAsync()
    } catch (e: Exception) { // Catch IOException, IllegalStateException, etc.
        Log.e("MediaPlayer", "Error setting up sound $uriString", e)
        try { mediaPlayer.reset() } catch (re: Exception) { Log.e("MediaPlayer", "Reset failed", re) }
    }
}


/**
 * Gère la lecture automatique en mode Apprentissage.
 */
@Composable
fun AutoPlayHandler(
    isPlaying: Boolean,
    currentNumberIndex: Int,
    numberItems: List<NumberItem>,
    mediaPlayer: MediaPlayer,
    context: Context,
    autoPlaySpeed: Float,
    updateIndex: (Int) -> Unit,
    playSoundFn: (NumberItem) -> Unit
) {
    LaunchedEffect(isPlaying, autoPlaySpeed) {
        if (isPlaying && numberItems.isNotEmpty()) {
            var internalIndex = currentNumberIndex
            while (isActive && isPlaying) {
                if (internalIndex in numberItems.indices) {
                    val itemToPlay = numberItems[internalIndex]
                    playSoundFn(itemToPlay)
                } else {
                    Log.w("AutoPlayHandler", "Invalid internalIndex: $internalIndex"); break
                }
                val baseDelay = 1500L
                val adjustedDelay = (baseDelay / autoPlaySpeed).toLong().coerceAtLeast(200L)
                delay(adjustedDelay)
                if (isActive && isPlaying) {
                    internalIndex = (internalIndex + 1) % numberItems.size
                    updateIndex(internalIndex)
                }
            }
        } else {
            try { if (mediaPlayer.isPlaying) mediaPlayer.stop(); mediaPlayer.reset() }
            catch (e: Exception) { Log.w("AutoPlayHandler", "MediaPlayer state error during stop/reset: ${e.message}") }
        }
    }
}

// --- Preview ---

@Preview(name = "Light Mode - Learning", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewNumbersScreenLearningLight() {
    val navController = rememberNavController()
    AdlamFulfuldeTheme { NumbersScreen(navController) }
}






@Preview(name = "Light Mode - Quiz", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewNumbersScreenQuiz() {
    val navController = rememberNavController()
    AdlamFulfuldeTheme {
        val sampleItems = getNumberItems().take(4) // Use actual data source for preview consistency
        val previewQuizState = generateQuiz(sampleItems, 4)
        Scaffold( topBar = { /* Mock TopAppBar if needed */ } ) { padding ->
            if (previewQuizState.questions.isNotEmpty()) {
                CleanQuizContent(previewQuizState.copy(showFeedback = false), {}, {})
            } else {
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center){ Text("Could not generate quiz preview.")}
            }
        }
    }
}