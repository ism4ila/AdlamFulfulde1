package com.bekisma.adlamfulfulde.screens.writing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.screens.AdlamCharacters
import com.bekisma.adlamfulfulde.screens.PaintAttributes
import com.bekisma.adlamfulfulde.screens.WritingType

enum class ComparisonView {
    SIDE_BY_SIDE,  // Côte à côte
    OVERLAY        // Superposition
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonModeScreen(
    navController: NavController,
    writingType: WritingType
) {
    val characters = when (writingType) {
        WritingType.UPPERCASE -> AdlamCharacters.uppercase
        WritingType.LOWERCASE -> AdlamCharacters.lowercase
        WritingType.NUMBERS -> AdlamCharacters.numbers
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var comparisonView by remember { mutableStateOf(ComparisonView.SIDE_BY_SIDE) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var userPaths by remember { mutableStateOf(listOf<Pair<Path, PaintAttributes>>()) }
    var currentPath by remember { mutableStateOf(Path()) }
    var overlayAlpha by remember { mutableFloatStateOf(0.5f) }

    val currentCharacter = characters.getOrNull(currentIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mode Comparaison", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    // Basculer le mode de vue
                    IconButton(onClick = { 
                        comparisonView = if (comparisonView == ComparisonView.SIDE_BY_SIDE) 
                            ComparisonView.OVERLAY else ComparisonView.SIDE_BY_SIDE 
                    }) {
                        Icon(
                            if (comparisonView == ComparisonView.SIDE_BY_SIDE) Icons.Filled.ViewColumn else Icons.Filled.Layers,
                            contentDescription = "Changer mode vue"
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
                            if (currentIndex > 0) {
                                currentIndex--
                                userPaths = emptyList()
                                currentPath = Path()
                            }
                        },
                        enabled = currentIndex > 0
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Précédent")
                    }

                    IconButton(onClick = { 
                        userPaths = emptyList()
                        currentPath = Path()
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Effacer")
                    }

                    IconButton(
                        onClick = { 
                            if (currentIndex < characters.size - 1) {
                                currentIndex++
                                userPaths = emptyList()
                                currentPath = Path()
                            }
                        },
                        enabled = currentIndex < characters.size - 1
                    ) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Suivant")
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
            // Contrôle d'opacité pour le mode superposition
            if (comparisonView == ComparisonView.OVERLAY) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Transparence du modèle: ${(overlayAlpha * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = overlayAlpha,
                            onValueChange = { overlayAlpha = it },
                            valueRange = 0.1f..1f,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            currentCharacter?.let { character ->
                when (comparisonView) {
                    ComparisonView.SIDE_BY_SIDE -> {
                        SideBySideView(
                            character = character,
                            userPaths = userPaths,
                            currentPath = currentPath,
                            onPathsUpdate = { userPaths = it },
                            onCurrentPathUpdate = { currentPath = it }
                        )
                    }
                    ComparisonView.OVERLAY -> {
                        OverlayView(
                            character = character,
                            userPaths = userPaths,
                            currentPath = currentPath,
                            overlayAlpha = overlayAlpha,
                            onPathsUpdate = { userPaths = it },
                            onCurrentPathUpdate = { currentPath = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SideBySideView(
    character: String,
    userPaths: List<Pair<Path, PaintAttributes>>,
    currentPath: Path,
    onPathsUpdate: (List<Pair<Path, PaintAttributes>>) -> Unit,
    onCurrentPathUpdate: (Path) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Côté gauche - Modèle
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Modèle",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = character,
                        fontSize = 120.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Côté droit - Utilisateur
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Votre tracé",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                DrawingCanvas(
                    modifier = Modifier.fillMaxSize(),
                    userPaths = userPaths,
                    currentPath = currentPath,
                    onPathsUpdate = onPathsUpdate,
                    onCurrentPathUpdate = onCurrentPathUpdate,
                    showBackground = false
                )
            }
        }
    }
}

@Composable
private fun OverlayView(
    character: String,
    userPaths: List<Pair<Path, PaintAttributes>>,
    currentPath: Path,
    overlayAlpha: Float,
    onPathsUpdate: (List<Pair<Path, PaintAttributes>>) -> Unit,
    onCurrentPathUpdate: (Path) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DrawingCanvas(
            modifier = Modifier.fillMaxSize(),
            userPaths = userPaths,
            currentPath = currentPath,
            onPathsUpdate = onPathsUpdate,
            onCurrentPathUpdate = onCurrentPathUpdate,
            showBackground = true,
            backgroundCharacter = character,
            backgroundAlpha = overlayAlpha
        )
    }
}

@Composable
private fun DrawingCanvas(
    modifier: Modifier = Modifier,
    userPaths: List<Pair<Path, PaintAttributes>>,
    currentPath: Path,
    onPathsUpdate: (List<Pair<Path, PaintAttributes>>) -> Unit,
    onCurrentPathUpdate: (Path) -> Unit,
    showBackground: Boolean = false,
    backgroundCharacter: String? = null,
    backgroundAlpha: Float = 0.3f
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
    ) {
        // Caractère en arrière-plan si demandé
        if (showBackground && backgroundCharacter != null) {
            Text(
                text = backgroundCharacter,
                fontSize = 200.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary.copy(alpha = backgroundAlpha),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Canvas pour le dessin
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            onCurrentPathUpdate(Path().apply { moveTo(offset.x, offset.y) })
                        },
                        onDrag = { change, _ ->
                            onCurrentPathUpdate(
                                Path().apply {
                                    addPath(currentPath)
                                    lineTo(change.position.x, change.position.y)
                                }
                            )
                        },
                        onDragEnd = {
                            if (!currentPath.isEmpty) {
                                onPathsUpdate(userPaths + (currentPath to PaintAttributes(Color.Red, 15f, 1f)))
                            }
                            onCurrentPathUpdate(Path())
                        }
                    )
                }
        ) {
            // Dessiner les tracés précédents
            userPaths.forEach { (path, attributes) ->
                drawPath(
                    path = path,
                    color = attributes.color.copy(alpha = attributes.alpha),
                    style = Stroke(
                        width = attributes.brushSize, 
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                )
            }

            // Dessiner le tracé en cours
            if (!currentPath.isEmpty) {
                drawPath(
                    path = currentPath,
                    color = Color.Red,
                    style = Stroke(
                        width = 15f, 
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                )
            }
        }
    }
}