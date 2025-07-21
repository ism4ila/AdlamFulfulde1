package com.bekisma.adlamfulfulde.utils

import androidx.compose.ui.graphics.Color
import com.bekisma.adlamfulfulde.data.alphabet.LetterDifficulty

fun getDifficultyColor(difficulty: LetterDifficulty): Color {
    return when (difficulty) {
        LetterDifficulty.EASY -> Color(0xFF4CAF50)
        LetterDifficulty.MEDIUM -> Color(0xFFFF9800)
        LetterDifficulty.HARD -> Color(0xFFFF5722)
        LetterDifficulty.VERY_HARD -> Color(0xFFD32F2F)
    }
}