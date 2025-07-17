package com.bekisma.adlamfulfulde.data

class VocabularyRepository {
    fun getVocabularyList(): List<VocabularyItem> {
        return emptyList()
    }

    fun getFavoriteWords(): Set<Int> {
        return emptySet()
    }

    fun saveFavoriteWords(favoriteWords: Set<Int>) {
        // TODO: Implement saving favorite words to persistent storage
    }
}

