package com.bekisma.adlamfulfulde.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
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
import com.bekisma.adlamfulfulde.navigation.getBasicsModules
import com.bekisma.adlamfulfulde.navigation.getPracticeModules
import com.bekisma.adlamfulfulde.navigation.getToolsModules
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_DELAY_MS = 300L

@Composable
fun MainScreen(
    drawerState: DrawerState,
    onNavigation: (MenuItem) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    MainScreenContent(
        drawerState = drawerState,
        onNavigation = onNavigation,
        onMenuClick = {
            coroutineScope.launch {
                drawerState.open()
            }
        },
        isTablet = isTablet
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    drawerState: DrawerState,
    onNavigation: (MenuItem) -> Unit,
    onMenuClick: () -> Unit,
    isTablet: Boolean
) {
    Scaffold(
        topBar = {
            CleanTopBar(onMenuClick = onMenuClick)
        },
        bottomBar = {
            if (!isTablet) {
                AppBottomBar()
            }
        }
    ) { paddingValues ->
        MainScreenBody(
            paddingValues = paddingValues,
            onNavigation = onNavigation,
            isTablet = isTablet
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CleanTopBar(
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
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
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menu_icon_desc),
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
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
    onNavigation: (MenuItem) -> Unit,
    isTablet: Boolean
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isTablet) {
            TabletLayout(onNavigation = onNavigation)
        } else {
            PhoneLayout(onNavigation = onNavigation)
        }
    }
}

@Composable
private fun TabletLayout(
    onNavigation: (MenuItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            WelcomeCard(onNavigation)
        }
        items(getBasicsModules()) { module -> CleanModuleCard(module) { onNavigation(module) } }
        items(getPracticeModules()) { module -> CleanModuleCard(module) { onNavigation(module) } }
        items(getToolsModules()) { module -> CleanModuleCard(module) { onNavigation(module) } }
    }
}

@Composable
private fun PhoneLayout(
    onNavigation: (MenuItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { WelcomeCard(onNavigation) }
        item { CleanModuleSection(stringResource(R.string.the_basics), getBasicsModules(), onNavigation) }
        item { CleanModuleSection(stringResource(R.string.practice), getPracticeModules(), onNavigation) }
        item { CleanModuleSection(stringResource(R.string.tools), getToolsModules(), onNavigation) }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun WelcomeCard(onStartClick: (MenuItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.welcome_to_adlam),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.start_learning_journey),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            
            Button(
                onClick = {
                    onStartClick(MenuItem(
                        R.drawable.iconapp,
                        R.string.alphabet_learning,
                        R.string.discover_the_adlam_alphabet,
                        "alphabet"
                    ))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.start_learning),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
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
private fun CleanModuleSection(
    title: String,
    modules: List<MenuItem>,
    onModuleClick: (MenuItem) -> Unit
) {
    if (modules.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            modules.forEach { module -> CleanModuleCard(module) { onModuleClick(module) } }
        }
    }
}




