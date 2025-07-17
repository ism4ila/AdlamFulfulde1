package com.bekisma.adlamfulfulde.screens.vocabulary

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FlashcardScreen(
    onNavigateBack: () -> Unit,
    vocabularyViewModel: VocabularyViewModel = viewModel()
) {
    val vocabularyList by vocabularyViewModel.vocabularyList.collectAsState()
    var currentCardIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var sessionStarted by remember { mutableStateOf(false) }
    var sessionCompleted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Flashcards")
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
                    if (sessionStarted && !sessionCompleted) {
                        Text(
                            text = "${currentCardIndex + 1}/${vocabularyList.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (!sessionStarted) {
                StudySessionSetup(
                    dueCardsCount = vocabularyList.size,
                    onStartSession = { sessionStarted = true }
                )
            } else if (sessionCompleted) {
                StudySessionSummary(
                    onContinueStudying = {
                        sessionCompleted = false
                        currentCardIndex = 0
                        isFlipped = false
                    },
                    onFinishSession = onNavigateBack
                )
            } else if (currentCardIndex < vocabularyList.size) {
                FlashcardContent(
                    card = vocabularyList[currentCardIndex],
                    isFlipped = isFlipped,
                    onFlip = { isFlipped = !isFlipped },
                    onDifficultySelected = {
                        if (currentCardIndex < vocabularyList.size - 1) {
                            currentCardIndex++
                            isFlipped = false
                        } else {
                            sessionCompleted = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun StudySessionSetup(
    dueCardsCount: Int,
    onStartSession: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Quiz,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ready to Study!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (dueCardsCount > 0) {
                        "You have $dueCardsCount cards to review"
                    } else {
                        "All caught up! Great job!"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Button(
            onClick = onStartSession,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = dueCardsCount > 0
        ) {
            Text(
                text = if (dueCardsCount > 0) "Start Studying" else "No Cards Due",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FlashcardContent(
    card: VocabularyItem,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onDifficultySelected: (Any) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clickable { onFlip() },
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = isFlipped,
                    transitionSpec = {
                        slideInHorizontally { it } with slideOutHorizontally { -it }
                    }
                ) { flipped ->
                    if (!flipped) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            card.category?.let {
                                Surface(
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            Text(
                                text = card.adlamWord,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Tap to reveal translation",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = card.translation,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            card.exampleSentenceAdlam?.let { example ->
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = example,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                card.exampleSentenceTranslation?.let { translation ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = translation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            card.imageResId?.let { imageRes ->
                                Spacer(modifier = Modifier.height(16.dp))
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedVisibility(
            visible = isFlipped,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Column {
                Text(
                    text = "How well did you know this word?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DifficultyButton(
                        text = "Again",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                        onClick = { onDifficultySelected(1) }
                    )
                    DifficultyButton(
                        text = "Hard",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        onClick = { onDifficultySelected(2) }
                    )
                    DifficultyButton(
                        text = "Good",
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f),
                        onClick = { onDifficultySelected(3) }
                    )
                    DifficultyButton(
                        text = "Easy",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f),
                        onClick = { onDifficultySelected(4) }
                    )
                }
            }
        }
    }
}

@Composable
fun DifficultyButton(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StudySessionSummary(
    onContinueStudying: () -> Unit,
    onFinishSession: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Session Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Great job studying!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onContinueStudying,
                modifier = Modifier.weight(1f)
            ) {
                Text("Continue")
            }
            Button(
                onClick = onFinishSession,
                modifier = Modifier.weight(1f)
            ) {
                Text("Finish")
            }
        }
    }
}
