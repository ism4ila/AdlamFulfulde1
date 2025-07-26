package com.bekisma.adlamfulfulde.screens.alphabet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun AlphabetDisplayScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val allLetters = AdlamLetter.values().toList()
    
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredLetters = remember(searchQuery) {
        allLetters.filter { letter ->
            searchQuery.isEmpty() || 
                context.getString(letter.displayNameRes).contains(searchQuery, ignoreCase = true) ||
                letter.latinName.contains(searchQuery, ignoreCase = true) ||
                letter.phoneticSound.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.alphabet))
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            
            
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allLetters) { letter ->
                    LetterGridCard(
                        letter = letter,
                        onPlaySound = {
                            val soundResId = context.resources.getIdentifier(
                                letter.soundFileName,
                                "raw",
                                context.packageName
                            )
                            if (soundResId != 0) {
                                AudioPlayer.playSound(context, soundResId)
                            } else {
                                // Handle case where sound file is not found
                                // You might want to log this or show a Toast
                            }
                        },
                        onCardClick = { letterUnicode ->
                            navController.navigate("DetailAlphabetScreen/${letterUnicode}")
                        }
                    )
                }
            }
            
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Search letters...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        modifier = modifier,
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun FilterSection(
    selectedCategory: LetterCategory?,
    selectedDifficulty: LetterDifficulty?,
    onCategorySelected: (LetterCategory) -> Unit,
    onDifficultySelected: (LetterDifficulty) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Filter by Category",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LetterCategory.values().forEach { category ->
                    FilterChip(
                        onClick = { onCategorySelected(category) },
                        label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        selected = selectedCategory == category,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (category) {
                                LetterCategory.VOWEL -> Color(0xFFFFEBEE)
                                LetterCategory.CONSONANT -> Color(0xFFF3E5F5)
                                LetterCategory.SEMI_VOWEL -> Color(0xFFE8F5E8)
                                LetterCategory.COMBINED -> Color(0xFFFFF3E0)
                            },
                            selectedLabelColor = when (category) {
                                LetterCategory.VOWEL -> Color(0xFFD32F2F)
                                LetterCategory.CONSONANT -> Color(0xFF7B1FA2)
                                LetterCategory.SEMI_VOWEL -> Color(0xFF388E3C)
                                LetterCategory.COMBINED -> Color(0xFFFF9800)
                            }
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Filter by Difficulty",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LetterDifficulty.values().forEach { difficulty ->
                    FilterChip(
                        onClick = { onDifficultySelected(difficulty) },
                        label = { Text(difficulty.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        selected = selectedDifficulty == difficulty,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = getDifficultyColor(difficulty)
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun LetterGridCard(
    letter: AdlamLetter,
    onPlaySound: () -> Unit,
    onCardClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onCardClick(letter.unicode) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (letter.category) {
                LetterCategory.VOWEL -> Color(0xFFFFE0E6)
                LetterCategory.CONSONANT -> Color(0xFFE8F4FD)
                LetterCategory.SEMI_VOWEL -> Color(0xFFE8F8E8)
                LetterCategory.COMBINED -> Color(0xFFFFF3E0)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Adlam Character (Large and prominent)
            Text(
                text = letter.unicode,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = when (letter.category) {
                    LetterCategory.VOWEL -> Color(0xFFE91E63)
                    LetterCategory.CONSONANT -> Color(0xFF2196F3)
                    LetterCategory.SEMI_VOWEL -> Color(0xFF4CAF50)
                    LetterCategory.COMBINED -> Color(0xFFFF9800)
                },
                textAlign = TextAlign.Center
            )
            
            // Letter name (Simple and clear)
            Text(
                text = stringResource(id = letter.displayNameRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            // Large play button for children
            FilledTonalIconButton(
                onClick = onPlaySound,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = when (letter.category) {
                        LetterCategory.VOWEL -> Color(0xFFE91E63)
                        LetterCategory.CONSONANT -> Color(0xFF2196F3)
                        LetterCategory.SEMI_VOWEL -> Color(0xFF4CAF50)
                        LetterCategory.COMBINED -> Color(0xFFFF9800)
                    }
                )
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.play_sound),
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun LetterDisplayCard(
    letter: AdlamLetter,
    onPlaySound: () -> Unit,
    onCardClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick(letter.unicode) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Difficulty indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(getDifficultyColor(letter.difficulty))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = letter.difficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.weight(1f))
                // Category badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (letter.category) {
                            LetterCategory.VOWEL -> Color(0xFFFFEBEE)
                            LetterCategory.CONSONANT -> Color(0xFFF3E5F5)
                            LetterCategory.SEMI_VOWEL -> Color(0xFFE8F5E8)
                            LetterCategory.COMBINED -> Color(0xFFFFF3E0)
                        }
                    ),
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        text = when (letter.category) {
                            LetterCategory.VOWEL -> "Vowel"
                            LetterCategory.CONSONANT -> "Consonant"
                            LetterCategory.SEMI_VOWEL -> "Semi-vowel"
                            LetterCategory.COMBINED -> "Combined"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = when (letter.category) {
                            LetterCategory.VOWEL -> Color(0xFFD32F2F)
                            LetterCategory.CONSONANT -> Color(0xFF7B1FA2)
                            LetterCategory.SEMI_VOWEL -> Color(0xFF388E3C)
                            LetterCategory.COMBINED -> Color(0xFFFF9800)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Adlam Letters (Uppercase and Lowercase)
                Text(
                    text = "${letter.unicode} ${letter.lowercaseUnicode}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Latin Name and Phonetic Sound
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = letter.displayNameRes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = letter.phoneticSound,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Play Sound Button
                FilledTonalIconButton(
                    onClick = onPlaySound,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = stringResource(R.string.play_sound),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Example Word
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Book,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = when (letter.category) {
                        LetterCategory.VOWEL -> Color(0xFFD32F2F)
                        LetterCategory.CONSONANT -> Color(0xFF7B1FA2)
                        LetterCategory.SEMI_VOWEL -> Color(0xFF388E3C)
                        LetterCategory.COMBINED -> Color(0xFFFF9800)
                    }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.example_sentence_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = when (letter.category) {
                        LetterCategory.VOWEL -> Color(0xFFD32F2F)
                        LetterCategory.CONSONANT -> Color(0xFF7B1FA2)
                        LetterCategory.SEMI_VOWEL -> Color(0xFF388E3C)
                        LetterCategory.COMBINED -> Color(0xFFFF9800)
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = stringResource(id = letter.exampleWordRes),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = letter.exampleWordTranslationRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}
