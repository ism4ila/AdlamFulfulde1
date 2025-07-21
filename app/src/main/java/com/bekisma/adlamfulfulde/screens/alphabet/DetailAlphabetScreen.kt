package com.bekisma.adlamfulfulde.screens.alphabet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.data.alphabet.AdlamLetter
import com.bekisma.adlamfulfulde.data.alphabet.LetterCategory
import com.bekisma.adlamfulfulde.data.alphabet.LetterDifficulty
import com.bekisma.adlamfulfulde.utils.AudioPlayer
import com.bekisma.adlamfulfulde.utils.getDifficultyColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAlphabetScreen(
    letter: String,
    navController: NavController
) {
    val context = LocalContext.current
    val adlamLetter = AdlamLetter.values().firstOrNull { it.unicode == letter }
    val allLetters = AdlamLetter.values().toList()
    val currentIndex = allLetters.indexOfFirst { it.unicode == letter }
    
    var isFavorite by remember { mutableStateOf(false) }
    var showPracticeMode by remember { mutableStateOf(false) }
    var showDescription by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(adlamLetter?.let { stringResource(id = it.displayNameRes) } ?: stringResource(R.string.alphabet_detail))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Add to favorites",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { showPracticeMode = !showPracticeMode }) {
                        Icon(
                            if (showPracticeMode) Icons.Default.VisibilityOff else Icons.Default.Edit,
                            contentDescription = "Practice mode"
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (adlamLetter != null) {
                // Navigation buttons
                NavigationSection(
                    currentIndex = currentIndex,
                    totalLetters = allLetters.size,
                    onPreviousClick = {
                        if (currentIndex > 0) {
                            val previousLetter = allLetters[currentIndex - 1]
                            navController.navigate("DetailAlphabetScreen/${previousLetter.unicode}") {
                                popUpTo("DetailAlphabetScreen/${letter}") { inclusive = true }
                            }
                        }
                    },
                    onNextClick = {
                        if (currentIndex < allLetters.size - 1) {
                            val nextLetter = allLetters[currentIndex + 1]
                            navController.navigate("DetailAlphabetScreen/${nextLetter.unicode}") {
                                popUpTo("DetailAlphabetScreen/${letter}") { inclusive = true }
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Letter Display Section
                LetterDisplaySection(
                    letter = adlamLetter,
                    showPracticeMode = showPracticeMode
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Letter Info Section
                LetterInfoSection(letter = adlamLetter)
                
                Spacer(modifier = Modifier.height(24.dp))

                // Play Sound Button
                OutlinedButton(
                    onClick = {
                        val soundResId = context.resources.getIdentifier(
                            adlamLetter.soundFileName,
                            "raw",
                            context.packageName
                        )
                        if (soundResId != 0) {
                            AudioPlayer.playSound(context, soundResId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.play_sound))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.play_sound),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Description Section
                DescriptionSection(
                    letter = adlamLetter,
                    showDescription = showDescription,
                    onToggleDescription = { showDescription = !showDescription }
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                // Example Word Section
                ExampleWordSection(letter = adlamLetter)
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.letter_not_found),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationSection(
    currentIndex: Int,
    totalLetters: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(
            onClick = onPreviousClick,
            enabled = currentIndex > 0
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous letter")
        }
        
        Text(
            text = "${currentIndex + 1} / $totalLetters",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        FilledTonalIconButton(
            onClick = onNextClick,
            enabled = currentIndex < totalLetters - 1
        ) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next letter")
        }
    }
}

@Composable
fun LetterDisplaySection(
    letter: AdlamLetter,
    showPracticeMode: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showPracticeMode) {
                // Practice mode with tracing guides
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Practice Tracing",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${letter.unicode} ${letter.lowercaseUnicode}",
                            fontSize = 120.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp)
                        )
                    }
                }
            } else {
                // Normal display mode
                Text(
                    text = "${letter.unicode} ${letter.lowercaseUnicode}",
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun LetterInfoSection(letter: AdlamLetter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Letter name and phonetic
            Text(
                text = stringResource(id = letter.displayNameRes),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = letter.phoneticSound,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Letter properties
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PropertyChip(
                    label = when (letter.category) {
                        LetterCategory.VOWEL -> "Vowel"
                        LetterCategory.CONSONANT -> "Consonant" 
                        LetterCategory.SEMI_VOWEL -> "Semi-vowel"
                    },
                    color = when (letter.category) {
                        LetterCategory.VOWEL -> Color(0xFFD32F2F)
                        LetterCategory.CONSONANT -> Color(0xFF7B1FA2)
                        LetterCategory.SEMI_VOWEL -> Color(0xFF388E3C)
                    }
                )
                PropertyChip(
                    label = letter.difficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = getDifficultyColor(letter.difficulty)
                )
            }
        }
    }
}

@Composable
fun PropertyChip(label: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DescriptionSection(
    letter: AdlamLetter,
    showDescription: Boolean,
    onToggleDescription: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Letter Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onToggleDescription) {
                    Icon(
                        if (showDescription) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (showDescription) "Hide description" else "Show description"
                    )
                }
            }
            
            if (showDescription) {
                Divider()
                Text(
                    text = stringResource(id = letter.descriptionRes),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ExampleWordSection(letter: AdlamLetter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = when (letter.category) {
                        LetterCategory.VOWEL -> Color(0xFFD32F2F)
                        LetterCategory.CONSONANT -> Color(0xFF7B1FA2)
                        LetterCategory.SEMI_VOWEL -> Color(0xFF388E3C)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.example_sentence_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (letter.category) {
                        LetterCategory.VOWEL -> Color(0xFFD32F2F)
                        LetterCategory.CONSONANT -> Color(0xFF7B1FA2)
                        LetterCategory.SEMI_VOWEL -> Color(0xFF388E3C)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(id = letter.exampleWordRes),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = letter.exampleWordTranslationRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

