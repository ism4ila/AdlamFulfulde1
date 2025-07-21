package com.bekisma.adlamfulfulde.screens.reading

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekisma.adlamfulfulde.data.ReadingPassage

// --- New Clean UI Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleanReadingTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Apprendre à lire",
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
fun EmptyReadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Aucun passage disponible",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Les passages de lecture apparaîtront ici",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SearchAndFilters(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedDifficulty: String?,
    onDifficultySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Rechercher un passage...") },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Rechercher")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Effacer")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        
        // Difficulty filters
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Niveau :",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            
            val difficulties = listOf("Facile", "Moyen", "Difficile")
            difficulties.forEach { difficulty ->
                DifficultyChip(
                    difficulty = difficulty,
                    isSelected = selectedDifficulty == difficulty,
                    onClick = { onDifficultySelected(difficulty) }
                )
            }
        }
    }
}

@Composable
fun DifficultyChip(
    difficulty: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> when (difficulty) {
            "Facile" -> Color(0xFF4CAF50)
            "Moyen" -> Color(0xFFFF9800)
            "Difficile" -> Color(0xFFE53935)
            else -> MaterialTheme.colorScheme.primary
        }
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = when {
        isSelected -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = difficulty,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun ModernPassageGrid(
    passages: List<ReadingPassage>,
    onPassageClick: (ReadingPassage) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(passages, key = { it.id }) { passage ->
            ModernPassageCard(
                passage = passage,
                onClick = { onPassageClick(passage) }
            )
        }
    }
}

@Composable
fun ModernPassageCard(
    passage: ReadingPassage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Filled.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    DifficultyBadge(difficulty = passage.difficulty)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = passage.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = passage.adlamText.take(50) + "...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Audio",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun DifficultyBadge(
    difficulty: String
) {
    val (backgroundColor, textColor) = when (difficulty) {
        "Facile" -> Color(0xFF4CAF50).copy(alpha = 0.1f) to Color(0xFF4CAF50)
        "Moyen" -> Color(0xFFFF9800).copy(alpha = 0.1f) to Color(0xFFFF9800)
        "Difficile" -> Color(0xFFE53935).copy(alpha = 0.1f) to Color(0xFFE53935)
        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = difficulty,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// --- Modern Reading Player Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernPlayerTopBar(
    title: String,
    onBackClick: () -> Unit,
    showTranslation: Boolean,
    onToggleTranslation: () -> Unit,
    playbackSpeed: Float,
    onSpeedChanged: (Float) -> Unit
) {
    var showSpeedMenu by remember { mutableStateOf(false) }
    
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
        actions = {
            // Speed control
            IconButton(onClick = { showSpeedMenu = true }) {
                Text(
                    text = "${playbackSpeed}x",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Translation toggle
            IconButton(onClick = onToggleTranslation) {
                Icon(
                    if (showTranslation) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (showTranslation) "Masquer traduction" else "Afficher traduction",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            if (showSpeedMenu) {
                SpeedSelectionMenu(
                    currentSpeed = playbackSpeed,
                    onSpeedSelected = { speed ->
                        onSpeedChanged(speed)
                        showSpeedMenu = false
                    },
                    onDismiss = { showSpeedMenu = false }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun SpeedSelectionMenu(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss
    ) {
        speeds.forEach { speed ->
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = speed == currentSpeed,
                            onClick = { onSpeedSelected(speed) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${speed}x")
                    }
                },
                onClick = { onSpeedSelected(speed) }
            )
        }
    }
}

@Composable
fun ModernReadingPlayerContent(
    passage: com.bekisma.adlamfulfulde.data.ReadingPassage?,
    showTranslation: Boolean,
    currentHighlightedWordIndex: Int,
    isPlaying: Boolean,
    currentPositionMs: Long,
    totalDurationMs: Long,
    onSeek: (Long) -> Unit,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onReplay: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (passage == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.Error,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Passage non trouvé",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        return
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Text display area
        if (showTranslation) {
            SplitTextView(
                adlamText = passage.adlamText,
                frenchText = passage.frenchTranslation,
                currentHighlightedWordIndex = currentHighlightedWordIndex,
                modifier = Modifier.weight(1f)
            )
        } else {
            SingleTextView(
                text = passage.adlamText,
                currentHighlightedWordIndex = currentHighlightedWordIndex,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Modern audio controls
        ModernAudioControls(
            isPlaying = isPlaying,
            currentPositionMs = currentPositionMs,
            totalDurationMs = totalDurationMs,
            onSeek = onSeek,
            onPlayPause = onPlayPause,
            onStop = onStop,
            onReplay = onReplay
        )
    }
}

@Composable
fun SplitTextView(
    adlamText: String,
    frenchText: String,
    currentHighlightedWordIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Adlam text
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Adlam",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = adlamText,
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 32.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        // French translation
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Français",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = frenchText,
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun SingleTextView(
    text: String,
    currentHighlightedWordIndex: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            style = MaterialTheme.typography.headlineSmall,
            lineHeight = 40.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun ModernAudioControls(
    isPlaying: Boolean,
    currentPositionMs: Long,
    totalDurationMs: Long,
    onSeek: (Long) -> Unit,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onReplay: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress indicator
            if (totalDurationMs > 0) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(currentPositionMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatTime(totalDurationMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Slider(
                        value = currentPositionMs.toFloat(),
                        onValueChange = { newPosition -> onSeek(newPosition.toLong()) },
                        valueRange = 0f..totalDurationMs.toFloat(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Control buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onReplay,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.Replay,
                        contentDescription = "Recommencer",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                FloatingActionButton(
                    onClick = onPlayPause,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                IconButton(
                    onClick = onStop,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.Stop,
                        contentDescription = "Arrêter",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}