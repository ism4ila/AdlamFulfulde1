package com.bekisma.adlamfulfulde.data

data class VocabularyData(
    val id: String = "",
    val word: String = "",
    val definition: String = "",
    val example: String = "",
    val audioUrl: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val isLearned: Boolean = false
)