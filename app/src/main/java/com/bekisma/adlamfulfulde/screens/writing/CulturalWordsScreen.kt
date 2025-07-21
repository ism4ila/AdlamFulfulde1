package com.bekisma.adlamfulfulde.screens.writing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import com.bekisma.adlamfulfulde.data.cultural.FulaniWord
import com.bekisma.adlamfulfulde.data.cultural.ExpandedFulaniWords
import com.bekisma.adlamfulfulde.data.cultural.WordCategory
import com.bekisma.adlamfulfulde.screens.PaintAttributes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CulturalWordsScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf(WordCategory.GREETINGS) }
    var currentWordIndex by remember { mutableIntStateOf(0) }
    var showMeaning by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var paths by remember { mutableStateOf(listOf<Pair<Path, PaintAttributes>>()) }
    var currentPath by remember { mutableStateOf(Path()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val wordsInCategory = ExpandedFulaniWords.wordsByCategory[selectedCategory] ?: emptyList()
    val currentWord = wordsInCategory.getOrNull(currentWordIndex)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mots culturels", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { showMeaning = !showMeaning }) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = if (showMeaning) "Cacher" else "Afficher signification"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { 
                            if (currentWordIndex > 0) {
                                currentWordIndex--
                                paths = emptyList()
                                currentPath = Path()
                            }
                        },
                        enabled = currentWordIndex > 0
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Mot précédent")
                    }

                    IconButton(onClick = { 
                        paths = emptyList()
                        currentPath = Path()
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Effacer")
                    }

                    IconButton(
                        onClick = { 
                            if (currentWordIndex < wordsInCategory.size - 1) {
                                currentWordIndex++
                                paths = emptyList()
                                currentPath = Path()
                            }
                        },
                        enabled = currentWordIndex < wordsInCategory.size - 1
                    ) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Mot suivant")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BannerAdView(modifier = Modifier.fillMaxWidth())
            
            // Sélecteur de catégorie
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(WordCategory.values().toList()) { category ->
                    FilterChip(
                        onClick = { 
                            selectedCategory = category
                            currentWordIndex = 0
                            paths = emptyList()
                            currentPath = Path()
                        },
                        label = { Text(getCategoryName(category)) },
                        selected = selectedCategory == category
                    )
                }
            }

            currentWord?.let { word ->
                // Informations sur le mot
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Mot ${currentWordIndex + 1}/${wordsInCategory.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = word.pronunciation,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        if (showMeaning) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = word.meaning,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            word.culturalContext?.let { context ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = context,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        // Indicateur de difficulté
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            repeat(3) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            color = if (index < word.difficulty) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                            shape = RoundedCornerShape(50)
                                        )
                                )
                            }
                        }
                    }
                }

                // Zone d'écriture
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        // Support RTL via CompositionLocal si nécessaire
                        .onSizeChanged { size -> canvasSize = Size(size.width.toFloat(), size.height.toFloat()) }
                ) {
                    // Mot en arrière-plan
                    Text(
                        text = word.word,
                        fontSize = 120.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Canvas pour l'écriture
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPath = Path().apply { moveTo(offset.x, offset.y) }
                                    },
                                    onDrag = { change, _ ->
                                        currentPath = Path().apply {
                                            addPath(currentPath)
                                            lineTo(change.position.x, change.position.y)
                                        }
                                    },
                                    onDragEnd = {
                                        if (!currentPath.isEmpty) {
                                            paths = paths + (currentPath to PaintAttributes(Color.Black, 15f, 1f))
                                        }
                                        currentPath = Path()
                                    }
                                )
                            }
                    ) {
                        // Dessiner les tracés précédents
                        paths.forEach { (path, attributes) ->
                            drawPath(
                                path = path,
                                color = attributes.color.copy(alpha = attributes.alpha),
                                style = Stroke(width = attributes.brushSize, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                        }

                        // Dessiner le tracé en cours
                        if (!currentPath.isEmpty) {
                            drawPath(
                                path = currentPath,
                                color = Color.Black,
                                style = Stroke(width = 15f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun getCategoryName(category: WordCategory): String {
    return when (category) {
        WordCategory.FAMILY -> "Famille"
        WordCategory.GREETINGS -> "Salutations"
        WordCategory.ANIMALS -> "Animaux"
        WordCategory.COLORS -> "Couleurs"
        WordCategory.NUMBERS -> "Nombres"
        WordCategory.NATURE -> "Nature"
        WordCategory.FOOD -> "Nourriture"
        WordCategory.BODY_PARTS -> "Corps"
        WordCategory.DAILY_LIFE -> "Quotidien"
        WordCategory.TOOLS -> "Outils"
        WordCategory.ACTIONS -> "Actions"
        WordCategory.EXPRESSIONS -> "Expressions"
    }
}