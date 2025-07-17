package com.bekisma.adlamfulfulde.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bekisma.adlamfulfulde.data.VocabularyItem
import com.bekisma.adlamfulfulde.data.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VocabularyViewModel(private val repository: VocabularyRepository) : ViewModel() {

    private val _vocabularyList = MutableStateFlow<List<VocabularyItem>>(emptyList())
    val vocabularyList: StateFlow<List<VocabularyItem>> = _vocabularyList

    private val _favoriteWords = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteWords: StateFlow<Set<Int>> = _favoriteWords

    init {
        loadVocabulary()
        loadFavorites()
    }

    private fun loadVocabulary() {
        viewModelScope.launch {
            _vocabularyList.value = repository.getVocabularyList()
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _favoriteWords.value = repository.getFavoriteWords()
        }
    }

    fun toggleFavorite(wordId: Int) {
        viewModelScope.launch {
            val currentFavorites = _favoriteWords.value.toMutableSet()
            if (currentFavorites.contains(wordId)) {
                currentFavorites.remove(wordId)
            } else {
                currentFavorites.add(wordId)
            }
            repository.saveFavoriteWords(currentFavorites)
            _favoriteWords.value = currentFavorites
        }
    }

    fun getWordById(wordId: Int): VocabularyItem? {
        return _vocabularyList.value.find { it.id == wordId }
    }

    class Factory(private val repository: VocabularyRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VocabularyViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return VocabularyViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}