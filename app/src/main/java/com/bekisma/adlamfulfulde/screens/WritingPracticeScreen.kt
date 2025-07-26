package com.bekisma.adlamfulfulde.screens // Ou ton package approprié

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Importe toutes les icônes filled
import kotlin.math.sqrt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import kotlinx.coroutines.launch
import kotlin.math.pow

// --- Enum et Données ---

enum class WritingType {
    UPPERCASE,
    LOWERCASE,
    NUMBERS
}

enum class WritingMode {
    SEPARATE,  // Mode actuel - lettres séparées
    CURSIVE    // Mode nouveau - lettres liées (joined)
}

enum class GuidanceMode {
    NO_GUIDANCE,     // Pas de guide
    VISUAL_ONLY,     // Guide visuel seulement
    FINGER_TRACING   // Guide avec suivi du doigt
}

object AdlamCharacters {
    val uppercase = listOf("𞤀", "𞤁", "𞤂", "𞤃", "𞤄", "𞤅", "𞤆", "𞤇", "𞤈", "𞤉", "𞤊", "𞤋", "𞤌", "𞤍", "𞤎", "𞤏", "𞤐", "𞤑", "𞤒", "𞤓", "𞤔", "𞤕", "𞤖", "𞤗", "𞤘", "𞤙", "𞤚", "𞤛")
    val lowercase = listOf("𞤢", "𞤣", "𞤤", "𞤥", "𞤦", "𞤧", "𞤨", "𞤩", "𞤪", "𞤫", "𞤬", "𞤭", "𞤮", "𞤯", "𞤰", "𞤱", "𞤲", "𞤳", "𞤴", "𞤵", "𞤶", "𞤷", "𞤸", "𞤹", "𞤺", "𞤻", "𞤼")
    val numbers = listOf("𞥐", "𞥑", "𞥒", "𞥓", "𞥔", "𞥕", "𞥖", "𞥗", "𞥘", "𞥙")
}

// --- Structures pour les Guides et Checkpoints ---

data class StrokeGuideSegment(
    val path: Path, // Chemin relatif (0f-1f)
    val startPoint: Offset? = null, // Point de départ relatif
    val isDash: Boolean = true
)

data class CharacterGuideData(
    val segments: List<StrokeGuideSegment>,
    val checkpoints: List<Offset> // Liste ORDONNÉE des checkpoints relatifs (0f-1f)
)

// !!! ATTENTION : VOUS DEVEZ REMPLIR CETTE MAP AVEC LES DONNÉES RÉELLES !!!
// Définissez les chemins et checkpoints pour CHAQUE caractère Adlam.
val adlamCharacterGuides: Map<String, CharacterGuideData> = mapOf(
    // Uppercase
    "𞤀" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.3f, 0.8f); lineTo(0.7f, 0.8f); lineTo(0.5f, 0.2f) }, Offset(0.5f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.3f, 0.8f), Offset(0.7f, 0.8f), Offset(0.5f, 0.2f))
    ),
    "𞤁" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); cubicTo(0.6f, 0.4f, 0.8f, 0.4f, 0.8f, 0.5f); cubicTo(0.8f, 0.6f, 0.6f, 0.6f, 0.5f, 0.5f) }, Offset(0.5f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f), Offset(0.5f, 0.5f), Offset(0.8f, 0.5f))
    ),
    "𞤂" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.3f, 0.8f); lineTo(0.7f, 0.8f) }, Offset(0.3f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f), Offset(0.7f, 0.8f))
    ),
    "𞤃" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.8f); lineTo(0.3f, 0.2f); lineTo(0.5f, 0.5f); lineTo(0.7f, 0.2f); lineTo(0.7f, 0.8f) }, Offset(0.3f, 0.8f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.8f), Offset(0.3f, 0.2f), Offset(0.5f, 0.5f), Offset(0.7f, 0.2f), Offset(0.7f, 0.8f))
    ),
    "𞤄" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); cubicTo(0.4f, 0.3f, 0.2f, 0.3f, 0.2f, 0.2f) }, Offset(0.5f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f), Offset(0.2f, 0.2f))
    ),
    "𞤅" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.2f); cubicTo(0.5f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f) }, Offset(0.7f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); cubicTo(0.5f, 0.4f, 0.5f, 0.6f, 0.3f, 0.8f) }, Offset(0.3f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.7f, 0.2f), Offset(0.7f, 0.8f), Offset(0.3f, 0.2f), Offset(0.3f, 0.8f))
    ),
    "𞤆" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); cubicTo(0.6f, 0.3f, 0.8f, 0.3f, 0.8f, 0.2f) }, Offset(0.5f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f), Offset(0.8f, 0.2f))
    ),
    "𞤇" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.8f); cubicTo(0.4f, 0.7f, 0.2f, 0.7f, 0.2f, 0.8f) }, Offset(0.5f, 0.8f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f), Offset(0.2f, 0.8f))
    ),
    "𞤈" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); lineTo(0.7f, 0.3f) }, Offset(0.5f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f), Offset(0.7f, 0.3f))
    ),
    "𞤉" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.7f, 0.2f); lineTo(0.7f, 0.5f); lineTo(0.3f, 0.5f); lineTo(0.3f, 0.8f); lineTo(0.7f, 0.8f) }, Offset(0.3f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.7f, 0.2f), Offset(0.7f, 0.5f), Offset(0.3f, 0.5f), Offset(0.3f, 0.8f), Offset(0.7f, 0.8f))
    ),
    "𞤊" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.3f, 0.8f); lineTo(0.7f, 0.5f) }, Offset(0.3f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.5f); lineTo(0.7f, 0.2f) }, Offset(0.3f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f), Offset(0.7f, 0.5f), Offset(0.3f, 0.5f), Offset(0.7f, 0.2f))
    ),
    "𞤋" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.7f, 0.2f) }, Offset(0.3f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.8f); lineTo(0.7f, 0.8f) }, Offset(0.3f, 0.8f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f), Offset(0.3f, 0.2f), Offset(0.7f, 0.2f), Offset(0.3f, 0.8f), Offset(0.7f, 0.8f))
    ),
    "𞤌" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); addOval(Rect(0.3f, 0.3f, 0.7f, 0.7f)) }, Offset(0.5f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.7f, 0.5f), Offset(0.5f, 0.7f), Offset(0.3f, 0.5f), Offset(0.5f, 0.3f))
    ),
    "𞤍" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.8f); cubicTo(0.6f, 0.7f, 0.8f, 0.7f, 0.8f, 0.8f) }, Offset(0.5f, 0.8f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f), Offset(0.8f, 0.8f))
    ),
    "𞤎" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.8f); lineTo(0.3f, 0.2f); lineTo(0.7f, 0.2f); lineTo(0.7f, 0.8f) }, Offset(0.3f, 0.8f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.8f), Offset(0.3f, 0.2f), Offset(0.7f, 0.2f), Offset(0.7f, 0.8f))
    ),
    "𞤏" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.3f, 0.8f); lineTo(0.5f, 0.5f); lineTo(0.7f, 0.8f); lineTo(0.7f, 0.2f) }, Offset(0.3f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f), Offset(0.5f, 0.5f), Offset(0.7f, 0.8f), Offset(0.7f, 0.2f))
    ),
    "𞤐" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.3f, 0.4f) }, Offset(0.5f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.7f, 0.4f) }, Offset(0.5f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f), Offset(0.3f, 0.4f), Offset(0.7f, 0.4f))
    ),
    "𞤑" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.5f); lineTo(0.5f, 0.3f); lineTo(0.7f, 0.5f) }, Offset(0.3f, 0.5f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f), Offset(0.3f, 0.5f), Offset(0.5f, 0.3f), Offset(0.7f, 0.5f))
    ),
    "𞤒" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.5f, 0.5f); lineTo(0.7f, 0.2f) }, Offset(0.3f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.5f, 0.5f), Offset(0.7f, 0.2f), Offset(0.5f, 0.8f))
    ),
    "𞤓" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.3f, 0.8f); lineTo(0.7f, 0.8f); lineTo(0.7f, 0.2f) }, Offset(0.3f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f), Offset(0.7f, 0.8f), Offset(0.7f, 0.2f))
    ),
    "𞤔" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.2f); lineTo(0.7f, 0.8f); lineTo(0.3f, 0.8f) }, Offset(0.7f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.7f, 0.2f), Offset(0.7f, 0.8f), Offset(0.3f, 0.8f))
    ),
    "𞤕" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.7f, 0.2f) }, Offset(0.3f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.7f, 0.2f), Offset(0.5f, 0.2f), Offset(0.5f, 0.8f))
    ),
    "𞤖" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.3f, 0.8f) }, Offset(0.3f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.2f); lineTo(0.7f, 0.8f) }, Offset(0.7f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.5f); lineTo(0.7f, 0.5f) }, Offset(0.3f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f), Offset(0.7f, 0.2f), Offset(0.7f, 0.8f), Offset(0.3f, 0.5f), Offset(0.7f, 0.5f))
    ),
    "𞤗" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); addOval(Rect(0.3f, 0.3f, 0.7f, 0.7f)) }, Offset(0.5f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.6f, 0.6f); lineTo(0.8f, 0.8f) }, Offset(0.6f, 0.6f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.7f, 0.5f), Offset(0.5f, 0.7f), Offset(0.3f, 0.5f), Offset(0.5f, 0.3f), Offset(0.6f, 0.6f), Offset(0.8f, 0.8f))
    ),
    "𞤘" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.3f); cubicTo(0.5f, 0.1f, 0.3f, 0.3f, 0.3f, 0.5f); cubicTo(0.3f, 0.7f, 0.5f, 0.9f, 0.7f, 0.7f); lineTo(0.6f, 0.7f) }, Offset(0.7f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.7f, 0.3f), Offset(0.3f, 0.5f), Offset(0.7f, 0.7f), Offset(0.6f, 0.7f))
    ),
    "𞤙" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.7f, 0.8f) }, Offset(0.3f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.2f); lineTo(0.3f, 0.8f) }, Offset(0.7f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.7f, 0.8f), Offset(0.7f, 0.2f), Offset(0.3f, 0.8f))
    ),
    "𞤚" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.8f); lineTo(0.3f, 0.2f); lineTo(0.7f, 0.2f); lineTo(0.7f, 0.8f) }, Offset(0.3f, 0.8f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.5f); lineTo(0.7f, 0.5f) }, Offset(0.3f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.8f), Offset(0.3f, 0.2f), Offset(0.7f, 0.2f), Offset(0.7f, 0.8f), Offset(0.3f, 0.5f), Offset(0.7f, 0.5f))
    ),
    "𞤛" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.5f, 0.8f); lineTo(0.7f, 0.2f) }, Offset(0.3f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.5f, 0.8f), Offset(0.7f, 0.2f))
    ),

    // Lowercase
    "𞤢" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.3f, 0.7f); lineTo(0.7f, 0.7f); lineTo(0.5f, 0.3f) }, Offset(0.5f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.3f, 0.7f), Offset(0.7f, 0.7f), Offset(0.5f, 0.3f))
    ),
    "𞤣" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); cubicTo(0.6f, 0.4f, 0.8f, 0.4f, 0.8f, 0.5f); cubicTo(0.8f, 0.6f, 0.6f, 0.6f, 0.5f, 0.5f) }, Offset(0.5f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f), Offset(0.5f, 0.5f), Offset(0.8f, 0.5f))
    ),
    "𞤤" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.3f); lineTo(0.3f, 0.7f); lineTo(0.7f, 0.7f) }, Offset(0.3f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
    ),
    "𞤥" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.7f); lineTo(0.3f, 0.3f); lineTo(0.5f, 0.5f); lineTo(0.7f, 0.3f); lineTo(0.7f, 0.7f) }, Offset(0.3f, 0.7f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.7f), Offset(0.3f, 0.3f), Offset(0.5f, 0.5f), Offset(0.7f, 0.3f), Offset(0.7f, 0.7f))
    ),
    "𞤦" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); cubicTo(0.4f, 0.4f, 0.2f, 0.4f, 0.2f, 0.3f) }, Offset(0.5f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f), Offset(0.2f, 0.3f))
    ),
    "𞤧" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.3f); cubicTo(0.5f, 0.5f, 0.5f, 0.7f, 0.7f, 0.9f) }, Offset(0.7f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.3f); cubicTo(0.5f, 0.5f, 0.5f, 0.7f, 0.3f, 0.9f) }, Offset(0.3f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.7f, 0.3f), Offset(0.7f, 0.9f), Offset(0.3f, 0.3f), Offset(0.3f, 0.9f))
    ),
    "𞤨" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); cubicTo(0.6f, 0.4f, 0.8f, 0.4f, 0.8f, 0.3f) }, Offset(0.5f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f), Offset(0.8f, 0.3f))
    ),
    "𞤩" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.7f); cubicTo(0.4f, 0.6f, 0.2f, 0.6f, 0.2f, 0.7f) }, Offset(0.5f, 0.7f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f), Offset(0.2f, 0.7f))
    ),
    "𞤪" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); lineTo(0.7f, 0.4f) }, Offset(0.5f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f), Offset(0.7f, 0.4f))
    ),
    "𞤫" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.3f); lineTo(0.7f, 0.3f); lineTo(0.7f, 0.5f); lineTo(0.3f, 0.5f); lineTo(0.3f, 0.7f); lineTo(0.7f, 0.7f) }, Offset(0.3f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f), Offset(0.7f, 0.5f), Offset(0.3f, 0.5f), Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
    ),
    "𞤬" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.3f); lineTo(0.3f, 0.7f); lineTo(0.7f, 0.5f) }, Offset(0.3f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.5f); lineTo(0.7f, 0.3f) }, Offset(0.3f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.7f), Offset(0.7f, 0.5f), Offset(0.3f, 0.5f), Offset(0.7f, 0.3f))
    ),
    "𞤭" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.3f); lineTo(0.7f, 0.3f) }, Offset(0.3f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.7f); lineTo(0.7f, 0.7f) }, Offset(0.3f, 0.7f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f), Offset(0.3f, 0.3f), Offset(0.7f, 0.3f), Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
    ),
    "𞤮" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); addOval(Rect(0.3f, 0.4f, 0.7f, 0.6f)) }, Offset(0.5f, 0.4f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.4f), Offset(0.7f, 0.5f), Offset(0.5f, 0.6f), Offset(0.3f, 0.5f), Offset(0.5f, 0.4f))
    ),
    "𞤯" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.7f); cubicTo(0.6f, 0.6f, 0.8f, 0.6f, 0.8f, 0.7f) }, Offset(0.5f, 0.7f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f), Offset(0.8f, 0.7f))
    ),
    "𞤰" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.7f); lineTo(0.3f, 0.3f); lineTo(0.7f, 0.3f); lineTo(0.7f, 0.7f) }, Offset(0.3f, 0.7f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.7f), Offset(0.3f, 0.3f), Offset(0.7f, 0.3f), Offset(0.7f, 0.7f))
    ),
    "𞤱" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.3f); lineTo(0.3f, 0.7f); lineTo(0.5f, 0.5f); lineTo(0.7f, 0.7f); lineTo(0.7f, 0.3f) }, Offset(0.3f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.7f), Offset(0.5f, 0.5f), Offset(0.7f, 0.7f), Offset(0.7f, 0.3f))
    ),
    "𞤲" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.3f, 0.5f) }, Offset(0.5f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.7f, 0.5f) }, Offset(0.5f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f), Offset(0.3f, 0.5f), Offset(0.7f, 0.5f))
    ),
    "𞤳" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.5f); lineTo(0.5f, 0.4f); lineTo(0.7f, 0.5f) }, Offset(0.3f, 0.5f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.4f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.4f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f), Offset(0.3f, 0.5f), Offset(0.5f, 0.4f), Offset(0.7f, 0.5f))
    ),
    "𞤴" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.3f); lineTo(0.5f, 0.5f); lineTo(0.7f, 0.3f) }, Offset(0.3f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.3f), Offset(0.5f, 0.5f), Offset(0.7f, 0.3f), Offset(0.5f, 0.7f))
    ),
    "𞤵" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.3f); lineTo(0.3f, 0.7f); lineTo(0.7f, 0.7f); lineTo(0.7f, 0.3f) }, Offset(0.3f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.7f), Offset(0.7f, 0.7f), Offset(0.7f, 0.3f))
    ),
    "𞤶" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.3f); lineTo(0.7f, 0.7f); lineTo(0.3f, 0.7f) }, Offset(0.7f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.7f, 0.3f), Offset(0.7f, 0.7f), Offset(0.3f, 0.7f))
    ),
    "𞤷" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.3f); lineTo(0.7f, 0.3f) }, Offset(0.3f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.3f); lineTo(0.5f, 0.7f) }, Offset(0.5f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f), Offset(0.5f, 0.3f), Offset(0.5f, 0.7f))
    ),
    "𞤸" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.3f); lineTo(0.3f, 0.7f) }, Offset(0.3f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.3f); lineTo(0.7f, 0.7f) }, Offset(0.7f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.5f); lineTo(0.7f, 0.5f) }, Offset(0.3f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.7f), Offset(0.7f, 0.3f), Offset(0.7f, 0.7f), Offset(0.3f, 0.5f), Offset(0.7f, 0.5f))
    ),
    "𞤹" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); addOval(Rect(0.3f, 0.4f, 0.7f, 0.6f)) }, Offset(0.5f, 0.4f)),
            StrokeGuideSegment(Path().apply { moveTo(0.6f, 0.6f); lineTo(0.8f, 0.8f) }, Offset(0.6f, 0.6f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.4f), Offset(0.7f, 0.5f), Offset(0.5f, 0.6f), Offset(0.3f, 0.5f), Offset(0.5f, 0.4f), Offset(0.6f, 0.6f), Offset(0.8f, 0.8f))
    ),
    "𞤺" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.4f); cubicTo(0.5f, 0.2f, 0.3f, 0.4f, 0.3f, 0.6f); cubicTo(0.3f, 0.8f, 0.5f, 1.0f, 0.7f, 0.8f); lineTo(0.6f, 0.8f) }, Offset(0.7f, 0.4f))
        ),
        checkpoints = listOf(Offset(0.7f, 0.4f), Offset(0.3f, 0.6f), Offset(0.7f, 0.8f), Offset(0.6f, 0.8f))
    ),
    "𞤻" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.3f); lineTo(0.7f, 0.7f) }, Offset(0.3f, 0.3f)),
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.3f); lineTo(0.3f, 0.7f) }, Offset(0.7f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.7f), Offset(0.7f, 0.3f), Offset(0.3f, 0.7f))
    ),
    "𞤼" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.7f); lineTo(0.3f, 0.3f); lineTo(0.7f, 0.3f); lineTo(0.7f, 0.7f) }, Offset(0.3f, 0.7f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.5f); lineTo(0.7f, 0.5f) }, Offset(0.3f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.7f), Offset(0.3f, 0.3f), Offset(0.7f, 0.3f), Offset(0.7f, 0.7f), Offset(0.3f, 0.5f), Offset(0.7f, 0.5f))
    ),

    // Numbers
    "𞥐" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); addOval(Rect(0.3f, 0.3f, 0.7f, 0.7f)) }, Offset(0.5f, 0.3f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.3f), Offset(0.7f, 0.5f), Offset(0.5f, 0.7f), Offset(0.3f, 0.5f), Offset(0.5f, 0.3f))
    ),
    "𞥑" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f))
    ),
    "𞥒" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.7f, 0.2f); lineTo(0.5f, 0.8f); lineTo(0.3f, 0.8f) }, Offset(0.3f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.7f, 0.2f), Offset(0.5f, 0.8f), Offset(0.3f, 0.8f))
    ),
    "𞥓" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.7f, 0.5f); lineTo(0.3f, 0.8f) }, Offset(0.3f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.5f); lineTo(0.5f, 0.5f) }, Offset(0.7f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.7f, 0.5f), Offset(0.3f, 0.8f), Offset(0.5f, 0.5f))
    ),
    "𞥔" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.5f); lineTo(0.7f, 0.5f) }, Offset(0.3f, 0.5f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.5f), Offset(0.7f, 0.5f), Offset(0.5f, 0.2f), Offset(0.5f, 0.8f))
    ),
    "𞥕" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.2f); lineTo(0.3f, 0.2f); lineTo(0.3f, 0.5f); lineTo(0.7f, 0.5f); lineTo(0.7f, 0.8f); lineTo(0.3f, 0.8f) }, Offset(0.7f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.7f, 0.2f), Offset(0.3f, 0.2f), Offset(0.3f, 0.5f), Offset(0.7f, 0.5f), Offset(0.7f, 0.8f), Offset(0.3f, 0.8f))
    ),
    "𞥖" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.2f); lineTo(0.3f, 0.5f); lineTo(0.7f, 0.8f) }, Offset(0.7f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.5f); lineTo(0.5f, 0.5f) }, Offset(0.3f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.7f, 0.2f), Offset(0.3f, 0.5f), Offset(0.7f, 0.8f), Offset(0.5f, 0.5f))
    ),
    "𞥗" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.2f); lineTo(0.7f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.3f, 0.2f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.2f), Offset(0.7f, 0.2f), Offset(0.5f, 0.8f))
    ),
    "𞥘" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); addOval(Rect(0.3f, 0.2f, 0.7f, 0.5f)) }, Offset(0.5f, 0.2f)),
            StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.5f); addOval(Rect(0.3f, 0.5f, 0.7f, 0.8f)) }, Offset(0.5f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.7f, 0.35f), Offset(0.5f, 0.5f), Offset(0.3f, 0.35f), Offset(0.5f, 0.5f), Offset(0.7f, 0.65f), Offset(0.5f, 0.8f), Offset(0.3f, 0.65f))
    ),
    "𞥙" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.8f); lineTo(0.7f, 0.5f); lineTo(0.3f, 0.2f) }, Offset(0.3f, 0.8f)),
            StrokeGuideSegment(Path().apply { moveTo(0.7f, 0.5f); lineTo(0.5f, 0.5f) }, Offset(0.7f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.8f), Offset(0.7f, 0.5f), Offset(0.3f, 0.2f), Offset(0.5f, 0.5f))
    )
)

// --- Styles pour le Guide ---
object GuideStyles {
    val GuideColor = Color.Gray.copy(alpha = 0.6f)
    val GuideStrokeWidth = 5f
    val StartIndicatorRadius = 10f
    val StartIndicatorColor = Color.Green.copy(alpha = 0.5f)
    val CheckpointVizRadius = 15f // Pour visualiser les checkpoints (optionnel)
    val CheckpointVizColor = Color.Red.copy(alpha = 0.4f)
    val DashedPathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
    val DashedStroke = Stroke(width = GuideStrokeWidth, pathEffect = DashedPathEffect, cap = StrokeCap.Round)
    val SolidStroke = Stroke(width = GuideStrokeWidth, cap = StrokeCap.Round)
}

// --- Composable Principal ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingPracticeScreen(
    navController: NavController,
    writingType: WritingType
) {
    val characters = when (writingType) {
        WritingType.UPPERCASE -> AdlamCharacters.uppercase
        WritingType.LOWERCASE -> AdlamCharacters.lowercase
        WritingType.NUMBERS -> AdlamCharacters.numbers
    }
    val topBarTitleRes = when (writingType) {
        WritingType.UPPERCASE -> R.string.writing_uppercase
        WritingType.LOWERCASE -> R.string.writing_lowercase
        WritingType.NUMBERS -> R.string.writing_numbers
    }
    val topBarTitle = stringResource(topBarTitleRes)

    // --- États ---
    var currentIndex by remember { mutableIntStateOf(0) }
    val paintColor = remember { mutableStateOf(Color.Black) }
    val brushSize = remember { mutableFloatStateOf(20f) }
    val brushAlpha = remember { mutableFloatStateOf(1f) }
    val paths = remember { mutableStateListOf<Pair<Path, PaintAttributes>>() }
    val currentPath = remember { mutableStateOf(Path()) }
    var showBrushSettingsDialog by remember { mutableStateOf(false) }
    var showStrokeGuide by remember { mutableStateOf(true) } // Guide affiché par défaut?
    var writingMode by remember { mutableStateOf(WritingMode.SEPARATE) } // Mode d'écriture
    var guidanceMode by remember { mutableStateOf(GuidanceMode.VISUAL_ONLY) } // Mode de guidage
    var fingerPosition by remember { mutableStateOf(Offset.Zero) } // Position du doigt
    var isTracing by remember { mutableStateOf(false) } // En cours de traçage
    // États pour l'évaluation
    var evaluationScore by remember { mutableFloatStateOf(-1f) } // -1f = non évalué
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var canvasSize by remember { mutableStateOf(Size.Zero) } // Pour obtenir la taille du canvas
    var missedCheckpoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    // Animation
    val animationProgress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    // Couleurs pour le feedback du tracé

    // Couleurs pour le feedback du tracé
    val correctTraceColor = Color(0xFF388E3C) // Vert
    val mediumTraceColor = Color(0xFFFFA000) // Orange
    val incorrectTraceColor = Color(0xFFD32F2F) // Rouge

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Ajout pour les messages de score
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    // Bouton pour le mode de guidage du doigt
                    IconButton(onClick = { 
                        guidanceMode = when (guidanceMode) {
                            GuidanceMode.NO_GUIDANCE -> GuidanceMode.VISUAL_ONLY
                            GuidanceMode.VISUAL_ONLY -> GuidanceMode.FINGER_TRACING
                            GuidanceMode.FINGER_TRACING -> GuidanceMode.NO_GUIDANCE
                        }
                    }) {
                        Icon(
                            imageVector = when (guidanceMode) {
                                GuidanceMode.NO_GUIDANCE -> Icons.Filled.TouchApp
                                GuidanceMode.VISUAL_ONLY -> Icons.Filled.Visibility
                                GuidanceMode.FINGER_TRACING -> Icons.Filled.PanTool
                            },
                            contentDescription = "Mode de guidage"
                        )
                    }
                    // Bouton pour basculer le mode d'écriture
                    IconButton(onClick = { writingMode = if (writingMode == WritingMode.SEPARATE) WritingMode.CURSIVE else WritingMode.SEPARATE }) {
                        Icon(
                            imageVector = if (writingMode == WritingMode.CURSIVE) Icons.Filled.Link else Icons.Filled.LinkOff,
                            contentDescription = if (writingMode == WritingMode.CURSIVE) "Mode lettres séparées" else "Mode écriture cursive"
                        )
                    }
                    // Bouton pour afficher/masquer le guide (si défini pour le caractère)
                    val currentCharacter = characters.getOrNull(currentIndex)
                    if (currentCharacter != null && adlamCharacterGuides.containsKey(currentCharacter)) {
                        IconButton(onClick = { showStrokeGuide = !showStrokeGuide }) {
                            Icon(
                                imageVector = if (showStrokeGuide) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = stringResource(if (showStrokeGuide) R.string.hide_guide else R.string.show_guide)
                            )
                        }
                    }
                    // Bouton pour rejouer l'animation du tracé
                    IconButton(onClick = {
                        coroutineScope.launch {
                            animationProgress.snapTo(0f)
                            animationProgress.animateTo(1f, animationSpec = tween(durationMillis = 1500))
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Rejouer l'animation"
                        )
                    }
                    // Bouton pour les paramètres du pinceau
                    IconButton(onClick = { showBrushSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Brush,
                            contentDescription = stringResource(R.string.brush_settings_title)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer // Couleur pour les icônes d'action
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (currentIndex > 0) { currentIndex--; paths.clear(); evaluationScore = -1f } }, // Reset score
                        enabled = currentIndex > 0
                    ) { Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.previous)) }

                    // Bouton Effacer (Clear)
                    IconButton(onClick = { paths.clear(); currentPath.value = Path(); evaluationScore = -1f }) { // Reset score
                        Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.clear))
                    }

                    // Peut-être un bouton "Vérifier" explicite ici ?
                    // IconButton(onClick = { /* Lancer l'évaluation manuellement */ }) { ... }

                    IconButton(
                        onClick = { if (currentIndex < characters.size - 1) { currentIndex++; paths.clear(); evaluationScore = -1f } }, // Reset score
                        enabled = currentIndex < characters.size - 1
                    ) { Icon(Icons.Filled.ArrowForward, contentDescription = stringResource(R.string.next)) }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Applique le padding du Scaffold
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    // Support RTL pour l'écriture Adlam
                    // Support RTL via CompositionLocal si nécessaire
                    // Récupère la taille disponible pour le Canvas
                    .onSizeChanged { size -> canvasSize = Size(size.width.toFloat(), size.height.toFloat()) }
            ) {
                // Caractère en arrière-plan
                val currentCharacter = characters.getOrNull(currentIndex)
                if (currentCharacter != null) {
                    Text(
                        text = currentCharacter,
                        fontSize = 300.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Canvas pour le guide (si activé et défini)
                if (showStrokeGuide && currentCharacter != null && adlamCharacterGuides.containsKey(currentCharacter) && canvasSize != Size.Zero) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        when (guidanceMode) {
                            GuidanceMode.VISUAL_ONLY -> {
                                drawStrokeGuide(currentCharacter)
                                drawAnimatedGuide(currentCharacter, animationProgress.value)
                            }
                            GuidanceMode.FINGER_TRACING -> {
                                drawFingerGuidance(currentCharacter, fingerPosition, isTracing)
                                drawAnimatedGuide(currentCharacter, animationProgress.value)
                            }
                            GuidanceMode.NO_GUIDANCE -> {
                                // Pas de guide visuel
                            }
                        }
                    }
                }

                // Canvas pour le dessin utilisateur et évaluation
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    evaluationScore = -1f // Réinitialise le score
                                    currentPath.value = Path().apply { moveTo(offset.x, offset.y) }
                                    fingerPosition = offset
                                    isTracing = true
                                },
                                onDrag = { change, _ ->
                                    fingerPosition = change.position
                                    // Utilise la méthode de création d'un nouveau Path pour assurer la mise à jour
                                    currentPath.value = Path().apply {
                                        addPath(currentPath.value)
                                        lineTo(change.position.x, change.position.y)
                                    }
                                    
                                    // Vérification du suivi du chemin pour le mode finger tracing
                                    if (guidanceMode == GuidanceMode.FINGER_TRACING) {
                                        checkFingerPathAccuracy(change.position, currentCharacter, canvasSize)
                                    }
                                },
                                onDragEnd = {
                                    isTracing = false
                                    fingerPosition = Offset.Zero
                                    
                                    val finalPath = currentPath.value
                                    if (!finalPath.isEmpty) { // Seulement si quelque chose a été dessiné
                                        val guideData = currentCharacter?.let { adlamCharacterGuides[it] }
                                        if (guideData != null && guideData.checkpoints.isNotEmpty() && canvasSize != Size.Zero) {
                                            val (score, missed) = evaluateTraceWithCheckpoints(
                                                userPath = finalPath,
                                                targetCheckpoints = guideData.checkpoints,
                                                canvasSize = canvasSize,
                                                toleranceRadius = if (guidanceMode == GuidanceMode.FINGER_TRACING) 25f else 35f
                                            )
                                            evaluationScore = score
                                            missedCheckpoints = missed

                                            val traceColor = when {
                                                score >= 0.85f -> correctTraceColor
                                                score >= 0.5f -> mediumTraceColor
                                                else -> incorrectTraceColor
                                            }
                                            paths.add(Pair(finalPath, PaintAttributes(traceColor, brushSize.value, brushAlpha.value)))

                                            scope.launch {
                                                val percentage = (score * 100).toInt()
                                                val message = if (guidanceMode == GuidanceMode.FINGER_TRACING) {
                                                    when {
                                                        score >= 0.9f -> "Excellent suivi ! $percentage%"
                                                        score >= 0.7f -> "Bon suivi du doigt : $percentage%"
                                                        else -> "Suivez mieux le guide : $percentage%"
                                                    }
                                                } else {
                                                    "Précision : $percentage%"
                                                }
                                                snackbarHostState.showSnackbar(
                                                    message = message,
                                                    withDismissAction = true
                                                )
                                            }
                                        } else {
                                            paths.add(Pair(finalPath, PaintAttributes(paintColor.value, brushSize.value, brushAlpha.value)))
                                            evaluationScore = -1f
                                            missedCheckpoints = emptyList()
                                        }
                                    }
                                    currentPath.value = Path()
                                }
                            )
                        }
                ) {
                    // Dessine les tracés précédents (permanents)
                    drawPaths(paths)
                    // Dessine le tracé en cours (pendant le glissement)
                    if (!currentPath.value.isEmpty) {
                        drawPath(
                            path = currentPath.value,
                            color = paintColor.value.copy(alpha = brushAlpha.value),
                            style = Stroke(width = brushSize.value, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }

                    // Dessine les checkpoints manqués
                    drawMissedCheckpoints(missedCheckpoints)
                } // Fin Canvas Dessin

                // Affichage textuel du score (optionnel)
                if (evaluationScore >= 0) {
                    Text(
                        text = "Score: ${(evaluationScore * 100).toInt()}%",
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp), // En haut à droite
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }

            } // Fin Box
        } // Fin Column

        // Boîte de dialogue pour les paramètres du pinceau
        if (showBrushSettingsDialog) {
            BrushSettingsDialog(
                initialBrushSize = brushSize.value,
                initialColor = paintColor.value,
                initialAlpha = brushAlpha.value,
                onBrushSizeChange = { brushSize.value = it },
                onColorChange = { paintColor.value = it },
                onAlphaChange = { brushAlpha.value = it },
                onDismiss = { showBrushSettingsDialog = false }
            )
        }
    } // Fin Scaffold
} // Fin Composable WritingPracticeScreen


// --- Fonctions Utilitaires ---

// Data class pour les attributs de peinture (inchangée)
data class PaintAttributes(val color: Color, val brushSize: Float, val alpha: Float)

// Fonction pour dessiner les chemins sauvegardés (inchangée)
fun DrawScope.drawPaths(paths: List<Pair<Path, PaintAttributes>>) {
    paths.forEach { (path, attributes) ->
        drawPath(
            path = path,
            color = attributes.color.copy(alpha = attributes.alpha),
            style = Stroke(width = attributes.brushSize, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

// Fonction pour dessiner les checkpoints manqués
fun DrawScope.drawMissedCheckpoints(missedCheckpoints: List<Offset>) {
    missedCheckpoints.forEach { checkpoint ->
        val crossSize = 15f
        // Dessine une croix 'X' rouge
        drawLine(Color.Red, Offset(checkpoint.x - crossSize, checkpoint.y - crossSize), Offset(checkpoint.x + crossSize, checkpoint.y + crossSize), strokeWidth = 5f)
        drawLine(Color.Red, Offset(checkpoint.x + crossSize, checkpoint.y - crossSize), Offset(checkpoint.x - crossSize, checkpoint.y + crossSize), strokeWidth = 5f)
    }
}

// Fonction pour dessiner le guide (MISE À JOUR)
// Fonction pour dessiner le guide (CORRIGÉE)
fun DrawScope.drawStrokeGuide(letter: String) {
    val guideData = adlamCharacterGuides[letter]
    val canvasWidth = size.width
    val canvasHeight = size.height

    if (guideData != null && canvasWidth > 0 && canvasHeight > 0) {
        val transformationMatrix = Matrix().apply { scale(canvasWidth, canvasHeight) }

        // 1. Dessine les segments du chemin guide
        guideData.segments.forEachIndexed { index, segment ->

            // --- CORRECTION DE LA TRANSFORMATION ---
            // a. Crée un nouveau Path vide pour le résultat transformé.
            val transformedPath = Path()
            // b. Copie le contenu du chemin original du segment dans le nouveau Path.
            transformedPath.addPath(segment.path)
            // c. Applique la transformation sur la *copie* (transformedPath).
            transformedPath.transform(transformationMatrix)
            // --- FIN DE LA CORRECTION ---

            // d. Dessine le chemin qui a été transformé.
            drawPath(
                path = transformedPath, // Utilise maintenant le chemin correctement transformé
                color = GuideStyles.GuideColor,
                style = if (segment.isDash) GuideStyles.DashedStroke else GuideStyles.SolidStroke
            )

            // 2. Dessine l'indicateur de départ (logique inchangée)
            if (index == 0 && segment.startPoint != null) {
                // La transformation du point de départ était déjà correcte
                val absoluteStartPoint = transformationMatrix.map(segment.startPoint)
                drawCircle(
                    color = GuideStyles.StartIndicatorColor,
                    radius = GuideStyles.StartIndicatorRadius,
                    center = absoluteStartPoint
                )
            }
        }

        // 3. Visualisation des checkpoints (logique inchangée)
        val showCheckpointsForDebug = false // Mettre à true pour voir les zones rouges
        if (showCheckpointsForDebug) {
            guideData.checkpoints.forEach { checkpoint ->
                // La transformation du checkpoint était déjà correcte
                val absoluteCheckpoint = transformationMatrix.map(checkpoint)
                drawCircle(
                    color = GuideStyles.CheckpointVizColor,
                    radius = GuideStyles.CheckpointVizRadius,
                    center = absoluteCheckpoint,
                    style = Stroke(2f)
                )
            }
        }
    } else if (guideData == null) {
        println("Warning: Guide non défini pour '$letter'")
    }
}


fun DrawScope.drawAnimatedGuide(letter: String, progress: Float) {
    val guideData = adlamCharacterGuides[letter]
    val canvasWidth = size.width
    val canvasHeight = size.height

    if (guideData != null && canvasWidth > 0 && canvasHeight > 0) {
        val transformationMatrix = Matrix().apply { scale(canvasWidth, canvasHeight) }

        guideData.segments.forEach { segment ->
            val transformedPath = Path()
            segment.path.transform(transformationMatrix)
            transformedPath.addPath(segment.path)
            transformedPath.addPath(segment.path)

            val pathMeasure = PathMeasure()
            pathMeasure.setPath(transformedPath, false)

            val totalLength = pathMeasure.length
            val animatedPath = Path()
            pathMeasure.getSegment(0f, totalLength * progress, animatedPath, true)

            drawPath(
                path = animatedPath,
                color = GuideStyles.StartIndicatorColor,
                style = GuideStyles.SolidStroke
            )
        }
    }
}

// Fonction pour dessiner le guidage du doigt
fun DrawScope.drawFingerGuidance(letter: String, fingerPos: Offset, isTracing: Boolean) {
    val guideData = adlamCharacterGuides[letter]
    val canvasWidth = size.width
    val canvasHeight = size.height

    if (guideData != null && canvasWidth > 0 && canvasHeight > 0) {
        val transformationMatrix = Matrix().apply { scale(canvasWidth, canvasHeight) }

        // Dessiner le chemin guide en version atténuée
        guideData.segments.forEach { segment ->
            val transformedPath = Path()
            transformedPath.addPath(segment.path)
            transformedPath.transform(transformationMatrix)

            drawPath(
                path = transformedPath,
                color = GuideStyles.GuideColor.copy(alpha = 0.3f),
                style = Stroke(width = GuideStyles.GuideStrokeWidth + 10f, cap = StrokeCap.Round)
            )
        }

        // Dessiner l'indicateur de position du doigt
        if (isTracing && fingerPos != Offset.Zero) {
            drawCircle(
                color = Color.Blue.copy(alpha = 0.6f),
                radius = 25f,
                center = fingerPos,
                style = Stroke(width = 3f)
            )
            
            drawCircle(
                color = Color.Blue.copy(alpha = 0.3f),
                radius = 15f,
                center = fingerPos
            )
        }

        // Dessiner les points de guidage sur le chemin
        val absoluteCheckpoints = guideData.checkpoints.map { transformationMatrix.map(it) }
        absoluteCheckpoints.forEachIndexed { index, checkpoint ->
            val distance = if (fingerPos != Offset.Zero) {
                kotlin.math.sqrt(
                    (fingerPos.x - checkpoint.x) * (fingerPos.x - checkpoint.x) + 
                    (fingerPos.y - checkpoint.y) * (fingerPos.y - checkpoint.y)
                )
            } else Float.MAX_VALUE
            
            val isNear = distance < 50f
            val color = if (isNear && isTracing) Color.Green else Color.Gray
            val radius = if (isNear && isTracing) 12f else 8f
            
            drawCircle(
                color = color.copy(alpha = 0.7f),
                radius = radius,
                center = checkpoint
            )
            
            // Numéroter les points pour indiquer l'ordre
            drawCircle(
                color = Color.White,
                radius = radius - 2f,
                center = checkpoint
            )
        }
    }
}

// Vérification de la précision du suivi du doigt
fun checkFingerPathAccuracy(fingerPos: Offset, currentCharacter: String?, canvasSize: Size) {
    if (currentCharacter == null || canvasSize == Size.Zero) return
    
    val guideData = adlamCharacterGuides[currentCharacter] ?: return
    val transformationMatrix = Matrix().apply { scale(canvasSize.width, canvasSize.height) }
    val absoluteCheckpoints = guideData.checkpoints.map { transformationMatrix.map(it) }
    
    // Vérifier si le doigt suit correctement le chemin
    val nearestCheckpoint = absoluteCheckpoints.minByOrNull { checkpoint ->
        kotlin.math.sqrt(
            (fingerPos.x - checkpoint.x) * (fingerPos.x - checkpoint.x) + 
            (fingerPos.y - checkpoint.y) * (fingerPos.y - checkpoint.y)
        )
    }
    
    // Ici on pourrait ajouter un feedback haptique ou sonore
    // pour guider l'utilisateur
}

// Fonction d'évaluation par checkpoints (NOUVELLE)
fun evaluateTraceWithCheckpoints(
    userPath: Path,
    targetCheckpoints: List<Offset>,
    canvasSize: Size,
    toleranceRadius: Float = 35f
): Pair<Float, List<Offset>> {
    if (targetCheckpoints.isEmpty()) return Pair(1f, emptyList())
    if (userPath.isEmpty || canvasSize == Size.Zero) return Pair(0f, targetCheckpoints)

    val transformationMatrix = Matrix().apply { scale(canvasSize.width, canvasSize.height) }
    val absoluteCheckpoints = targetCheckpoints.map { transformationMatrix.map(it) }

    val pathMeasure = PathMeasure()
    pathMeasure.setPath(userPath, false)
    val pathLength = pathMeasure.length
    if (pathLength == 0f) return Pair(0f, absoluteCheckpoints)

    val reachedCheckpoints = mutableSetOf<Offset>()
    val checkpointToleranceSquared = toleranceRadius.pow(2)

    var distanceAlongPath = 0f
    val step = 5f

    while (distanceAlongPath <= pathLength) {
        val currentPoint: Offset = pathMeasure.getPosition(distanceAlongPath)

        absoluteCheckpoints.forEach { checkpoint ->
            val dx = currentPoint.x - checkpoint.x
            val dy = currentPoint.y - checkpoint.y
            val distanceSquared = dx * dx + dy * dy

            if (distanceSquared <= checkpointToleranceSquared) {
                reachedCheckpoints.add(checkpoint)
            }
        }
        distanceAlongPath += step
    }

    val score = reachedCheckpoints.size.toFloat() / absoluteCheckpoints.size.toFloat()
    val missedCheckpoints = absoluteCheckpoints.filter { it !in reachedCheckpoints }

    return Pair(score, missedCheckpoints)
}


// --- Boîte de dialogue Paramètres Pinceau (inchangée structurellement) ---
@Composable
fun BrushSettingsDialog(
    initialBrushSize: Float, initialColor: Color, initialAlpha: Float,
    onBrushSizeChange: (Float) -> Unit, onColorChange: (Color) -> Unit, onAlphaChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var brushSize by remember { mutableFloatStateOf(initialBrushSize) }
    var selectedColor by remember { mutableStateOf(initialColor) }
    var brushAlpha by remember { mutableFloatStateOf(initialAlpha) }
    val colors = remember { listOf(Color.Black, Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color(0xFFFFA500) /*Orange*/, Color.Gray) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.brush_settings_title), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Taille
                Text(stringResource(R.string.brush_size, brushSize.toInt()), style = MaterialTheme.typography.titleMedium)
                Slider(value = brushSize, onValueChange = { brushSize = it }, valueRange = 5f..50f, steps = 8)
                // Couleur
                Text(stringResource(R.string.brush_color), style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = if (selectedColor == color) MaterialTheme.colorScheme.outline else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
                // Opacité
                Text(stringResource(R.string.brush_alpha, (brushAlpha * 100).toInt()), style = MaterialTheme.typography.titleMedium)
                Slider(value = brushAlpha, onValueChange = { brushAlpha = it }, valueRange = 0.1f..1f)
            }
        },
        confirmButton = {
            Button(onClick = {
                onBrushSizeChange(brushSize); onColorChange(selectedColor); onAlphaChange(brushAlpha); onDismiss()
            }) { Text(stringResource(R.string.confirm)) }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        }
    )
}

// --- Previews ---
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Practice Uppercase Dark")
@Composable
fun WritingPracticeScreenUppercasePreview() {
    // Theme { // Décommentez et utilisez votre thème si nécessaire
    WritingPracticeScreen(rememberNavController(), WritingType.UPPERCASE)
    // }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Practice Lowercase Light")
@Composable
fun WritingPracticeScreenLowercasePreview() {
    WritingPracticeScreen(rememberNavController(), WritingType.LOWERCASE)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Practice Numbers Light")
@Composable
fun WritingPracticeScreenNumbersPreview() {
    WritingPracticeScreen(rememberNavController(), WritingType.NUMBERS)
}