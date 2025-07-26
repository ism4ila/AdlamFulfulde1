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

    // Mapping CORRIGÉ Adlam vers Latin basé sur les sources officielles
    val adlamToLatinMap = mapOf(
        // VOYELLES CORRIGÉES (sources officielles)
        "𞤢" to "a", // ADLAM SMALL LETTER ALIF - voyelle A
        "𞤫" to "e", // ADLAM SMALL LETTER E - voyelle E  
        "𞤭" to "i", // ADLAM SMALL LETTER I - voyelle I
        "𞤮" to "o", // ADLAM SMALL LETTER O - voyelle O
        "𞤵" to "u", // ADLAM SMALL LETTER U - voyelle U
        
        // VOYELLES MAJUSCULES
        "𞤀" to "A", // ADLAM CAPITAL LETTER ALIF
        "𞤉" to "E", // ADLAM CAPITAL LETTER E
        "𞤋" to "I", // ADLAM CAPITAL LETTER I  
        "𞤌" to "O", // ADLAM CAPITAL LETTER O
        "𞤓" to "U", // ADLAM CAPITAL LETTER U
        
        // CONSONNES CORRIGÉES
        "𞤦" to "b", // ADLAM SMALL LETTER BA - consonne B
        "𞤨" to "p", // ADLAM SMALL LETTER PE - consonne P
        "𞤩" to "ɓ", // ADLAM SMALL LETTER BHE - B implosif
        "𞤣" to "d", // ADLAM SMALL LETTER DAALI - consonne D
        "𞤼" to "t", // ADLAM SMALL LETTER TU - consonne T
        "𞤶" to "j", // ADLAM SMALL LETTER JIIM - consonne J (dʒ)
        "𞤳" to "k", // ADLAM SMALL LETTER KAF - consonne K
        "𞤺" to "g", // ADLAM SMALL LETTER GA - consonne G
        "𞤬" to "f", // ADLAM SMALL LETTER FA - consonne F
        "𞤧" to "s", // ADLAM SMALL LETTER SINNYIIYHE - consonne S
        "𞤸" to "h", // ADLAM SMALL LETTER HA - consonne H
        "𞤥" to "m", // ADLAM SMALL LETTER MIIM - consonne M
        "𞤲" to "n", // ADLAM SMALL LETTER NUN - consonne N
        "𞤱" to "w", // ADLAM SMALL LETTER WAW - consonne W
        "𞤪" to "r", // ADLAM SMALL LETTER RA - consonne R
        "𞤴" to "y", // ADLAM SMALL LETTER YA - consonne Y (j)
        "𞤤" to "l", // ADLAM SMALL LETTER LAAM - consonne L
        
        // CONSONNES MAJUSCULES
        "𞤄" to "B", // ADLAM CAPITAL LETTER BA
        "𞤆" to "P", // ADLAM CAPITAL LETTER PE
        "𞤇" to "Ɓ", // ADLAM CAPITAL LETTER BHE
        "𞤁" to "D", // ADLAM CAPITAL LETTER DAALI
        "𞤚" to "T", // ADLAM CAPITAL LETTER TU
        "𞤔" to "J", // ADLAM CAPITAL LETTER JIIM
        "𞤑" to "K", // ADLAM CAPITAL LETTER KAF
        "𞤘" to "G", // ADLAM CAPITAL LETTER GA
        "𞤊" to "F", // ADLAM CAPITAL LETTER FA
        "𞤅" to "S", // ADLAM CAPITAL LETTER SINNYIIYHE
        "𞤖" to "H", // ADLAM CAPITAL LETTER HA
        "𞤃" to "M", // ADLAM CAPITAL LETTER MIIM
        "𞤐" to "N", // ADLAM CAPITAL LETTER NUN
        "𞤏" to "W", // ADLAM CAPITAL LETTER WAW
        "𞤈" to "R", // ADLAM CAPITAL LETTER RA
        "𞤒" to "Y", // ADLAM CAPITAL LETTER YA
        "𞤂" to "L", // ADLAM CAPITAL LETTER LAAM

        // CONSONNES SPÉCIALES FULFULDE (implosives et autres)
        "𞤯" to "ɗ", // ADLAM SMALL LETTER DHA - D implosif
        "𞤻" to "ɠ", // ADLAM SMALL LETTER NYA - G implosif  
        "𞤰" to "ƴ", // ADLAM SMALL LETTER YHE - Y palatale
        "𞤷" to "ŋ", // ADLAM SMALL LETTER CHI - NG vélaire
        
        // VOYELLES LONGUES CORRIGÉES
        "𞤢𞤢" to "aa", // Double A
        "𞤫𞤫" to "ee", // Double E
        "𞤭𞤭" to "ii", // Double I
        "𞤮𞤮" to "oo", // Double O
        "𞤵𞤵" to "uu", // Double U
        
        // CONSONNES PRÉNASALISÉES
        "𞤲𞤣" to "nd", // N + D
        "𞤲𞤦" to "nb", // N + B
        "𞤲𞤺" to "ng", // N + G
        "𞤲𞤳" to "nk", // N + K
        "𞤲𞤶" to "nj", // N + J
        "𞤲𞤴" to "ny", // N + Y
        
        // CONSONNES COMPOSÉES COURANTES
        "𞤧𞤸" to "sh", // S + H = SH
        "𞤼𞤸" to "th", // T + H = TH
        "𞤳𞤸" to "kh", // K + H = KH
        "𞤺𞤸" to "gh", // G + H = GH

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

        // CARACTÈRES DE BASE
        " " to " ", // Espace normal
        "\n" to "\n", // Retour à la ligne
        "\t" to "\t", // Tabulation
        
        // DIPHTONGUES COURANTES
        "𞤢𞤭" to "ai", // A + I = AI
        "𞤢𞤵" to "au", // A + U = AU
        "𞤫𞤭" to "ei", // E + I = EI
        "𞤫𞤵" to "eu", // E + U = EU
        "𞤮𞤭" to "oi", // O + I = OI
        "𞤮𞤵" to "ou"  // O + U = OU
    )

    // Mapping inverse pour Latin vers Adlam
    val latinToAdlamMap = adlamToLatinMap.entries.associate { (k, v) -> v to k }

    // Mapping CORRIGÉ Latin vers Adlam
    val latinToAdlamSpecialMap = mapOf(
        // VOYELLES CORRIGÉES (minuscules)
        "a" to "𞤢", // Latin a → Adlam small alif
        "e" to "𞤫", // Latin e → Adlam small e
        "i" to "𞤭", // Latin i → Adlam small i
        "o" to "𞤮", // Latin o → Adlam small o  
        "u" to "𞤵", // Latin u → Adlam small u
        
        // VOYELLES MAJUSCULES
        "A" to "𞤀", // Latin A → Adlam capital alif
        "E" to "𞤉", // Latin E → Adlam capital e
        "I" to "𞤋", // Latin I → Adlam capital i
        "O" to "𞤌", // Latin O → Adlam capital o
        "U" to "𞤓", // Latin U → Adlam capital u

        // CONSONNES CORRIGÉES (minuscules)
        "b" to "𞤦", // Latin b → Adlam small ba
        "p" to "𞤨", // Latin p → Adlam small pe
        "d" to "𞤣", // Latin d → Adlam small daali
        "t" to "𞤼", // Latin t → Adlam small tu
        "j" to "𞤶", // Latin j → Adlam small jiim
        "k" to "𞤳", // Latin k → Adlam small kaf
        "g" to "𞤺", // Latin g → Adlam small ga
        "f" to "𞤬", // Latin f → Adlam small fa
        "s" to "𞤧", // Latin s → Adlam small sinnyiiyhe
        "h" to "𞤸", // Latin h → Adlam small ha
        "m" to "𞤥", // Latin m → Adlam small miim
        "n" to "𞤲", // Latin n → Adlam small nun
        "w" to "𞤱", // Latin w → Adlam small waw
        "r" to "𞤪", // Latin r → Adlam small ra
        "y" to "𞤴", // Latin y → Adlam small ya
        "l" to "𞤤", // Latin l → Adlam small laam
        
        // CONSONNES MAJUSCULES
        "B" to "𞤄", // Latin B → Adlam capital ba
        "P" to "𞤆", // Latin P → Adlam capital pe
        "D" to "𞤁", // Latin D → Adlam capital daali
        "T" to "𞤚", // Latin T → Adlam capital tu
        "J" to "𞤔", // Latin J → Adlam capital jiim
        "K" to "𞤑", // Latin K → Adlam capital kaf
        "G" to "𞤘", // Latin G → Adlam capital ga
        "F" to "𞤊", // Latin F → Adlam capital fa
        "S" to "𞤅", // Latin S → Adlam capital sinnyiiyhe
        "H" to "𞤖", // Latin H → Adlam capital ha
        "M" to "𞤃", // Latin M → Adlam capital miim
        "N" to "𞤐", // Latin N → Adlam capital nun
        "W" to "𞤏", // Latin W → Adlam capital waw
        "R" to "𞤈", // Latin R → Adlam capital ra
        "Y" to "𞤒", // Latin Y → Adlam capital ya
        "L" to "𞤂", // Latin L → Adlam capital laam

        // VOYELLES LONGUES
        "aa" to "𞤢𞤢", // Double a
        "ee" to "𞤫𞤫", // Double e
        "ii" to "𞤭𞤭", // Double i
        "oo" to "𞤮𞤮", // Double o
        "uu" to "𞤵𞤵", // Double u

        // CONSONNES SPÉCIALES FULFULDE
        "ɓ" to "𞤩", // B implosif → Adlam bhe
        "ɗ" to "𞤯", // D implosif → Adlam dha
        "ɠ" to "𞤻", // G implosif → Adlam nya
        "ƴ" to "𞤰", // Y palatale → Adlam yhe
        "ŋ" to "𞤷", // NG vélaire → Adlam chi

        // CONSONNES PRÉNASALISÉES ET COMPOSÉES
        "nd" to "𞤲𞤣", // N + D
        "nb" to "𞤲𞤦", // N + B  
        "ng" to "𞤲𞤺", // N + G
        "nk" to "𞤲𞤳", // N + K
        "nj" to "𞤲𞤶", // N + J
        "ny" to "𞤲𞤴", // N + Y
        "sh" to "𞤧𞤸", // S + H
        "th" to "𞤼𞤸", // T + H
        "kh" to "𞤳𞤸", // K + H
        "gh" to "𞤺𞤸"  // G + H
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
        // bottomBar removed - ad moved to content
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

                        // Exemples CORRIGÉS avec les vrais mappings
                        val examples = listOf(
                            "Voyelles minuscules" to listOf("𞤢 → a", "𞤫 → e", "𞤭 → i", "𞤮 → o", "𞤵 → u"),
                            "Voyelles majuscules" to listOf("𞤀 → A", "𞤉 → E", "𞤋 → I", "𞤌 → O", "𞤓 → U"),
                            "Consonnes courantes" to listOf("𞤦 → b", "𞤣 → d", "𞤤 → l", "𞤥 → m", "𞤲 → n"),
                            "Consonnes spéciales" to listOf("𞤩 → ɓ", "𞤯 → ɗ", "𞤻 → ɠ", "𞤰 → ƴ", "𞤷 → ŋ"),
                            "Consonnes prénasalisées" to listOf("𞤲𞤣 → nd", "𞤲𞤦 → nb", "𞤲𞤺 → ng", "𞤲𞤳 → nk", "𞤲𞤴 → ny"),
                            "Consonnes composées" to listOf("𞤧𞤸 → sh", "𞤼𞤸 → th", "𞤳𞤸 → kh", "𞤺𞤸 → gh"),
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

            
            // Space at the end
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
