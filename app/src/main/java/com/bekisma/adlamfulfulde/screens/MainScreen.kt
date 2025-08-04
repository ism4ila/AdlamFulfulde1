package com.bekisma.adlamfulfulde.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.isSystemInDarkTheme
import com.bekisma.adlamfulfulde.ads.StickyTopBannerAd
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.data.MenuItem
import com.bekisma.adlamfulfulde.navigation.getBasicsModules
import com.bekisma.adlamfulfulde.navigation.getPracticeModules
import com.bekisma.adlamfulfulde.navigation.getToolsModules
import com.bekisma.adlamfulfulde.viewmodel.MainScreenViewModel
import com.bekisma.adlamfulfulde.viewmodel.MainScreenUiState
import com.bekisma.adlamfulfulde.ui.components.AccessibleCard
import com.bekisma.adlamfulfulde.ui.components.AccessibleModuleCard
import com.bekisma.adlamfulfulde.ui.components.AccessibleLearningCard
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_DELAY_MS = 300L

@Composable
fun MainScreen(
    drawerState: DrawerState,
    onNavigation: (MenuItem) -> Unit,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val uiState by viewModel.uiState.collectAsState()
    
    // Update device type and theme in ViewModel
    LaunchedEffect(configuration.screenWidthDp) {
        val isTablet = configuration.screenWidthDp >= 600
        viewModel.updateDeviceType(isTablet)
    }
    
    val isDarkTheme = isSystemInDarkTheme()
    LaunchedEffect(isDarkTheme) {
        viewModel.updateTheme(isDarkTheme)
    }

    // Stable callback to avoid recomposition - using remember(key1) for proper dependency tracking
    val onMenuClick: () -> Unit = remember(drawerState) {
        {
            coroutineScope.launch {
                drawerState.open()
            }
        }
    }
    
    val handleNavigation: (MenuItem) -> Unit = remember(onNavigation, viewModel) {
        { item: MenuItem ->
            viewModel.selectModule(item)
            try {
                onNavigation(item)
            } catch (e: Exception) {
                viewModel.setError("Navigation failed: ${e.message}")
            }
        }
    }

    AdaptiveMainScreenContent(
        uiState = uiState,
        drawerState = drawerState,
        onNavigation = handleNavigation,
        onMenuClick = onMenuClick,
        onErrorDismiss = { viewModel.clearError() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdaptiveMainScreenContent(
    uiState: MainScreenUiState,
    drawerState: DrawerState,
    onNavigation: (MenuItem) -> Unit,
    onMenuClick: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Error handling
        uiState.errorMessage?.let { errorMessage ->
            LaunchedEffect(errorMessage) {
                // Could show a snackbar or toast here
                onErrorDismiss()
            }
        }
        
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    AdaptiveTopBar(onMenuClick = onMenuClick, isDarkTheme = uiState.isDarkTheme)
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                val modifiedPadding = PaddingValues(
                    start = paddingValues.calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                    top = paddingValues.calculateTopPadding() + 60.dp, // Espace pour bannière du haut
                    end = paddingValues.calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                    bottom = paddingValues.calculateBottomPadding()
                )
                
                if (uiState.isTablet) {
                    TabletMainScreenBody(
                        paddingValues = modifiedPadding,
                        onNavigation = onNavigation,
                        isDarkTheme = uiState.isDarkTheme,
                        learningProgress = uiState.learningProgress
                    )
                } else {
                    PhoneMainScreenBody(
                        paddingValues = modifiedPadding,
                        onNavigation = onNavigation,
                        isDarkTheme = uiState.isDarkTheme,
                        learningProgress = uiState.learningProgress
                    )
                }
            }
            
            // Bannière collante en haut
            StickyTopBannerAd(
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdaptiveTopBar(
    onMenuClick: () -> Unit,
    isDarkTheme: Boolean
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App logo/icon - plus grand pour enfants
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = if (isDarkTheme) {
                                    listOf(
                                        Color(0xFF6200EA).copy(alpha = 0.3f),
                                        Color(0xFF3F51B5).copy(alpha = 0.2f)
                                    )
                                } else {
                                    listOf(
                                        Color(0xFFFF6B9D).copy(alpha = 0.3f),
                                        Color(0xFFFFE0E6)
                                    )
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoStories,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = if (isDarkTheme) {
                            Color(0xFF6200EA)
                        } else {
                            Color(0xFFE91E63)
                        }
                    )
                }
                
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menu_icon_desc),
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isDarkTheme) {
                MaterialTheme.colorScheme.surface
            } else {
                Color(0xFFFFFBFF)
            },
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}


@Composable
private fun PhoneMainScreenBody(
    paddingValues: PaddingValues,
    onNavigation: (MenuItem) -> Unit,
    isDarkTheme: Boolean,
    learningProgress: Float = 0.35f
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { 
            AdaptiveWelcomeCard(onNavigation, isDarkTheme = isDarkTheme) 
        }
        
        item { 
            AdaptiveModuleSection(
                title = stringResource(R.string.the_basics), 
                modules = getBasicsModules(), 
                onNavigation = onNavigation,
                color = if (isDarkTheme) Color(0xFF6200EA) else Color(0xFFE91E63),
                isDarkTheme = isDarkTheme,
                icon = Icons.Default.AutoStories
            ) 
        }
        
        item { 
            AdaptiveModuleSection(
                title = stringResource(R.string.practice), 
                modules = getPracticeModules(), 
                onNavigation = onNavigation,
                color = if (isDarkTheme) Color(0xFF03DAC6) else Color(0xFF2196F3),
                isDarkTheme = isDarkTheme,
                icon = Icons.Default.Edit
            ) 
        }
        
        item { 
            AdaptiveModuleSection(
                title = stringResource(R.string.tools), 
                modules = getToolsModules(), 
                onNavigation = onNavigation,
                color = if (isDarkTheme) Color(0xFFFF0266) else Color(0xFF4CAF50),
                isDarkTheme = isDarkTheme,
                icon = Icons.Default.Build
            ) 
        }
    }
}

@Composable
private fun ModernTabletLayout(
    onNavigation: (MenuItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 16.dp,
            bottom = 16.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ModernWelcomeCard(onNavigation)
        }
        
        item(span = { GridItemSpan(maxLineSpan) }) {
            ModernProgressCard()
        }
        
        items(getBasicsModules()) { module ->
            AdaptiveModuleCard(module, MaterialTheme.colorScheme.primary, false) { onNavigation(module) }
        }
        items(getPracticeModules()) { module ->
            AdaptiveModuleCard(module, MaterialTheme.colorScheme.secondary, false) { onNavigation(module) }
        }
        items(getToolsModules()) { module ->
            AdaptiveModuleCard(module, MaterialTheme.colorScheme.tertiary, false) { onNavigation(module) }
        }
    }
}

@Composable
private fun ModernPhoneLayout(
    onNavigation: (MenuItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { 
            ModernWelcomeCard(onNavigation) 
        }
        
        item {
            ModernProgressCard()
        }
        
        item { 
            ModernModuleSection(
                title = stringResource(R.string.the_basics), 
                modules = getBasicsModules(), 
                onNavigation = onNavigation,
                icon = Icons.Default.AutoStories,
                accentColor = MaterialTheme.colorScheme.primary
            ) 
        }
        
        item { 
            ModernModuleSection(
                title = stringResource(R.string.practice), 
                modules = getPracticeModules(), 
                onNavigation = onNavigation,
                icon = Icons.Default.Edit,
                accentColor = MaterialTheme.colorScheme.secondary
            ) 
        }
        
        item { 
            ModernModuleSection(
                title = stringResource(R.string.tools), 
                modules = getToolsModules(), 
                onNavigation = onNavigation,
                icon = Icons.Default.Build,
                accentColor = MaterialTheme.colorScheme.tertiary
            ) 
        }
    }
}

@Composable
private fun EnhancedWelcomeCard(onStartClick: (MenuItem) -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .scale(animatedScale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                            ),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    )
            )
            
            // Subtle glow effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha * 0.1f)
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.welcome_to_adlam),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = stringResource(R.string.start_learning_journey),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        lineHeight = 22.sp
                    )
                }
                
                ElevatedButton(
                    onClick = {
                        onStartClick(MenuItem(
                            R.drawable.iconapp,
                            R.string.alphabet_learning,
                            R.string.discover_the_adlam_alphabet,
                            "alphabet"
                        ))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 10.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.start_learning),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CleanModuleCard(module: MenuItem, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
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
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = module.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
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
            }
            
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
private fun EnhancedModuleSection(
    title: String,
    modules: List<MenuItem>,
    onNavigation: (MenuItem) -> Unit,
    icon: ImageVector
) {
    if (modules.isEmpty()) return
    
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 0f else -90f,
        animationSpec = tween(300)
    )
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Section Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotationAngle),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
        
        // Modules List
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                modules.forEach { module ->
                    EnhancedModuleCard(module) { onNavigation(module) }
                }
            }
        }
    }
}

@Composable
private fun EnhancedModuleCard(module: MenuItem, onClick: () -> Unit = {}) {
    var isHovered by remember { mutableStateOf(false) }
    val elevation by animateFloatAsState(
        targetValue = if (isHovered) 8f else 2f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHovered = true
                        onClick()
                        tryAwaitRelease()
                        isHovered = false
                    }
                )
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Subtle gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
            
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon container with enhanced styling
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = module.imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                // Content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = module.titleRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = stringResource(id = module.subtitleRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Arrow with animation
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(
                        alpha = if (isHovered) 1f else 0.6f
                    ),
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = if (isHovered) 4.dp else 0.dp)
                )
            }
        }
    }
}

@Composable
private fun ModernWelcomeCard(onStartClick: (MenuItem) -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .scale(animatedScale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Dynamic background with shimmer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.primaryContainer
                            ),
                            center = Offset(0.3f, 0.3f)
                        )
                    )
            )
            
            // Subtle shimmer overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = shimmerAlpha * 0.05f),
                                Color.Transparent
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Celebration,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.welcome_to_adlam),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = stringResource(R.string.start_learning_journey),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                        lineHeight = 24.sp
                    )
                }
                
                FilledTonalButton(
                    onClick = {
                        onStartClick(MenuItem(
                            R.drawable.iconapp,
                            R.string.alphabet_learning,
                            R.string.discover_the_adlam_alphabet,
                            "alphabet"
                        ))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.filledTonalButtonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(
                        Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.start_learning),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernProgressCard(
    learningProgress: Float = 0.35f,
    onResumeClick: () -> Unit = {}
) {
    AccessibleLearningCard(
        title = stringResource(R.string.resume_learning),
        subtitle = "Continue your journey with Adlam",
        progress = learningProgress,
        onClick = onResumeClick,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}

@Composable
private fun ModernModuleSection(
    title: String,
    modules: List<MenuItem>,
    onNavigation: (MenuItem) -> Unit,
    icon: ImageVector,
    accentColor: Color
) {
    if (modules.isEmpty()) return
    
    var isExpanded by remember { mutableStateOf(true) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 0f else -90f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "rotation"
    )
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Modern Section Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            colors = CardDefaults.cardColors(
                containerColor = accentColor.copy(alpha = 0.08f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = accentColor
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${modules.size} modules available",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(rotationAngle),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Modules List with modern animation
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) + fadeIn(),
            exit = shrinkVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) + fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                modules.forEach { module ->
                    AccessibleModuleCard(
                        title = stringResource(module.titleRes),
                        subtitle = stringResource(module.subtitleRes),
                        onClick = { onNavigation(module) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(accentColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = module.imageRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        },
                        trailingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TabletMainScreenBody(
    paddingValues: PaddingValues,
    onNavigation: (MenuItem) -> Unit,
    isDarkTheme: Boolean,
    learningProgress: Float = 0.35f
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDarkTheme) {
                        listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.background
                        )
                    } else {
                        listOf(
                            Color(0xFFFFF8E1),
                            Color(0xFFFFFFFF),
                            Color(0xFFF3E5F5)
                        )
                    }
                )
            ),
        contentPadding = PaddingValues(
            start = 32.dp,
            end = 32.dp,
            top = 24.dp,
            bottom = 16.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            AdaptiveWelcomeCard(onNavigation, isDarkTheme = isDarkTheme)
        }
        
        items(getBasicsModules()) { module ->
            AdaptiveModuleCard(module, if (isDarkTheme) Color(0xFF6200EA) else Color(0xFFE91E63), isDarkTheme) { onNavigation(module) }
        }
        items(getPracticeModules()) { module ->
            AdaptiveModuleCard(module, if (isDarkTheme) Color(0xFF03DAC6) else Color(0xFF2196F3), isDarkTheme) { onNavigation(module) }
        }
        items(getToolsModules()) { module ->
            AdaptiveModuleCard(module, if (isDarkTheme) Color(0xFFFF0266) else Color(0xFF4CAF50), isDarkTheme) { onNavigation(module) }
        }
    }
}

@Composable
private fun AdaptiveWelcomeCard(
    onStartClick: (MenuItem) -> Unit,
    isDarkTheme: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "welcome_scale"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_glow")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    AccessibleCard(
        onClick = {
            onStartClick(MenuItem(
                R.drawable.iconapp,
                R.string.alphabet_learning,
                R.string.discover_the_adlam_alphabet,
                "alphabet"
            ))
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .scale(animatedScale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease() 
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(28.dp),
        contentDescription = "${stringResource(R.string.welcome_to_adlam)}, ${stringResource(R.string.start_learning_journey)}",
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                Color(0xFFFFE0E6)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Dynamic gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                if (isDarkTheme) MaterialTheme.colorScheme.primaryContainer else Color(0xFFFFE0E6),
                                if (isDarkTheme) Color(0xFF6200EA).copy(alpha = 0.1f) else Color(0xFFE91E63).copy(alpha = 0.1f),
                                if (isDarkTheme) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f) else Color(0xFFFFE0E6).copy(alpha = 0.7f),
                                if (isDarkTheme) MaterialTheme.colorScheme.primaryContainer else Color(0xFFFFE0E6)
                            ),
                            center = Offset(0.3f, 0.3f)
                        )
                    )
            )
            
            // Subtle shimmer overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = shimmerAlpha * 0.1f),
                                Color.Transparent
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            if (isDarkTheme) Color(0xFF6200EA).copy(alpha = 0.3f) else Color(0xFFE91E63).copy(alpha = 0.3f),
                                            if (isDarkTheme) Color(0xFF6200EA).copy(alpha = 0.1f) else Color(0xFFE91E63).copy(alpha = 0.1f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Celebration,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = if (isDarkTheme) Color(0xFF6200EA) else Color(0xFFE91E63)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.welcome_to_adlam),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFF6200EA) else Color(0xFFE91E63)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = stringResource(R.string.start_learning_journey),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isDarkTheme) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        lineHeight = 24.sp
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (isDarkTheme) Color(0xFF6200EA) else Color(0xFFE91E63)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.start_learning),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color(0xFF6200EA) else Color(0xFFE91E63)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (isDarkTheme) Color(0xFF6200EA) else Color(0xFFE91E63)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdaptiveModuleSection(
    title: String,
    modules: List<MenuItem>,
    onNavigation: (MenuItem) -> Unit,
    color: Color,
    isDarkTheme: Boolean,
    icon: ImageVector
) {
    if (modules.isEmpty()) return
    
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Section Header adaptif
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) {
                    color.copy(alpha = 0.2f)
                } else {
                    color.copy(alpha = 0.1f)
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = color
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text(
                        text = "${modules.size} modules disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Modules List - Simple grid pour enfants
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height((((modules.size + 1) / 2) * 120).dp),
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(modules) { module ->
                AdaptiveModuleCard(module, color, isDarkTheme) { onNavigation(module) }
            }
        }
    }
}

@Composable
private fun AdaptiveModuleCard(
    module: MenuItem,
    color: Color,
    isDarkTheme: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) {
                MaterialTheme.colorScheme.surfaceContainer
            } else {
                color.copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Icône adaptative
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (isDarkTheme) {
                                listOf(
                                    color.copy(alpha = 0.3f),
                                    color.copy(alpha = 0.1f)
                                )
                            } else {
                                listOf(
                                    color.copy(alpha = 0.2f),
                                    color.copy(alpha = 0.1f)
                                )
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = module.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }
            
            // Titre adaptatif
            Text(
                text = stringResource(id = module.titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    color.copy(alpha = 0.9f)
                },
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


