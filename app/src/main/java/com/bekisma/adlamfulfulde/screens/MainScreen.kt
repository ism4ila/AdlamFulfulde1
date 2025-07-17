package com.bekisma.adlamfulfulde.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlin.math.roundToInt
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import com.bekisma.adlamfulfulde.data.MenuItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_DELAY_MS = 300L
private const val CARD_ANIMATION_DELAY_MS = 50L

@Composable
fun MainScreen(
    drawerState: DrawerState,
    onNavigation: (MenuItem) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var lastVisitedModule by remember { mutableStateOf<MenuItem?>(null) }
    var showStatsDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    
    // Sample progress data - in real app, this would come from a repository
    val moduleProgress = remember {
        mapOf(
            "alphabet" to 0.75f,
            "numbers" to 0.45f,
            "writing" to 0.30f,
            "quiz" to 0.60f,
            "vocabulary_list" to 0.20f,
            "reading_passage_list" to 0.15f,
            "adlam_keyboard" to 0.80f,
            "adlam_transcription" to 0.10f
        )
    }

    LaunchedEffect(Unit) {
        delay(SPLASH_DELAY_MS)
        lastVisitedModule = MenuItem(
            R.drawable.abc64,
            R.string.alphabet_learning,
            R.string.discover_the_adlam_alphabet,
            "alphabet"
        )
        isLoading = false
    }

    AnimatedVisibility(
        visible = !isLoading,
        enter = fadeIn(animationSpec = tween(durationMillis = 400)) +
                slideInVertically(initialOffsetY = { it / 3 }),
        exit = fadeOut()
    ) {
        MainScreenContent(
            drawerState = drawerState,
            lastVisitedModule = lastVisitedModule,
            moduleProgress = moduleProgress,
            onNavigation = {
                lastVisitedModule = it
                onNavigation(it)
            },
            onMenuClick = {
                coroutineScope.launch {
                    drawerState.open()
                }
            },
            onStatsClick = { showStatsDialog = true },
            isTablet = isTablet
        )
        
        if (showStatsDialog) {
            LearningStatsDialog(
                moduleProgress = moduleProgress,
                onDismiss = { showStatsDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    drawerState: DrawerState,
    lastVisitedModule: MenuItem?,
    moduleProgress: Map<String, Float>,
    onNavigation: (MenuItem) -> Unit,
    onMenuClick: () -> Unit,
    onStatsClick: () -> Unit,
    isTablet: Boolean
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                onMenuClick = onMenuClick,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            if (!isTablet) {
                AppBottomBar()
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            MainScreenBody(
                paddingValues = paddingValues,
                lastVisitedModule = lastVisitedModule,
                moduleProgress = moduleProgress,
                onNavigation = onNavigation,
                isTablet = isTablet
            )
            
            // Floating Action Buttons
            QuickActionButtons(
                modifier = Modifier.align(Alignment.BottomEnd),
                onStatsClick = onStatsClick,
                onSearchClick = { /* TODO: Implement search */ },
                paddingValues = paddingValues
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    onMenuClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.learn_adlam_script_ease_short),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menu_icon_desc),
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun AppBottomBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 4.dp
    ) {
        BannerAdView()
    }
}

@Composable
private fun MainScreenBody(
    paddingValues: PaddingValues,
    lastVisitedModule: MenuItem?,
    moduleProgress: Map<String, Float>,
    onNavigation: (MenuItem) -> Unit,
    isTablet: Boolean
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(createBackgroundGradient())
    ) {
        if (isTablet) {
            TabletLayout(
                lastVisitedModule = lastVisitedModule,
                moduleProgress = moduleProgress,
                onNavigation = onNavigation
            )
        } else {
            PhoneLayout(
                lastVisitedModule = lastVisitedModule,
                moduleProgress = moduleProgress,
                onNavigation = onNavigation
            )
        }
    }
}

@Composable
private fun TabletLayout(
    lastVisitedModule: MenuItem?,
    moduleProgress: Map<String, Float>,
    onNavigation: (MenuItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            WelcomeCard(
                onStartClick = {
                    onNavigation(MenuItem(
                        R.drawable.abc64,
                        R.string.alphabet_learning,
                        R.string.discover_the_adlam_alphabet,
                        "alphabet"
                    ))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            LearningProgressCard(
                moduleProgress = moduleProgress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        lastVisitedModule?.let { module ->
            item {
                ResumeCard(
                    module = module,
                    progress = moduleProgress[module.destination] ?: 0f,
                    onClick = { onNavigation(module) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Modules de base
        items(getBasicsModules()) { module ->
            AnimatedModuleCard(
                module = module,
                progress = moduleProgress[module.destination] ?: 0f,
                onClick = { onNavigation(module) },
                isCompact = false
            )
        }

        // Modules de pratique
        items(getPracticeModules()) { module ->
            AnimatedModuleCard(
                module = module,
                progress = moduleProgress[module.destination] ?: 0f,
                onClick = { onNavigation(module) },
                isCompact = false
            )
        }

        // Modules d'outils
        items(getToolsModules()) { module ->
            AnimatedModuleCard(
                module = module,
                progress = moduleProgress[module.destination] ?: 0f,
                onClick = { onNavigation(module) },
                isCompact = false
            )
        }
    }
}

@Composable
private fun PhoneLayout(
    lastVisitedModule: MenuItem?,
    moduleProgress: Map<String, Float>,
    onNavigation: (MenuItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            WelcomeCard(
                onStartClick = {
                    onNavigation(MenuItem(
                        R.drawable.abc64,
                        R.string.alphabet_learning,
                        R.string.discover_the_adlam_alphabet,
                        "alphabet"
                    ))
                }
            )
        }
        
        item {
            LearningProgressCard(
                moduleProgress = moduleProgress
            )
        }

        lastVisitedModule?.let { module ->
            item {
                ResumeCard(
                    module = module,
                    progress = moduleProgress[module.destination] ?: 0f,
                    onClick = { onNavigation(module) }
                )
            }
        }

        item {
            ModuleSection(
                title = stringResource(R.string.the_basics),
                modules = getBasicsModules(),
                moduleProgress = moduleProgress,
                onModuleClick = onNavigation
            )
        }

        item {
            ModuleSection(
                title = stringResource(R.string.practice),
                modules = getPracticeModules(),
                moduleProgress = moduleProgress,
                onModuleClick = onNavigation
            )
        }

        item {
            ModuleSection(
                title = stringResource(R.string.tools),
                modules = getToolsModules(),
                moduleProgress = moduleProgress,
                onModuleClick = onNavigation,
                useGrid = false
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun WelcomeCard(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.start_adlam_journey_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 32.sp
                )

                Text(
                    text = stringResource(R.string.start_adlam_journey_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 24.sp
                )

                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(25.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.get_started),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Logo de l'app en arrière-plan
            Image(
                painter = painterResource(id = R.drawable.abc64),
                contentDescription = stringResource(R.string.featured_image_desc),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterEnd)
                    .graphicsLayer {
                        alpha = 0.3f
                        rotationZ = -10f
                    },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun ResumeCard(
    module: MenuItem,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = module.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.resume_learning),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = stringResource(id = module.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            )
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${(progress * 100).roundToInt()}% complete",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                )
            }

            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ModuleSection(
    title: String,
    modules: List<MenuItem>,
    moduleProgress: Map<String, Float>,
    onModuleClick: (MenuItem) -> Unit,
    useGrid: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        if (useGrid && modules.size > 2) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(((modules.size / 2 + modules.size % 2) * 140).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false
            ) {
                items(modules) { module ->
                    AnimatedModuleCard(
                        module = module,
                        progress = moduleProgress[module.destination] ?: 0f,
                        onClick = { onModuleClick(module) },
                        isCompact = true
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                modules.forEach { module ->
                    AnimatedModuleCard(
                        module = module,
                        progress = moduleProgress[module.destination] ?: 0f,
                        onClick = { onModuleClick(module) },
                        isCompact = false
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedModuleCard(
    module: MenuItem,
    progress: Float,
    onClick: () -> Unit,
    isCompact: Boolean
) {
    var isVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "cardScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        label = "cardElevation"
    )

    LaunchedEffect(Unit) {
        delay(CARD_ANIMATION_DELAY_MS)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
                slideInVertically(initialOffsetY = { it / 4 }),
        exit = fadeOut()
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isCompact) 120.dp else 80.dp)
                .scale(scale)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            if (isCompact) {
                CompactModuleContent(module, progress)
            } else {
                RegularModuleContent(module, progress)
            }
        }
    }
}

@Composable
private fun CompactModuleContent(module: MenuItem, progress: Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = module.imageRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(id = module.titleRes),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        if (progress > 0f) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
                    .background(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            ),
                            center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(progress * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RegularModuleContent(module: MenuItem, progress: Float) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = module.imageRes),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = module.titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(id = module.subtitleRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (progress > 0f) {
                Spacer(modifier = Modifier.height(6.dp))
                
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            )
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (progress > 0f) {
                Text(
                    text = "${(progress * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun createBackgroundGradient(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    )
}

@Composable
private fun LearningProgressCard(
    moduleProgress: Map<String, Float>,
    modifier: Modifier = Modifier
) {
    val totalProgress = moduleProgress.values.average().toFloat()
    val completedModules = moduleProgress.values.count { it >= 1.0f }
    val totalModules = moduleProgress.size
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Learning Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$completedModules/$totalModules",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Overall progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(totalProgress)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${(totalProgress * 100).roundToInt()}% Overall Progress",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun QuickActionButtons(
    modifier: Modifier = Modifier,
    onStatsClick: () -> Unit,
    onSearchClick: () -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        modifier = modifier.padding(
            end = 16.dp,
            bottom = 16.dp + paddingValues.calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Stats FAB
        FloatingActionButton(
            onClick = onStatsClick,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = "View Statistics",
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Search FAB
        FloatingActionButton(
            onClick = onSearchClick,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search Modules",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LearningStatsDialog(
    moduleProgress: Map<String, Float>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Learning Statistics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    val totalProgress = moduleProgress.values.average().toFloat()
                    val completedModules = moduleProgress.values.count { it >= 1.0f }
                    val inProgressModules = moduleProgress.values.count { it > 0f && it < 1.0f }
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Overall Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Completed: $completedModules modules")
                            Text(text = "In Progress: $inProgressModules modules")
                            Text(text = "Total: ${(totalProgress * 100).roundToInt()}%")
                        }
                    }
                }
                
                items(moduleProgress.entries.sortedByDescending { it.value }) { (destination, progress) ->
                    val moduleName = when (destination) {
                        "alphabet" -> "Alphabet Learning"
                        "numbers" -> "Numbers"
                        "writing" -> "Writing Practice"
                        "quiz" -> "Quiz"
                        "vocabulary_list" -> "Vocabulary"
                        "reading_passage_list" -> "Reading"
                        "adlam_keyboard" -> "Adlam Keyboard"
                        "adlam_transcription" -> "Transcription"
                        else -> destination.capitalize()
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = moduleName,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progress)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.secondary
                                            )
                                        )
                                    )
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "${(progress * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// Fonctions utilitaires pour organiser les modules
private fun getBasicsModules(): List<MenuItem> = listOf(
    MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "alphabet"),
    MenuItem(R.drawable.number, R.string.numbers, R.string.practice_adlam_numbers, "numbers")
)

private fun getPracticeModules(): List<MenuItem> = listOf(
    MenuItem(R.drawable.writing, R.string.learn_to_write, R.string.improve_your_writing_skills, "writing"),
    MenuItem(R.drawable.quiz, R.string.quiz, R.string.test_your_knowledge, "quiz"),
    MenuItem(R.drawable.abc_24, R.string.vocabulary_module_title, R.string.vocabulary_module_subtitle, "vocabulary_list")
)

private fun getToolsModules(): List<MenuItem> = listOf(
    MenuItem(R.drawable.numbered_24, R.string.reading_module_title, R.string.reading_module_subtitle, "reading_passage_list"),
    MenuItem(R.drawable.keyboard_icon, R.string.adlam_keyboard, R.string.adlam_keyboard_subtitle, "adlam_keyboard"),
    MenuItem(R.drawable.translate_icon, R.string.adlam_transcription, R.string.adlam_transcription_subtitle, "adlam_transcription")
)