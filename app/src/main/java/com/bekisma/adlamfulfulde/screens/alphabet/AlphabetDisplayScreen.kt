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
    var selectedCategory by remember { mutableStateOf<LetterCategory?>(null) }
    var selectedDifficulty by remember { mutableStateOf<LetterDifficulty?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    
    val filteredLetters = remember(searchQuery, selectedCategory, selectedDifficulty) {
        allLetters.filter { letter ->
            val matchesSearch = searchQuery.isEmpty() || 
                context.getString(letter.displayNameRes).contains(searchQuery, ignoreCase = true) ||
                letter.latinName.contains(searchQuery, ignoreCase = true) ||
                letter.phoneticSound.contains(searchQuery, ignoreCase = true)
                
            val matchesCategory = selectedCategory == null || letter.category == selectedCategory
            val matchesDifficulty = selectedDifficulty == null || letter.difficulty == selectedDifficulty
            
            matchesSearch && matchesCategory && matchesDifficulty
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
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            if (showFilters) Icons.Default.FilterList else Icons.Default.FilterAlt,
                            contentDescription = "Toggle filters",
                            tint = if (selectedCategory != null || selectedDifficulty != null) 
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (showFilters) {
                FilterSection(
                    selectedCategory = selectedCategory,
                    selectedDifficulty = selectedDifficulty,
                    onCategorySelected = { selectedCategory = if (selectedCategory == it) null else it },
                    onDifficultySelected = { selectedDifficulty = if (selectedDifficulty == it) null else it }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Text(
                text = "${filteredLetters.size} letters found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredLetters) { letter ->
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
                            },
                            selectedLabelColor = when (category) {
                                LetterCategory.VOWEL -> Color(0xFFD32F2F)
                                LetterCategory.CONSONANT -> Color(0xFF7B1FA2)
                                LetterCategory.SEMI_VOWEL -> Color(0xFF388E3C)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Difficulty indicator and category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(getDifficultyColor(letter.difficulty))
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (letter.category) {
                            LetterCategory.VOWEL -> Color(0xFFFFEBEE)
                            LetterCategory.CONSONANT -> Color(0xFFF3E5F5)
                            LetterCategory.SEMI_VOWEL -> Color(0xFFE8F5E8)
                        }
                    ),
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        text = when (letter.category) {
                            LetterCategory.VOWEL -> "V"
                            LetterCategory.CONSONANT -> "C"
                            LetterCategory.SEMI_VOWEL -> "S"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = when (letter.category) {
                            LetterCategory.VOWEL -> Color(0xFFD32F2F)
                            LetterCategory.CONSONANT -> Color(0xFF7B1FA2)
                            LetterCategory.SEMI_VOWEL -> Color(0xFF388E3C)
                        }
                    )
                }
            }
            
            // Adlam Letters (Uppercase and Lowercase)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = letter.unicode,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = letter.lowercaseUnicode,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
            
            // Letter name and play button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = letter.displayNameRes),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                IconButton(
                    onClick = onPlaySound,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = stringResource(R.string.play_sound),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
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
                        }
                    ),
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        text = when (letter.category) {
                            LetterCategory.VOWEL -> "Vowel"
                            LetterCategory.CONSONANT -> "Consonant"
                            LetterCategory.SEMI_VOWEL -> "Semi-vowel"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = when (letter.category) {
                            LetterCategory.VOWEL -> Color(0xFFD32F2F)
                            LetterCategory.CONSONANT -> Color(0xFF7B1FA2)
                            LetterCategory.SEMI_VOWEL -> Color(0xFF388E3C)
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
