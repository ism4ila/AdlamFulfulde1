package com.bekisma.adlamfulfulde.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllablesReadingScreen(navController: NavController) {
    // Liste des consonnes Adlam avec leurs noms
    val adlamConsonants = listOf(
        "𞤁" to "Daali", "𞤂" to "Laam", "𞤃" to "Miim", "𞤄" to "Baa", 
        "𞤅" to "Sinnyiiyhe", "𞤆" to "Pulaar", "𞤇" to "Bhe", "𞤈" to "Raa",
        "𞤊" to "Fa", "𞤍" to "U", "𞤎" to "Yhe", "𞤏" to "Waw", 
        "𞤐" to "Nun", "𞤑" to "Kaf", "𞤒" to "Yaa", "𞤔" to "Waw Laabi",
        "𞤕" to "Arre", "𞤖" to "Che", "𞤗" to "Je", "𞤘" to "Tee", 
        "𞤙" to "Nye", "𞤚" to "Gbe", "𞤛" to "Kpokpo"
    )

    // Liste des voyelles Adlam avec leurs noms
    val adlamVowels = listOf(
        "𞤀" to "A", "𞤉" to "E", "𞤋" to "I", "𞤌" to "O", "𞤍" to "U"
    )

    var currentConsonantIndex by remember { mutableStateOf(0) }
    var selectedSyllable by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CleanSyllablesTopBar(
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Current consonant display
            CurrentConsonantCard(
                consonant = adlamConsonants[currentConsonantIndex],
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Syllables grid
            Text(
                text = "Syllabes formées",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SyllablesGrid(
                consonant = adlamConsonants[currentConsonantIndex].first,
                vowels = adlamVowels,
                selectedSyllable = selectedSyllable,
                onSyllableClick = { syllable ->
                    selectedSyllable = if (selectedSyllable == syllable) null else syllable
                },
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Navigation controls
            ModernNavigationControls(
                currentIndex = currentConsonantIndex,
                totalItems = adlamConsonants.size,
                onPrevious = { 
                    if (currentConsonantIndex > 0) {
                        currentConsonantIndex--
                        selectedSyllable = null
                    }
                },
                onNext = { 
                    if (currentConsonantIndex < adlamConsonants.size - 1) {
                        currentConsonantIndex++
                        selectedSyllable = null
                    }
                }
            )
        }
    }
}

// --- Clean UI Components for Syllables ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleanSyllablesTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Syllabes Adlam",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Retour",
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
fun CurrentConsonantCard(
    consonant: Pair<String, String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Consonne actuelle",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = consonant.second,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Text(
                text = consonant.first,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 48.sp
            )
        }
    }
}

@Composable
fun SyllablesGrid(
    consonant: String,
    vowels: List<Pair<String, String>>,
    selectedSyllable: String?,
    onSyllableClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(vowels) { vowel ->
            val syllable = consonant + vowel.first
            SyllableCard(
                syllable = syllable,
                vowelName = vowel.second,
                isSelected = selectedSyllable == syllable,
                onClick = { onSyllableClick(syllable) }
            )
        }
    }
}

@Composable
fun SyllableCard(
    syllable: String,
    vowelName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() }
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = syllable,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onSurface,
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "+ $vowelName",
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) 
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernNavigationControls(
    currentIndex: Int,
    totalItems: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPrevious,
                enabled = currentIndex > 0,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Précédent")
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "${currentIndex + 1} / $totalItems",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / totalItems.toFloat() },
                    modifier = Modifier
                        .width(80.dp)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Button(
                onClick = onNext,
                enabled = currentIndex < totalItems - 1,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Suivant")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SyllablesReadingScreenPreview() {
    val navController = rememberNavController()
    SyllablesReadingScreen(navController)
}
