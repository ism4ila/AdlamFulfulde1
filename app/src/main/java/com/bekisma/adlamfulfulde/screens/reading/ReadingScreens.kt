package com.bekisma.adlamfulfulde.screens.reading

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.data.ReadingPassage
import com.bekisma.adlamfulfulde.data.WordTiming
import com.bekisma.adlamfulfulde.data.getReadingPassages

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingPassageListScreen(navController: NavController) {
    val passages = remember { getReadingPassages() }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredPassages = remember(passages, selectedDifficulty, searchQuery) {
        passages.filter { passage ->
            val matchesDifficulty = selectedDifficulty == null || passage.difficulty == selectedDifficulty
            val matchesSearch = searchQuery.isEmpty() || 
                passage.title.contains(searchQuery, ignoreCase = true)
            matchesDifficulty && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            CleanReadingTopBar(
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { paddingValues ->
        if (passages.isEmpty()) {
            EmptyReadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Search and filters
            SearchAndFilters(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedDifficulty = selectedDifficulty,
                onDifficultySelected = { selectedDifficulty = if (selectedDifficulty == it) null else it },
                modifier = Modifier.padding(16.dp)
            )
            
            // Passages grid
            if (filteredPassages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun passage trouvé",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                ModernPassageGrid(
                    passages = filteredPassages,
                    onPassageClick = { passage ->
                        navController.navigate("reading_player/${passage.id}")
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingPlayerScreen(navController: NavController, passageId: Int?) {
    val passages = remember { getReadingPassages() }
    val passage = remember(passageId) { passages.find { it.id == passageId } }
    val context = LocalContext.current

    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var totalDurationMs by remember { mutableLongStateOf(0L) }
    var currentHighlightedWordIndex by remember { mutableIntStateOf(-1) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var showTranslation by remember { mutableStateOf(false) }

    fun prepareAndPlay() {
        if (passage == null) return
        if (passage.soundResId == R.raw.son_nul) {
            Toast.makeText(context, "Audio non disponible pour ce passage.", Toast.LENGTH_LONG).show()
            return
        }
        try {
            mediaPlayer.reset()
            val afd = context.resources.openRawResourceFd(passage.soundResId)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer.prepare()
            totalDurationMs = mediaPlayer.duration.toLong()
            mediaPlayer.start()
            isPlaying = true
        } catch (e: Exception) {
            isPlaying = false
            Log.e("ReadingPlayerScreen", "Error preparing or playing media: ${e.message}")
            Toast.makeText(context, "Erreur de lecture audio: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun pausePlayback() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
    }

    fun resumePlayback() {
        if (passage != null && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        }
    }

    fun stopPlayback() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset() // Important pour pouvoir le réutiliser
        isPlaying = false
        currentPositionMs = 0L
        currentHighlightedWordIndex = -1
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer.seekTo(positionMs.toInt())
        currentPositionMs = positionMs
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive && isPlaying && mediaPlayer.isPlaying) {
                currentPositionMs = mediaPlayer.currentPosition.toLong()
                // Logique de surlignage (si wordTimings est disponible)
                passage?.wordTimings?.let { timings ->
                    val newIndex = timings.indexOfFirst { timing ->
                        currentPositionMs >= timing.startTimeMs && currentPositionMs < timing.endTimeMs
                    }
                    if (newIndex != currentHighlightedWordIndex) {
                        currentHighlightedWordIndex = newIndex
                    }
                }
                delay(100) // Mettre à jour la position toutes les 100ms
            }
            if (isPlaying && !mediaPlayer.isPlaying) { // S'est terminé naturellement
                isPlaying = false
                currentPositionMs = totalDurationMs // ou mediaPlayer.duration.toLong()
            }
        }
    }

    mediaPlayer.setOnCompletionListener {
        isPlaying = false
        currentPositionMs = totalDurationMs
        currentHighlightedWordIndex = -1
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            ModernPlayerTopBar(
                title = passage?.title ?: "Lecture",
                onBackClick = {
                    stopPlayback()
                    navController.navigateUp()
                },
                showTranslation = showTranslation,
                onToggleTranslation = { showTranslation = !showTranslation },
                playbackSpeed = playbackSpeed,
                onSpeedChanged = { newSpeed ->
                    playbackSpeed = newSpeed
                    if (mediaPlayer.isPlaying) {
                        try {
                            val params = mediaPlayer.playbackParams
                            params.speed = newSpeed
                            mediaPlayer.playbackParams = params
                        } catch (e: Exception) {
                            Log.e("ReadingPlayer", "Error setting playback speed: ${e.message}")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (passage == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.reading_passage_not_found)) // Nouvelle string
            }
            return@Scaffold
        }

        ModernReadingPlayerContent(
            passage = passage,
            showTranslation = showTranslation,
            currentHighlightedWordIndex = currentHighlightedWordIndex,
            isPlaying = isPlaying,
            currentPositionMs = currentPositionMs,
            totalDurationMs = totalDurationMs,
            onSeek = { seekTo(it) },
            onPlayPause = {
                if (isPlaying) pausePlayback() 
                else if (currentPositionMs > 0 && currentPositionMs < totalDurationMs) resumePlayback() 
                else prepareAndPlay()
            },
            onStop = { stopPlayback() },
            onReplay = { stopPlayback(); prepareAndPlay() },
            modifier = Modifier.padding(paddingValues)
        )

    }
}