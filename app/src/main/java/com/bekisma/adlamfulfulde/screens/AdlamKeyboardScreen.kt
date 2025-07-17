package com.bekisma.adlamfulfulde.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.delay

data class AdlamKeyboardState(
    val textFieldValue: TextFieldValue = TextFieldValue(),
    val undoStack: List<String> = emptyList(),
    val redoStack: List<String> = emptyList(),
    val showPunctuation: Boolean = false,
    val showNumbers: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdlamKeyboardScreen(navController: NavController) {
    var keyboardState by remember { mutableStateOf(AdlamKeyboardState()) }
    val focusRequester = remember { FocusRequester() }
    val hapticFeedback = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showCopiedMessage by remember { mutableStateOf(false) }

    // Adlam characters organized by category
    val adlamLetters = listOf(
        "𞤀", "𞤁", "𞤂", "𞤃", "𞤄", "𞤅", "𞤆", "𞤇", "𞤈", "𞤉", "𞤊", "𞤋", "𞤌", "𞤍", "𞤎", "𞤏",
        "𞤐", "𞤑", "𞤒", "𞤓", "𞤔", "𞤕", "𞤖", "𞤗", "𞤘", "𞤙", "𞤚", "𞤛", "𞤜", "𞤝", "𞤞", "𞤟",
        "𞤠", "𞤡", "𞤢", "𞤣", "𞤤", "𞤥", "𞤦", "𞤧", "𞤨", "𞤩", "𞤪", "𞤫", "𞤬", "𞤭", "𞤮", "𞤰",
        "𞤱", "𞤲", "𞤳", "𞤴", "𞤵", "𞤶", "𞤷", "𞤸", "𞤹", "𞤺", "𞤻", "𞤼", "𞤽", "𞤾", "𞤿", "𞥀",
        "𞥁", "𞥂", "𞥃", "𞥄", "𞥅", "𞥆", "𞥇", "𞥈", "𞥉", "𞥊", "𞥋", "𞥌", "𞥍", "𞥎", "𞥏"
    )

    val adlamNumbers = listOf(
        "𞥐", "𞥑", "𞥒", "𞥓", "𞥔", "𞥕", "𞥖", "𞥗", "𞥘", "𞥙"
    )

    val adlamPunctuation = listOf(
        "𞥞", "𞥟", ".", ",", "!", "?", ";", ":", "-", "(", ")", "[", "]", "{", "}", "\"", "'", "/", "\\", "@", "#", "$", "%", "&", "*", "+", "=", "<", ">", "^", "_", "`", "|", "~"
    )

    // Effect to show copied message
    LaunchedEffect(showCopiedMessage) {
        if (showCopiedMessage) {
            delay(2000)
            showCopiedMessage = false
        }
    }

    // Helper functions
    fun addToUndoStack(text: String) {
        keyboardState = keyboardState.copy(
            undoStack = keyboardState.undoStack + text,
            redoStack = emptyList()
        )
    }

    fun insertText(char: String) {
        val currentText = keyboardState.textFieldValue.text
        val currentSelection = keyboardState.textFieldValue.selection

        addToUndoStack(currentText)

        val newText = currentText.substring(0, currentSelection.start) +
                char +
                currentText.substring(currentSelection.end)

        val newSelection = TextRange(currentSelection.start + char.length)

        keyboardState = keyboardState.copy(
            textFieldValue = TextFieldValue(newText, newSelection)
        )

        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun deleteCharacter() {
        val currentText = keyboardState.textFieldValue.text
        val currentSelection = keyboardState.textFieldValue.selection

        if (currentSelection.start > 0) {
            addToUndoStack(currentText)

            val newText = currentText.substring(0, currentSelection.start - 1) +
                    currentText.substring(currentSelection.end)

            val newSelection = TextRange(currentSelection.start - 1)

            keyboardState = keyboardState.copy(
                textFieldValue = TextFieldValue(newText, newSelection)
            )

            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    fun clearText() {
        addToUndoStack(keyboardState.textFieldValue.text)
        keyboardState = keyboardState.copy(
            textFieldValue = TextFieldValue("")
        )
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun undo() {
        if (keyboardState.undoStack.isNotEmpty()) {
            val previousText = keyboardState.undoStack.last()
            keyboardState = keyboardState.copy(
                textFieldValue = TextFieldValue(previousText, TextRange(previousText.length)),
                undoStack = keyboardState.undoStack.dropLast(1),
                redoStack = keyboardState.redoStack + keyboardState.textFieldValue.text
            )
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    fun redo() {
        if (keyboardState.redoStack.isNotEmpty()) {
            val nextText = keyboardState.redoStack.last()
            keyboardState = keyboardState.copy(
                textFieldValue = TextFieldValue(nextText, TextRange(nextText.length)),
                undoStack = keyboardState.undoStack + keyboardState.textFieldValue.text,
                redoStack = keyboardState.redoStack.dropLast(1)
            )
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    fun copyText() {
        if (keyboardState.textFieldValue.text.isNotEmpty()) {
            clipboardManager.setText(AnnotatedString(keyboardState.textFieldValue.text))
            showCopiedMessage = true
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    fun shareText() {
        if (keyboardState.textFieldValue.text.isNotEmpty()) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, keyboardState.textFieldValue.text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.adlam_keyboard),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.size(28.dp)
            )
                    }
                },
                actions = {
                    // Undo/Redo buttons
                    IconButton(
                        onClick = { undo() },
                        enabled = keyboardState.undoStack.isNotEmpty()
                    ) {
                        Icon(
                Icons.AutoMirrored.Filled.Undo,
                contentDescription = stringResource(R.string.undo),
                modifier = Modifier.size(28.dp)
            )
                    }
                    IconButton(
                        onClick = { redo() },
                        enabled = keyboardState.redoStack.isNotEmpty()
                    ) {
                        Icon(
                Icons.AutoMirrored.Filled.Redo,
                contentDescription = stringResource(R.string.redo),
                modifier = Modifier.size(28.dp)
            )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
        ) {
            // Text Display Area with improved design
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Character count and word count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Caractères: ${keyboardState.textFieldValue.text.length}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Mots: ${keyboardState.textFieldValue.text.split(" ").filter { it.isNotBlank() }.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Improved text field with cursor support
                    BasicTextField(
                        value = keyboardState.textFieldValue,
                        onValueChange = { newValue ->
                            keyboardState = keyboardState.copy(textFieldValue = newValue)
                        },
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 32.sp
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Default
                        )
                    )
                }
            }

            // Copied message
            AnimatedVisibility(
                visible = showCopiedMessage,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Texte copié dans le presse-papiers!",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Keyboard type selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    onClick = {
                        keyboardState = keyboardState.copy(
                            showPunctuation = false,
                            showNumbers = false
                        )
                    },
                    label = { Text("Lettres") },
                    selected = !keyboardState.showPunctuation && !keyboardState.showNumbers
                )
                FilterChip(
                    onClick = {
                        keyboardState = keyboardState.copy(
                            showNumbers = true,
                            showPunctuation = false
                        )
                    },
                    label = { Text("Nombres") },
                    selected = keyboardState.showNumbers
                )
                FilterChip(
                    onClick = {
                        keyboardState = keyboardState.copy(
                            showPunctuation = true,
                            showNumbers = false
                        )
                    },
                    label = { Text("Ponctuation") },
                    selected = keyboardState.showPunctuation
                )
            }

            // Keyboard Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                // Character grid
                val currentCharacters = when {
                    keyboardState.showNumbers -> adlamNumbers
                    keyboardState.showPunctuation -> adlamPunctuation
                    else -> adlamLetters
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (keyboardState.showNumbers) 5 else 8),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(currentCharacters) { char ->
                        ElevatedButton(
                            onClick = { insertText(char) },
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxSize(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text(
                                text = char,
                                fontSize = if (keyboardState.showPunctuation) 18.sp else 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Control buttons with improved design
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Clear button
                    ElevatedButton(
                        onClick = { clearText() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.clear))
                    }

                    // Delete button
                    ElevatedButton(
                        onClick = { deleteCharacter() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                Icons.AutoMirrored.Filled.Backspace,
                contentDescription = stringResource(R.string.delete),
                modifier = Modifier.size(28.dp)
            )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.delete))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Space button
                    ElevatedButton(
                        onClick = { insertText(" ") },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.SpaceBar,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.space))
                    }

                    // Enter button
                    ElevatedButton(
                        onClick = { insertText("\n") },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                Icons.AutoMirrored.Filled.KeyboardReturn,
                contentDescription = stringResource(R.string.enter),
                modifier = Modifier.size(28.dp)
            )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.enter))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Copy and Share buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedButton(
                        onClick = { copyText() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        enabled = keyboardState.textFieldValue.text.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.copy))
                    }

                    ElevatedButton(
                        onClick = { shareText() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        enabled = keyboardState.textFieldValue.text.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.share))
                    }
                }
            }
        }
    }
}