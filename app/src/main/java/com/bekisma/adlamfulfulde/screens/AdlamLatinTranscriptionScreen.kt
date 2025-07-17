package com.bekisma.adlamfulfulde.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import kotlinx.coroutines.delay

data class TranscriptionState(
    val inputText: String = "",
    val outputText: String = "",
    val isAdlamToLatin: Boolean = true,
    val showCopiedMessage: Boolean = false,
    val showTranscriptionGuide: Boolean = false,
    val isLoading: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdlamLatinTranscriptionScreen(navController: NavController) {
    var transcriptionState by remember { mutableStateOf(TranscriptionState()) }
    val hapticFeedback = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Animation pour les boutons
    val buttonScale by animateFloatAsState(
        targetValue = if (transcriptionState.outputText.isNotEmpty()) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    // Mapping complet et précis Adlam vers Latin basé sur les standards Unicode
    val adlamToLatinMap = mapOf(
        // Consonnes de base (Unicode U+1E900-U+1E91B)
        "𞤀" to "a", // ADLAM LETTER ALIF
        "𞤁" to "d", // ADLAM LETTER DAALI
        "𞤂" to "l", // ADLAM LETTER LAAM
        "𞤃" to "m", // ADLAM LETTER MIIM
        "𞤄" to "b", // ADLAM LETTER BA
        "𞤅" to "s", // ADLAM LETTER SINNYIIYHE
        "𞤆" to "pe", // ADLAM LETTER PE
        "𞤇" to "bh", // ADLAM LETTER BHE
        "𞤈" to "r", // ADLAM LETTER RA
        "𞤉" to "e", // ADLAM LETTER E
        "𞤊" to "f", // ADLAM LETTER FA
        "𞤋" to "i", // ADLAM LETTER I
        "𞤌" to "o", // ADLAM LETTER O
        "𞤍" to "dh", // ADLAM LETTER DHA
        "𞤎" to "yh", // ADLAM LETTER YHE
        "𞤏" to "w", // ADLAM LETTER WAW
        "𞤐" to "n", // ADLAM LETTER NUN
        "𞤑" to "k", // ADLAM LETTER KAF
        "𞤒" to "y", // ADLAM LETTER YA
        "𞤓" to "u", // ADLAM LETTER U
        "𞤔" to "j", // ADLAM LETTER JIIM
        "𞤕" to "c", // ADLAM LETTER CHI
        "𞤖" to "h", // ADLAM LETTER HA
        "𞤗" to "q", // ADLAM LETTER QAAF
        "𞤘" to "g", // ADLAM LETTER GA
        "𞤙" to "ny", // ADLAM LETTER NYA
        "𞤚" to "t", // ADLAM LETTER TU
        "𞤛" to "nd", // ADLAM LETTER NDA

        // Voyelles avec diacritiques (Unicode U+1E91C-U+1E943)
        "𞤜" to "aa", // ADLAM LETTER A WITH DIAERESIS
        "𞤝" to "ee", // ADLAM LETTER E WITH DIAERESIS
        "𞤞" to "ii", // ADLAM LETTER I WITH DIAERESIS
        "𞤟" to "oo", // ADLAM LETTER O WITH DIAERESIS
        "𞤠" to "uu", // ADLAM LETTER U WITH DIAERESIS

        // Consonnes additionnelles
        "𞤡" to "p", // ADLAM LETTER P
        "𞤢" to "ɓ", // ADLAM LETTER ALIF WITH DIAERESIS
        "𞤣" to "mb", // ADLAM LETTER DAALI WITH DIAERESIS
        "𞤤" to "ɗ", // ADLAM LETTER LAAM WITH DIAERESIS
        "𞤥" to "ɠ", // ADLAM LETTER MIIM WITH DIAERESIS
        "𞤦" to "ƴ", // ADLAM LETTER BA WITH DIAERESIS
        "𞤧" to "ñ", // ADLAM LETTER SINNYIIYHE WITH DIAERESIS
        "𞤨" to "ŋ", // ADLAM LETTER PE WITH DIAERESIS
        "𞤩" to "ng", // ADLAM LETTER BHE WITH DIAERESIS
        "𞤪" to "nj", // ADLAM LETTER RA WITH DIAERESIS
        "𞤫" to "nk", // ADLAM LETTER E WITH DIAERESIS
        "𞤬" to "sh", // ADLAM LETTER FA WITH DIAERESIS
        "𞤭" to "th", // ADLAM LETTER I WITH DIAERESIS
        "𞤮" to "kh", // ADLAM LETTER O WITH DIAERESIS
        "𞤯" to "gh", // ADLAM LETTER DHA WITH DIAERESIS
        "𞤰" to "zh", // ADLAM LETTER YHE WITH DIAERESIS
        "𞤱" to "z", // ADLAM LETTER WAW WITH DIAERESIS
        "𞤲" to "x", // ADLAM LETTER NUN WITH DIAERESIS
        "𞤳" to "v", // ADLAM LETTER KAF WITH DIAERESIS
        "𞤴" to "ts", // ADLAM LETTER YA WITH DIAERESIS
        "𞤵" to "dz", // ADLAM LETTER U WITH DIAERESIS
        "𞤶" to "tsh", // ADLAM LETTER JIIM WITH DIAERESIS
        "𞤷" to "dzh", // ADLAM LETTER CHI WITH DIAERESIS
        "𞤸" to "rh", // ADLAM LETTER HA WITH DIAERESIS
        "𞤹" to "lh", // ADLAM LETTER QAAF WITH DIAERESIS
        "𞤺" to "mh", // ADLAM LETTER GA WITH DIAERESIS
        "𞤻" to "nh", // ADLAM LETTER NYA WITH DIAERESIS
        "𞤼" to "wh", // ADLAM LETTER TU WITH DIAERESIS
        "𞤽" to "bh", // ADLAM LETTER NDA WITH DIAERESIS

        // Chiffres Adlam (Unicode U+1E950-U+1E959)
        "𞥐" to "0", // ADLAM DIGIT ZERO
        "𞥑" to "1", // ADLAM DIGIT ONE
        "𞥒" to "2", // ADLAM DIGIT TWO
        "𞥓" to "3", // ADLAM DIGIT THREE
        "𞥔" to "4", // ADLAM DIGIT FOUR
        "𞥕" to "5", // ADLAM DIGIT FIVE
        "𞥖" to "6", // ADLAM DIGIT SIX
        "𞥗" to "7", // ADLAM DIGIT SEVEN
        "𞥘" to "8", // ADLAM DIGIT EIGHT
        "𞥙" to "9", // ADLAM DIGIT NINE

        // Ponctuation Adlam (Unicode U+1E95A-U+1E95F)
        "𞥚" to "!", // ADLAM INITIAL EXCLAMATION MARK
        "𞥛" to "!", // ADLAM FINAL EXCLAMATION MARK
        "𞥜" to "?", // ADLAM INITIAL QUESTION MARK
        "𞥝" to "?", // ADLAM FINAL QUESTION MARK
        "𞥞" to ".", // ADLAM FULL STOP
        "𞥟" to ",", // ADLAM COMMA

        // Espaces et autres caractères spéciaux
        " " to " ", // Espace normal
        "\n" to "\n", // Retour à la ligne
        "\t" to "\t", // Tabulation

        // Modifications consonantiques spécifiques au Fulfulde
        "𞤾" to "ɓ", // ADLAM LETTER ALIF WITH HAMZA ABOVE
        "𞤿" to "ɗ", // ADLAM LETTER ALIF WITH HAMZA BELOW
        "𞥀" to "ƴ", // ADLAM LETTER WAWI
        "𞥁" to "ɠ", // ADLAM LETTER ALIF WITH HAMZA ABOVE AND BELOW
        "𞥂" to "ŋ", // ADLAM LETTER ALIF WITH DAMMA ABOVE
        "𞥃" to "ñ", // ADLAM LETTER ALIF WITH KASRA BELOW

        // Caractères composés courants en Fulfulde
        "𞤀𞤀" to "aa", // Double A
        "𞤉𞤉" to "ee", // Double E
        "𞤋𞤋" to "ii", // Double I
        "𞤌𞤌" to "oo", // Double O
        "𞤓𞤓" to "uu", // Double U

        // Consonnes prénasalisées
        "𞤂𞤀" to "nda", // ND + A
        "𞤂𞤄" to "ndb", // ND + B
        "𞤂𞤆" to "ndp", // ND + P
        "𞤂𞤘" to "ndg", // ND + G
        "𞤂𞤑" to "ndk", // ND + K
        "𞤂𞤔" to "ndj", // ND + J
        "𞤂𞤕" to "ndc", // ND + C

        // Modificateurs de tons (si utilisés)
        "𞤀́" to "á", // A avec ton haut
        "𞤀̀" to "à", // A avec ton bas
        "𞤀̂" to "â", // A avec ton montant
        "𞤀̌" to "ǎ", // A avec ton descendant

        // Variantes contextuelles
        "𞤀𞤋" to "ai", // A + I
        "𞤀𞤓" to "au", // A + U
        "𞤉𞤋" to "ei", // E + I
        "𞤉𞤓" to "eu", // E + U
        "𞤋𞤀" to "ia", // I + A
        "𞤋𞤉" to "ie", // I + E
        "𞤌𞤀" to "oa", // O + A
        "𞤌𞤉" to "oe", // O + E
        "𞤌𞤋" to "oi", // O + I
        "𞤓𞤀" to "ua", // U + A
        "𞤓𞤉" to "ue", // U + E
        "𞤓𞤋" to "ui", // U + I
        "𞤓𞤌" to "uo"  // U + O
    )

    // Mapping inverse pour Latin vers Adlam
    val latinToAdlamMap = adlamToLatinMap.entries.associate { (k, v) -> v to k }

    // Ajout de mappings spéciaux pour les caractères latins vers Adlam
    val latinToAdlamSpecialMap = mapOf(
        // Voyelles simples
        "a" to "𞤀",
        "e" to "𞤉",
        "i" to "𞤋",
        "o" to "𞤌",
        "u" to "𞤓",

        // Consonnes simples
        "b" to "𞤄",
        "c" to "𞤕",
        "d" to "𞤁",
        "f" to "𞤊",
        "g" to "𞤘",
        "h" to "𞤖",
        "j" to "𞤔",
        "k" to "𞤑",
        "l" to "𞤂",
        "m" to "𞤃",
        "n" to "𞤐",
        "p" to "𞤡",
        "q" to "𞤗",
        "r" to "𞤈",
        "s" to "𞤅",
        "t" to "𞤚",
        "v" to "𞤳",
        "w" to "𞤏",
        "x" to "𞤲",
        "y" to "𞤒",
        "z" to "𞤱",

        // Voyelles longues
        "aa" to "𞤜",
        "ee" to "𞤝",
        "ii" to "𞤞",
        "oo" to "𞤟",
        "uu" to "𞤠",

        // Consonnes spéciales Fulfulde
        "ɓ" to "𞤢",
        "ɗ" to "𞤤",
        "ɠ" to "𞤥",
        "ƴ" to "𞤦",
        "ŋ" to "𞤨",
        "ñ" to "𞤧",

        // Consonnes composées
        "mb" to "𞤣",
        "nd" to "𞤛",
        "ng" to "𞤩",
        "nj" to "𞤪",
        "nk" to "𞤫",
        "ny" to "𞤙",
        "sh" to "𞤬",
        "th" to "𞤭",
        "kh" to "𞤮",
        "gh" to "𞤯",
        "zh" to "𞤰",
        "ts" to "𞤴",
        "dz" to "𞤵",
        "tsh" to "𞤶",
        "dzh" to "𞤷"
    )

    // Fonction de transcription améliorée
    fun transcribeText(text: String, isAdlamToLatin: Boolean): String {
        if (text.isEmpty()) return ""

        return if (isAdlamToLatin) {
            // Adlam vers Latin
            var result = text

            // Traiter d'abord les séquences les plus longues
            val sortedMappings = adlamToLatinMap.entries.sortedByDescending { it.key.length }

            for ((adlam, latin) in sortedMappings) {
                result = result.replace(adlam, latin)
            }

            result
        } else {
            // Latin vers Adlam
            var result = text.lowercase()

            // Utiliser d'abord les mappings spéciaux, puis les mappings inversés
            val combinedMappings = (latinToAdlamSpecialMap + latinToAdlamMap)
                .entries
                .sortedByDescending { it.key.length }

            for ((latin, adlam) in combinedMappings) {
                result = result.replace(latin, adlam)
            }

            result
        }
    }

    // Effet pour transcription automatique avec délai
    LaunchedEffect(transcriptionState.inputText, transcriptionState.isAdlamToLatin) {
        if (transcriptionState.inputText.isNotEmpty()) {
            transcriptionState = transcriptionState.copy(isLoading = true)
            delay(300) // Délai pour éviter trop de calculs
            val newOutput = transcribeText(transcriptionState.inputText, transcriptionState.isAdlamToLatin)
            transcriptionState = transcriptionState.copy(outputText = newOutput, isLoading = false)
        } else {
            transcriptionState = transcriptionState.copy(outputText = "", isLoading = false)
        }
    }

    // Effet pour le message de copie
    LaunchedEffect(transcriptionState.showCopiedMessage) {
        if (transcriptionState.showCopiedMessage) {
            delay(2000)
            transcriptionState = transcriptionState.copy(showCopiedMessage = false)
        }
    }

    // Fonctions utilitaires
    fun clearInput() {
        transcriptionState = transcriptionState.copy(inputText = "", outputText = "")
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun swapLanguages() {
        transcriptionState = transcriptionState.copy(
            isAdlamToLatin = !transcriptionState.isAdlamToLatin,
            inputText = transcriptionState.outputText,
            outputText = ""
        )
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun copyOutput() {
        if (transcriptionState.outputText.isNotEmpty()) {
            clipboardManager.setText(AnnotatedString(transcriptionState.outputText))
            transcriptionState = transcriptionState.copy(showCopiedMessage = true)
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    fun shareOutput() {
        if (transcriptionState.outputText.isNotEmpty()) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, transcriptionState.outputText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Partager la transcription")
            context.startActivity(shareIntent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transcription Adlam ↔ Latin",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            transcriptionState = transcriptionState.copy(
                                showTranscriptionGuide = !transcriptionState.showTranscriptionGuide
                            )
                        }
                    ) {
                        Icon(
                            imageVector = if (transcriptionState.showTranscriptionGuide)
                                Icons.Default.Close else Icons.Default.Info,
                            contentDescription = "Guide de transcription"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                BannerAdView()
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            // Message de copie amélioré
            AnimatedVisibility(
                visible = transcriptionState.showCopiedMessage,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Transcription copiée avec succès!",
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Guide de transcription amélioré avec exemples plus complets
            AnimatedVisibility(
                visible = transcriptionState.showTranscriptionGuide,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Guide de transcription complet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Exemples organisés de manière plus complète
                        val examples = listOf(
                            "Voyelles de base" to listOf("𞤀 → a", "𞤉 → e", "𞤋 → i", "𞤌 → o", "𞤓 → u"),
                            "Voyelles longues" to listOf("𞤜 → aa", "𞤝 → ee", "𞤞 → ii", "𞤟 → oo", "𞤠 → uu"),
                            "Consonnes courantes" to listOf("𞤄 → b", "𞤁 → d", "𞤂 → l", "𞤃 → m", "𞤐 → n"),
                            "Consonnes spéciales" to listOf("𞤢 → ɓ", "𞤤 → ɗ", "𞤥 → ɠ", "𞤦 → ƴ", "𞤨 → ŋ"),
                            "Consonnes composées" to listOf("𞤣 → mb", "𞤛 → nd", "𞤩 → ng", "𞤪 → nj", "𞤙 → ny"),
                            "Chiffres" to listOf("𞥐 → 0", "𞥑 → 1", "𞥒 → 2", "𞥓 → 3", "𞥔 → 4"),
                            "Ponctuation" to listOf("𞥞 → .", "𞥟 → ,", "𞥛 → !", "𞥝 → ?")
                        )

                        examples.forEach { (category, exampleList) ->
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = exampleList.joinToString("  •  "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💡 Transcription automatique basée sur les standards Unicode Adlam",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            // Sélecteur de direction amélioré
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Langue source
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (transcriptionState.isAdlamToLatin) "Adlam" else "Latin",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Source",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }

                    // Bouton de swap avec animation
                    ElevatedButton(
                        onClick = { swapLanguages() },
                        modifier = Modifier.size(56.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Inverser les langues",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Langue cible
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (transcriptionState.isAdlamToLatin) "Latin" else "Adlam",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Cible",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Zone de saisie améliorée
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Texte à transcrire",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row {
                            Text(
                                text = "${transcriptionState.inputText.length}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { clearInput() },
                                enabled = transcriptionState.inputText.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Effacer",
                                    tint = if (transcriptionState.inputText.isNotEmpty())
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    BasicTextField(
                        value = transcriptionState.inputText,
                        onValueChange = { newText ->
                            transcriptionState = transcriptionState.copy(inputText = newText)
                        },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        decorationBox = { innerTextField ->
                            Box {
                                if (transcriptionState.inputText.isEmpty()) {
                                    Text(
                                        text = if (transcriptionState.isAdlamToLatin)
                                            "Saisissez du texte en Adlam..."
                                        else
                                            "Saisissez du texte en Latin...",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }

            // Zone de résultat améliorée
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Transcription",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (transcriptionState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Text(
                                text = "${transcriptionState.outputText.length} caractères",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        if (transcriptionState.outputText.isEmpty() && !transcriptionState.isLoading) {
                            Text(
                                text = "La transcription apparaîtra ici...",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            )
                        } else {
                            Text(
                                text = transcriptionState.outputText,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                }
            }

            // Boutons d'action améliorés
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedButton(
                    onClick = { copyOutput() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .scale(if (transcriptionState.outputText.isNotEmpty()) buttonScale else 0.95f),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    enabled = transcriptionState.outputText.isNotEmpty(),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = if (transcriptionState.outputText.isNotEmpty()) 4.dp else 1.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Copier",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }

                ElevatedButton(
                    onClick = { shareOutput() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .scale(if (transcriptionState.outputText.isNotEmpty()) buttonScale else 0.95f),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    enabled = transcriptionState.outputText.isNotEmpty(),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = if (transcriptionState.outputText.isNotEmpty()) 4.dp else 1.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Partager",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            // Espace pour éviter que le contenu soit caché par la publicité
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
