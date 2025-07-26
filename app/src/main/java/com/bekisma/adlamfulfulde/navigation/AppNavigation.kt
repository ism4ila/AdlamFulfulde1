package com.bekisma.adlamfulfulde.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bekisma.adlamfulfulde.screens.alphabet.DetailAlphabetScreen
import com.bekisma.adlamfulfulde.screens.SimpleMainScreen
import com.bekisma.adlamfulfulde.screens.AboutScreen
import com.bekisma.adlamfulfulde.screens.NumbersScreen
import com.bekisma.adlamfulfulde.screens.QuizScreen
import com.bekisma.adlamfulfulde.screens.SettingsScreen
import com.bekisma.adlamfulfulde.screens.WritingScreen
import com.bekisma.adlamfulfulde.screens.WritingType
import com.bekisma.adlamfulfulde.screens.WritingPracticeScreen
import com.bekisma.adlamfulfulde.screens.AdlamKeyboardScreen
import com.bekisma.adlamfulfulde.screens.AdlamLatinTranscriptionScreen
import com.bekisma.adlamfulfulde.screens.AnalyticsScreen
import com.bekisma.adlamfulfulde.screens.reading.ReadingPassageListScreen
import com.bekisma.adlamfulfulde.screens.reading.ReadingPlayerScreen
import com.bekisma.adlamfulfulde.ThemeMode
import com.bekisma.adlamfulfulde.ColorTheme
import com.bekisma.adlamfulfulde.data.MenuItem
import com.bekisma.adlamfulfulde.BuildConfig
import com.bekisma.adlamfulfulde.ProManager
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.screens.vocabulary.EnhancedVocabularyListScreen
import com.bekisma.adlamfulfulde.screens.vocabulary.VocabularyDetailScreen
import com.bekisma.adlamfulfulde.screens.writing.CulturalWordsScreen
import com.bekisma.adlamfulfulde.screens.writing.ComparisonModeScreen

// ========================================
// CONSTANTES - Données de l'application
// ========================================

/**
 * Liste des modules d'apprentissage de l'application
 * Organisée par ordre logique d'apprentissage
 */
private val menuItems = listOf(
    // Modules de base
    MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "alphabet"),
    MenuItem(R.drawable.number, R.string.numbers, R.string.practice_adlam_numbers, "numbers"),

    // Modules de pratique
    MenuItem(R.drawable.writing, R.string.learn_to_write, R.string.improve_your_writing_skills, "writing"),
    MenuItem(R.drawable.quiz, R.string.quiz, R.string.test_your_knowledge, "quiz"),

    // Modules d'outils
    MenuItem(R.drawable.numbered_24, R.string.reading_module_title, R.string.reading_module_subtitle, "reading_passage_list"),
    MenuItem(R.drawable.keyboard_icon, R.string.adlam_keyboard, R.string.adlam_keyboard_subtitle, "adlam_keyboard"),
    MenuItem(R.drawable.translate_icon, R.string.adlam_transcription, R.string.adlam_transcription_subtitle, "adlam_transcription"),
)

// Constantes pour l'organisation des modules
private object ModuleIndices {
    const val BASICS_START = 0
    const val BASICS_END = 2
    const val PRACTICE_START = 2
    const val PRACTICE_END = 4
    const val TOOLS_START = 4
}

/**
 * Retourne le premier module (alphabet)
 */
private fun getFirstModule(): MenuItem = menuItems.first()

/**
 * Fonctions publiques pour accéder aux modules depuis MainScreen
 */
fun getBasicsModules(): List<MenuItem> = try {
    menuItems.subList(ModuleIndices.BASICS_START, ModuleIndices.BASICS_END)
} catch (e: Exception) {
    emptyList()
}

fun getPracticeModules(): List<MenuItem> = try {
    menuItems.subList(ModuleIndices.PRACTICE_START, ModuleIndices.PRACTICE_END)
} catch (e: Exception) {
    emptyList()
}

fun getToolsModules(): List<MenuItem> = try {
    menuItems.subList(ModuleIndices.TOOLS_START, menuItems.size)
} catch (e: Exception) {
    emptyList()
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    onNavigation: (MenuItem) -> Unit,
    themeManager: com.bekisma.adlamfulfulde.ThemeManager,
    proManager: com.bekisma.adlamfulfulde.ProManager
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    AppDrawerContent(
                        navController = navController,
                        closeDrawer = { scope.launch { drawerState.close() } },
                        onNavigation = { item ->
                            onNavigation(item)
                        }
                    )
                }
            ) {
                SimpleMainScreen(
                    drawerState = drawerState,
                    onNavigation = { item -> onNavigation(item) }
                )
            }
        }
        composable("alphabet") { 
            com.bekisma.adlamfulfulde.screens.alphabet.AlphabetDisplayScreen(
                navController = navController
            )
        }
        composable("numbers") {
            NumbersScreen(navController)
        }
        composable("writing") { WritingScreen(navController) }
        composable("quiz") {
            QuizScreen(navController)
        }
        composable("about") { AboutScreen(navController) }
        composable(
            route = "writingPractice/{writingType}",
            arguments = listOf(navArgument("writingType") { type = NavType.StringType })
        ) { backStackEntry ->
            val typeString = backStackEntry.arguments?.getString("writingType")
            val writingType = try {
                WritingType.valueOf(typeString ?: WritingType.UPPERCASE.name)
            } catch (e: IllegalArgumentException) {
                WritingType.UPPERCASE
            }

            WritingPracticeScreen(navController = navController, writingType = writingType)
        }

        composable("settings") {
            val scope = androidx.compose.runtime.rememberCoroutineScope()
            val currentTheme = themeManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM).value
            val currentColorTheme = themeManager.colorTheme.collectAsState(initial = ColorTheme.DEFAULT).value
            SettingsScreen(
                navController = navController,
                currentTheme = currentTheme,
                currentColorTheme = currentColorTheme,
                onThemeChanged = { theme ->
                    scope.launch { themeManager.saveThemeMode(theme) }
                },
                onColorThemeChanged = { colorTheme ->
                    scope.launch { themeManager.saveColorTheme(colorTheme) }
                }
            )
        }
        composable(
            route = "DetailAlphabetScreen/{letter}",
            arguments = listOf(navArgument("letter") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val letter = backStackEntry.arguments?.getString("letter")
            DetailAlphabetScreen(letter = letter ?: "", navController = navController)
        }

        // Routes pour le Vocabulaire
        composable("vocabulary_list") {
            EnhancedVocabularyListScreen(navController = navController)
        }
        composable(
            route = "vocabulary_detail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId")
            VocabularyDetailScreen(navController = navController, itemId = itemId)
        }

        // -- NOUVELLES ROUTES POUR LA LECTURE GUIDÉE --
        composable("reading_passage_list") {
            ReadingPassageListScreen(navController = navController)
        }
        composable(
            route = "reading_player/{passageId}",
            arguments = listOf(navArgument("passageId") { type = NavType.IntType })
        ) { backStackEntry ->
            val passageId = backStackEntry.arguments?.getInt("passageId")
            ReadingPlayerScreen(navController = navController, passageId = passageId)
        }
        // -- FIN DES NOUVELLES ROUTES --
        composable("adlam_keyboard") { AdlamKeyboardScreen(navController) }
        composable("adlam_transcription") { AdlamLatinTranscriptionScreen(navController) }
        composable("analytics") {
            AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Vocabulary learning screens
        composable("vocabulary_flashcards") {
            com.bekisma.adlamfulfulde.screens.vocabulary.FlashcardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("vocabulary_quiz") {
            com.bekisma.adlamfulfulde.screens.vocabulary.VocabularyQuizScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("vocabulary_analytics") {
            com.bekisma.adlamfulfulde.screens.vocabulary.VocabularyAnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Alphabet Learning Routes
        
        
        composable("alphabet_quiz") {
            com.bekisma.adlamfulfulde.screens.alphabet.AlphabetQuizScreen(
                navController = navController
            )
        }
        
        composable("audio_quiz") {
            com.bekisma.adlamfulfulde.screens.alphabet.AudioQuizScreen(
                navController = navController
            )
        }
        
        // Routes pour les nouvelles fonctionnalités d'écriture
        composable("culturalWords") {
            CulturalWordsScreen(navController = navController)
        }
        
        composable(
            route = "comparisonMode/{writingType}",
            arguments = listOf(navArgument("writingType") { type = NavType.StringType })
        ) { backStackEntry ->
            val typeString = backStackEntry.arguments?.getString("writingType")
            val writingType = try {
                WritingType.valueOf(typeString ?: WritingType.UPPERCASE.name)
            } catch (e: IllegalArgumentException) {
                WritingType.UPPERCASE
            }
            ComparisonModeScreen(navController = navController, writingType = writingType)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerContent(
    navController: NavHostController,
    closeDrawer: () -> Unit,
    onNavigation: (MenuItem) -> Unit
) {
    ModalDrawerSheet {
        // En-tête du tiroir
        Text(
            text = "Adlam Fulfulde",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider()

        // Éléments du menu
        LazyColumn {
            items(menuItems) { item: MenuItem ->
                NavigationDrawerItem(
                    label = { Text(stringResource(id = item.titleRes)) },
                    selected = false, // Vous pouvez ajouter une logique pour marquer l'élément sélectionné
                    onClick = {
                        onNavigation(item)
                        closeDrawer()
                    },
                    icon = { Icon(painterResource(id = item.imageRes), contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
            // Ajouter un élément pour les paramètres
            item {
                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.settings)) },
                    selected = false,
                    onClick = {
                        navController.navigate("settings")
                        closeDrawer()
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
            // Ajouter un élément pour "Analytics"
            item {
                NavigationDrawerItem(
                    label = { Text("Analytics") },
                    selected = false,
                    onClick = {
                        navController.navigate("analytics")
                        closeDrawer()
                    },
                    icon = { Icon(painterResource(id = R.drawable.ic_analytics), contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
            // Ajouter un élément pour "À propos"
            item {
                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.about)) },
                    selected = false,
                    onClick = {
                        navController.navigate("about")
                        closeDrawer()
                    },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }
}
