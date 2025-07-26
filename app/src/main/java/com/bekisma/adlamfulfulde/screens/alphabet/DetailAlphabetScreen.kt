package com.bekisma.adlamfulfulde.screens.alphabet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.data.alphabet.AdlamLetter
import com.bekisma.adlamfulfulde.utils.AudioPlayer

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
                LetterDisplaySection(letter = adlamLetter)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Letter Info Section
                LetterInfoSection(letter = adlamLetter)
                
                Spacer(modifier = Modifier.height(24.dp))

                // Play Sound Button - Large and child-friendly
                Button(
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
                        .height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow, 
                        contentDescription = stringResource(R.string.play_sound),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        stringResource(R.string.play_sound),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Description Section
                DescriptionSection(letter = adlamLetter)
                
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
fun LetterDisplaySection(letter: AdlamLetter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Simple, large letter display for children
            Text(
                text = "${letter.unicode} ${letter.lowercaseUnicode}",
                fontSize = 140.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
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
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Letter name - simplified for children
            Text(
                text = stringResource(id = letter.displayNameRes),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Phonetic sound - larger and more prominent
            Text(
                text = letter.phoneticSound,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun DescriptionSection(letter: AdlamLetter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "About this letter",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = stringResource(id = letter.descriptionRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun ExampleWordSection(letter: AdlamLetter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Example Word",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(id = letter.exampleWordRes),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(id = letter.exampleWordTranslationRes),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

