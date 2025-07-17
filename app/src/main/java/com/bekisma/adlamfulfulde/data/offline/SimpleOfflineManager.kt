package com.bekisma.adlamfulfulde.data.offline

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

data class SimpleOfflineContent(
    val id: String,
    val moduleId: String,
    val title: String,
    val content: String,
    val contentType: String,
    val size: Long,
    val lastUpdated: Long,
    val version: Int,
    val isDownloaded: Boolean = false,
    val filePath: String? = null
)

data class SimpleOfflineModule(
    val id: String,
    val name: String,
    val description: String,
    val contentItems: List<SimpleOfflineContent>,
    val totalSize: Long,
    val downloadedSize: Long,
    val isCompletelyDownloaded: Boolean
)

enum class SimpleDownloadStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    PAUSED
}

data class SimpleDownloadProgress(
    val moduleId: String,
    val status: SimpleDownloadStatus,
    val progress: Float,
    val downloadedItems: Int,
    val totalItems: Int,
    val error: String? = null
)

class SimpleOfflineManager(private val context: Context) {
    
    private val _offlineModules = MutableStateFlow<List<SimpleOfflineModule>>(emptyList())
    val offlineModules: StateFlow<List<SimpleOfflineModule>> = _offlineModules.asStateFlow()
    
    private val _downloadProgress = MutableStateFlow<Map<String, SimpleDownloadProgress>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, SimpleDownloadProgress>> = _downloadProgress.asStateFlow()
    
    private val _isOfflineModeEnabled = MutableStateFlow(false)
    val isOfflineModeEnabled: StateFlow<Boolean> = _isOfflineModeEnabled.asStateFlow()
    
    private val _storageUsage = MutableStateFlow(0L)
    val storageUsage: StateFlow<Long> = _storageUsage.asStateFlow()
    
    init {
        initializeOfflineContent()
    }
    
    private fun initializeOfflineContent() {
        // Create sample offline content
        val sampleModules = listOf(
            SimpleOfflineModule(
                id = "alphabet",
                name = "Adlam Alphabet",
                description = "Learn the complete Adlam alphabet",
                contentItems = createAlphabetContent(),
                totalSize = 2048000L, // 2MB
                downloadedSize = 0L,
                isCompletelyDownloaded = false
            ),
            SimpleOfflineModule(
                id = "numbers",
                name = "Adlam Numbers",
                description = "Master Adlam numerals",
                contentItems = createNumberContent(),
                totalSize = 1024000L, // 1MB
                downloadedSize = 0L,
                isCompletelyDownloaded = false
            ),
            SimpleOfflineModule(
                id = "vocabulary",
                name = "Basic Vocabulary",
                description = "Essential Fulfulde words in Adlam",
                contentItems = createVocabularyContent(),
                totalSize = 5120000L, // 5MB
                downloadedSize = 0L,
                isCompletelyDownloaded = false
            )
        )
        
        _offlineModules.value = sampleModules
    }
    
    private fun createAlphabetContent(): List<SimpleOfflineContent> {
        val letters = listOf("𞤢", "𞤣", "𞤤", "𞤥", "𞤦", "𞤧", "𞤨", "𞤩", "𞤪", "𞤫")
        return letters.mapIndexed { index, letter ->
            SimpleOfflineContent(
                id = "alphabet_$index",
                moduleId = "alphabet",
                title = "Letter $letter",
                content = "Learn to write and pronounce the Adlam letter $letter",
                contentType = "ALPHABET_LESSON",
                size = 204800L, // 200KB
                lastUpdated = System.currentTimeMillis(),
                version = 1
            )
        }
    }
    
    private fun createNumberContent(): List<SimpleOfflineContent> {
        val numbers = (0..9).map { "𞥐𞥑𞥒𞥓𞥔𞥕𞥖𞥗𞥘𞥙"[it].toString() }
        return numbers.mapIndexed { index, number ->
            SimpleOfflineContent(
                id = "number_$index",
                moduleId = "numbers",
                title = "Number $index",
                content = "Learn the Adlam numeral $number representing $index",
                contentType = "NUMBER_LESSON",
                size = 102400L, // 100KB
                lastUpdated = System.currentTimeMillis(),
                version = 1
            )
        }
    }
    
    private fun createVocabularyContent(): List<SimpleOfflineContent> {
        val vocabulary = mapOf(
            "𞤔𞤢𞤤𞤢𞤥" to "Hello",
            "𞤢𞤣𞤢" to "Father",
            "𞤯𞤢𞤤" to "Water",
            "𞤬𞤢𞤣" to "Good",
            "𞤫𞤯𞤮" to "House"
        )
        
        return vocabulary.entries.mapIndexed { index, (adlam, english) ->
            SimpleOfflineContent(
                id = "vocab_$index",
                moduleId = "vocabulary",
                title = "$adlam - $english",
                content = "Learn the word $adlam meaning $english",
                contentType = "VOCABULARY_ITEM",
                size = 512000L, // 500KB (includes audio)
                lastUpdated = System.currentTimeMillis(),
                version = 1
            )
        }
    }
    
    suspend fun downloadModule(moduleId: String) {
        val module = _offlineModules.value.find { it.id == moduleId } ?: return
        
        // Update progress
        val progress = SimpleDownloadProgress(
            moduleId = moduleId,
            status = SimpleDownloadStatus.IN_PROGRESS,
            progress = 0f,
            downloadedItems = 0,
            totalItems = module.contentItems.size
        )
        
        _downloadProgress.value = _downloadProgress.value + (moduleId to progress)
        
        // Simulate download
        for (i in 0 until module.contentItems.size) {
            kotlinx.coroutines.delay(500) // Simulate download time
            
            val currentProgress = SimpleDownloadProgress(
                moduleId = moduleId,
                status = SimpleDownloadStatus.IN_PROGRESS,
                progress = (i + 1f) / module.contentItems.size,
                downloadedItems = i + 1,
                totalItems = module.contentItems.size
            )
            
            _downloadProgress.value = _downloadProgress.value + (moduleId to currentProgress)
        }
        
        // Mark as completed
        val completedProgress = SimpleDownloadProgress(
            moduleId = moduleId,
            status = SimpleDownloadStatus.COMPLETED,
            progress = 1f,
            downloadedItems = module.contentItems.size,
            totalItems = module.contentItems.size
        )
        
        _downloadProgress.value = _downloadProgress.value + (moduleId to completedProgress)
        
        // Update module status
        val updatedModules = _offlineModules.value.map { m ->
            if (m.id == moduleId) {
                m.copy(
                    downloadedSize = m.totalSize,
                    isCompletelyDownloaded = true,
                    contentItems = m.contentItems.map { it.copy(isDownloaded = true) }
                )
            } else {
                m
            }
        }
        
        _offlineModules.value = updatedModules
    }
    
    fun deleteModule(moduleId: String) {
        val updatedModules = _offlineModules.value.map { module ->
            if (module.id == moduleId) {
                module.copy(
                    downloadedSize = 0L,
                    isCompletelyDownloaded = false,
                    contentItems = module.contentItems.map { it.copy(isDownloaded = false, filePath = null) }
                )
            } else {
                module
            }
        }
        
        _offlineModules.value = updatedModules
    }
    
    fun enableOfflineMode(enabled: Boolean) {
        _isOfflineModeEnabled.value = enabled
    }
    
    fun getOfflineContent(moduleId: String, contentId: String): SimpleOfflineContent? {
        return _offlineModules.value
            .find { it.id == moduleId }
            ?.contentItems
            ?.find { it.id == contentId && it.isDownloaded }
    }
    
    fun isContentAvailableOffline(moduleId: String, contentId: String): Boolean {
        return getOfflineContent(moduleId, contentId) != null
    }
    
    fun getDownloadedModules(): List<SimpleOfflineModule> {
        return _offlineModules.value.filter { it.isCompletelyDownloaded }
    }
    
    fun getStorageUsage(): Long {
        return _storageUsage.value
    }
    
    fun getAvailableStorage(): Long {
        return context.getExternalFilesDir(null)?.usableSpace ?: 0L
    }
}