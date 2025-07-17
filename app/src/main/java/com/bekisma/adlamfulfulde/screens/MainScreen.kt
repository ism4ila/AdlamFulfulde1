package com.bekisma.adlamfulfulde.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import com.bekisma.adlamfulfulde.data.MenuItem
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_DELAY_MS = 500L
private const val FADE_DURATION_MS = 300
private const val CARD_ANIMATION_DELAY_MS = 100L

@Composable
fun MainScreen(
    drawerState: DrawerState,
    onNavigation: (MenuItem) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var lastVisitedModule by remember { mutableStateOf<MenuItem?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(SPLASH_DELAY_MS)
        lastVisitedModule = MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "alphabet")
        isLoading = false
    }

    AnimatedVisibility(
        visible = !isLoading,
        enter = fadeIn(animationSpec = tween(durationMillis = FADE_DURATION_MS)) + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut()
    ) {
        MainScreenContent(
            drawerState = drawerState,
            lastVisitedModule = lastVisitedModule,
            onNavigation = {
                lastVisitedModule = it
                onNavigation(it)
            },
            onMenuClick = {
                coroutineScope.launch {
                    drawerState.open()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    drawerState: DrawerState,
    lastVisitedModule: MenuItem?,
    onNavigation: (MenuItem) -> Unit,
    onMenuClick: () -> Unit
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
            AppBottomBar()
        }
    ) { paddingValues ->
        MainScreenBody(
            paddingValues = paddingValues,
            lastVisitedModule = lastVisitedModule,
            onNavigation = onNavigation
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    onMenuClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = { AppTitle() },
        navigationIcon = { MenuButton(onClick = onMenuClick) },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun AppTitle() {
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
}

@Composable
private fun MenuButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Default.Menu,
            contentDescription = stringResource(R.string.menu_icon_desc),
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun AppBottomBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        BannerAdView()
    }
}

@Composable
private fun MainScreenBody(
    paddingValues: PaddingValues,
    lastVisitedModule: MenuItem?,
    onNavigation: (MenuItem) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(createBackgroundGradient())
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                WelcomeCard(
                    onStartClick = { onNavigation(MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "alphabet")) }
                )
            }

            lastVisitedModule?.let { module ->
                item {
                    SectionTitle(
                        title = stringResource(R.string.resume_learning),
                        icon = Icons.Default.PlayArrow
                    )
                }
                item { ResumeCard(module = module, onClick = { onNavigation(module) }) }
            }

            item {
                SectionTitle(
                    title = stringResource(R.string.the_basics),
                    icon = Icons.Default.Star
                )
            }
            item {
                ModuleHorizontalList(
                    modules = listOf(
                        MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "alphabet"),
                        MenuItem(R.drawable.number, R.string.numbers, R.string.practice_adlam_numbers, "numbers")
                    ),
                    onModuleClick = onNavigation
                )
            }

            item {
                SectionTitle(
                    title = stringResource(R.string.practice),
                    icon = Icons.Default.Star
                )
            }
            item {
                ModuleHorizontalList(
                    modules = listOf(
                        MenuItem(R.drawable.writing, R.string.learn_to_write, R.string.improve_your_writing_skills, "writing"),
                        MenuItem(R.drawable.quiz, R.string.quiz, R.string.test_your_knowledge, "quiz"),
                        MenuItem(R.drawable.abc_24, R.string.vocabulary_module_title, R.string.vocabulary_module_subtitle, "vocabulary_list")
                    ),
                    onModuleClick = onNavigation
                )
            }

            item {
                SectionTitle(
                    title = stringResource(R.string.tools),
                    icon = Icons.Default.Star
                )
            }
            items(
                listOf(
                    MenuItem(R.drawable.numbered_24, R.string.reading_module_title, R.string.reading_module_subtitle, "reading_passage_list"),
                    MenuItem(R.drawable.keyboard_icon, R.string.adlam_keyboard, R.string.adlam_keyboard_subtitle, "adlam_keyboard"),
                    MenuItem(R.drawable.translate_icon, R.string.adlam_transcription, R.string.adlam_transcription_subtitle, "adlam_transcription")
                )
            ) { module ->
                AnimatedModuleCard(
                    module = module,
                    onClick = { onNavigation(module) }
                )
            }
        }
    }
}

@Composable
private fun WelcomeCard(onStartClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
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
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WelcomeCardContent(onStartClick = onStartClick)
                WelcomeCardImage()
            }
        }
    }
}

@Composable
private fun RowScope.WelcomeCardContent(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(start = 24.dp, end = 12.dp, top = 20.dp, bottom = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.start_adlam_journey_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.start_adlam_journey_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = stringResource(R.string.get_started),
                fontWeight = FontWeight.Medium
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

@Composable
private fun WelcomeCardImage() {
    Box(
        modifier = Modifier
            .padding(end = 20.dp)
            .size(120.dp)
            .clip(CircleShape)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.abc64),
            contentDescription = stringResource(R.string.featured_image_desc),
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer { rotationZ = -5f },
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ResumeCard(
    module: MenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = module.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        id = R.string.resume_learning_title,
                        stringResource(id = module.titleRes)
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = stringResource(
                        id = R.string.resume_learning_subtitle,
                        stringResource(id = module.subtitleRes)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
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
private fun ModuleHorizontalList(
    modules: List<MenuItem>,
    onModuleClick: (MenuItem) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(modules) { module ->
            ModuleCard(
                module = module,
                onClick = { onModuleClick(module) }
            )
        }
    }
}

@Composable
private fun ModuleCard(
    module: MenuItem,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "cardScale"
    )

    ElevatedCard(
        modifier = Modifier
            .width(180.dp)
            .height(200.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = module.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = module.titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = module.subtitleRes),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2
            )
        }
    }
}

@Composable
private fun AnimatedModuleCard(
    module: MenuItem,
    onClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(CARD_ANIMATION_DELAY_MS)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = FADE_DURATION_MS)) + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut()
    ) {
        ElevatedMenuCard(
            image = painterResource(id = module.imageRes),
            title = stringResource(id = module.titleRes),
            subtitle = stringResource(id = module.subtitleRes),
            onClick = onClick
        )
    }
}

@Composable
private fun ElevatedMenuCard(
    image: Painter,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        label = "cardScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 12.dp else 6.dp,
        label = "cardElevation"
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = image,
                    contentDescription = title,
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun createBackgroundGradient(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    AdlamFulfuldeTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        MainScreen(
            drawerState = drawerState,
            onNavigation = { }
        )
    }
}