package com.bekisma.adlamfulfulde.data.alphabet

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestionnaire pour l'apprentissage progressif de l'alphabet Adlam
 * Chaque lettre doit être maîtrisée avant de passer à la suivante
 */
class AdlamAlphabetManager(private val context: Context) {
    
    private val prefs = context.getSharedPreferences("adlam_alphabet_progress", Context.MODE_PRIVATE)

    private val _progressState = MutableStateFlow(AlphabetProgressState())
    val progressState: StateFlow<AlphabetProgressState> = _progressState.asStateFlow()
    
    private val _currentQuizState = MutableStateFlow(QuizState())
    val currentQuizState: StateFlow<QuizState> = _currentQuizState.asStateFlow()
    
    private lateinit var learningOrder: List<AdlamLetter>

    init {
        // Ordre d'apprentissage recommandé (du plus simple au plus complexe)
        learningOrder = listOf(
            AdlamLetter.ALIF,      // 𞤀 [a] - Voyelle de base
            AdlamLetter.DAALI,     // 𞤁 [d] - Consonne simple
            AdlamLetter.LAAM,      // 𞤂 [l] - Forme distinctive
            AdlamLetter.MIIM,      // 𞤃 [m] - Facile à retenir
            AdlamLetter.BAA,       // 𞤄 [b] - Forme simple
            AdlamLetter.SINNYIIYHE, // 𞤅 [s] - Son clair
            AdlamLetter.PULAAR,    // 𞤆 [p] - Spécifique au Pulaar
            AdlamLetter.BHE,       // 𞤇 [bh] - Aspiration
            AdlamLetter.RAA,       // 𞤈 [r] - Roulé
            AdlamLetter.E,         // 𞤉 [e] - Voyelle
            AdlamLetter.FA,        // 𞤊 [f] - Fricative
            AdlamLetter.I,         // 𞤋 [i] - Voyelle haute
            AdlamLetter.O,         // 𞤌 [o] - Voyelle ronde
            AdlamLetter.U,         // 𞤍 [u] - Voyelle fermée
            AdlamLetter.YHE,       // 𞤎 [y] - Semi-voyelle
            AdlamLetter.WAW,       // 𞤏 [w] - Labio-vélaire
            AdlamLetter.NUN,       // 𞤐 [n] - Nasale
            AdlamLetter.KAF,       // 𞤑 [k] - Occlusive
            AdlamLetter.YAA,       // 𞤒 [ya] - Complexe
            AdlamLetter.HE,        // 𞤓 [h] - Aspirée
            AdlamLetter.WAW_LAABI, // 𞤔 [w] - Variante
            AdlamLetter.ARRE,      // 𞤕 [r] - Roulé fort
            AdlamLetter.CHE,       // 𞤖 [ch] - Affriquée
            AdlamLetter.JE,        // 𞤗 [j] - Sonore
            AdlamLetter.TEE,       // 𞤘 [t] - Occlusive sourde
            AdlamLetter.NYE,       // 𞤙 [ny] - Palatale
            AdlamLetter.GBE,       // 𞤚 [gb] - Labio-vélaire
            AdlamLetter.KPOKPO     // 𞤛 [kp] - Complexe
        )
    }
    
    private fun loadProgressState(): AlphabetProgressState {
        val currentLetterIndex = prefs.getInt("current_letter_index", 0)
        val currentPhase = LearningPhase.values()[prefs.getInt("current_phase", 0)]
        val masteredLettersSet = prefs.getStringSet("mastered_letters", emptySet())
            ?.mapNotNull { letterName ->
                AdlamLetter.values().find { it.name == letterName }
            }?.toSet() ?: emptySet()
        
        return AlphabetProgressState(
            currentLetterIndex = currentLetterIndex,
            currentPhase = currentPhase,
            masteredLetters = masteredLettersSet,
            totalLetters = learningOrder.size
        )
    }
    
    private fun saveProgressState() {
        val state = _progressState.value
        prefs.edit()
            .putInt("current_letter_index", state.currentLetterIndex)
            .putInt("current_phase", state.currentPhase.ordinal)
            .putStringSet("mastered_letters", state.masteredLetters.map { it.name }.toSet())
            .apply()
    }
    
    fun getCurrentLetter(): AdlamLetter? {
        val state = _progressState.value
        return if (state.currentLetterIndex < learningOrder.size) {
            learningOrder[state.currentLetterIndex]
        } else null
    }
    
    fun startVisualRecognitionQuiz(): List<VisualRecognitionQuestion> {
        val currentLetter = getCurrentLetter() ?: return emptyList()
        
        // Générer 10 questions pour la reconnaissance visuelle
        val questions = generateVisualRecognitionQuestions(currentLetter, 10)
        
        _currentQuizState.value = _currentQuizState.value.copy(
            visualQuestions = questions,
            currentPhase = LearningPhase.VISUAL_RECOGNITION
        )
        
        return questions
    }
    
    fun startAudioRecognitionQuiz(): List<AudioRecognitionQuestion> {
        val currentLetter = getCurrentLetter() ?: return emptyList()
        
        // Générer 10 questions pour la reconnaissance audio
        val questions = generateAudioRecognitionQuestions(currentLetter, 10)
        
        _currentQuizState.value = _currentQuizState.value.copy(
            audioQuestions = questions,
            currentPhase = LearningPhase.AUDIO_RECOGNITION
        )
        
        return questions
    }
    
    private fun generateVisualRecognitionQuestions(
        targetLetter: AdlamLetter,
        questionCount: Int
    ): List<VisualRecognitionQuestion> {
        val questions = mutableListOf<VisualRecognitionQuestion>()
        
        // Pool de lettres pour les distracteurs (lettres déjà apprises + quelques nouvelles)
        val availableLetters = learningOrder.take(_progressState.value.currentLetterIndex + 4)
        
        repeat(questionCount) {
            val distractors = availableLetters
                .filter { it != targetLetter }
                .shuffled()
                .take(3)
            
            val options = (distractors + targetLetter).shuffled()
            
            questions.add(
                VisualRecognitionQuestion(
                    questionNumber = it + 1,
                    targetLetter = targetLetter,
                    options = options,
                    correctAnswer = targetLetter
                )
            )
        }
        
        return questions
    }
    
    private fun generateAudioRecognitionQuestions(
        targetLetter: AdlamLetter,
        questionCount: Int
    ): List<AudioRecognitionQuestion> {
        val questions = mutableListOf<AudioRecognitionQuestion>()
        
        // Pool de lettres pour les distracteurs (lettres déjà apprises + quelques nouvelles)
        val availableLetters = learningOrder.take(_progressState.value.currentLetterIndex + 4)
        
        repeat(questionCount) {
            val distractors = availableLetters
                .filter { it != targetLetter }
                .shuffled()
                .take(3)
            
            val options = (distractors + targetLetter).shuffled()
            
            questions.add(
                AudioRecognitionQuestion(
                    questionNumber = it + 1,
                    targetLetter = targetLetter,
                    audioFileName = "adlam_${targetLetter.latinName.lowercase()}.mp3",
                    options = options,
                    correctAnswer = targetLetter
                )
            )
        }
        
        return questions
    }
    
    fun submitQuizAnswer(questionId: Int, selectedLetter: AdlamLetter): AnswerResult {
        val currentQuiz = _currentQuizState.value
        
        val correctAnswer = when (currentQuiz.currentPhase) {
            LearningPhase.VISUAL_RECOGNITION -> {
                currentQuiz.visualQuestions.find { it.questionNumber == questionId }?.correctAnswer
            }
            LearningPhase.AUDIO_RECOGNITION -> {
                currentQuiz.audioQuestions.find { it.questionNumber == questionId }?.correctAnswer
            }
            else -> null
        } ?: return AnswerResult.ERROR
        
        val isCorrect = selectedLetter == correctAnswer
        
        // Mettre à jour les statistiques
        val newStats = currentQuiz.copy(
            answeredQuestions = currentQuiz.answeredQuestions + 1,
            correctAnswers = if (isCorrect) currentQuiz.correctAnswers + 1 else currentQuiz.correctAnswers,
            incorrectAnswers = if (!isCorrect) currentQuiz.incorrectAnswers + 1 else currentQuiz.incorrectAnswers
        )
        
        _currentQuizState.value = newStats
        
        return if (isCorrect) AnswerResult.CORRECT else AnswerResult.INCORRECT
    }
    
    fun checkLetterMastery(): MasteryResult {
        val quizState = _currentQuizState.value
        val accuracy = if (quizState.answeredQuestions > 0) {
            (quizState.correctAnswers.toFloat() / quizState.answeredQuestions.toFloat()) * 100
        } else 0f
        
        val masteryThreshold = 80f // 80% de réussite requis
        val isMastered = accuracy >= masteryThreshold && quizState.answeredQuestions >= 8
        
        return MasteryResult(
            isMastered = isMastered,
            accuracy = accuracy,
            requiredAccuracy = masteryThreshold,
            questionsAnswered = quizState.answeredQuestions,
            minimumQuestions = 8
        )
    }
    
    fun advanceToNextPhase(): Boolean {
        val currentState = _progressState.value
        
        return when (currentState.currentPhase) {
            LearningPhase.VISUAL_RECOGNITION -> {
                // Passer à la phase audio pour la même lettre
                val newState = currentState.copy(
                    currentPhase = LearningPhase.AUDIO_RECOGNITION
                )
                _progressState.value = newState
                _currentQuizState.value = QuizState() // Reset quiz state
                saveProgressState()
                true
            }
            LearningPhase.AUDIO_RECOGNITION -> {
                // Passer à la lettre suivante et revenir à la phase visuelle
                advanceToNextLetter()
            }
            else -> false
        }
    }
    
    fun advanceToNextLetter(): Boolean {
        val currentState = _progressState.value
        val currentLetter = getCurrentLetter() ?: return false
        
        // Marquer la lettre comme maîtrisée
        val newMasteredLetters = currentState.masteredLetters + currentLetter
        val newLetterIndex = currentState.currentLetterIndex + 1
        
        val newState = currentState.copy(
            currentLetterIndex = newLetterIndex,
            masteredLetters = newMasteredLetters,
            currentPhase = LearningPhase.VISUAL_RECOGNITION // Recommencer en phase 1 pour la prochaine lettre
        )
        
        _progressState.value = newState
        _currentQuizState.value = QuizState() // Reset quiz state
        saveProgressState()
        
        return newLetterIndex < learningOrder.size
    }
    
    fun isAlphabetComplete(): Boolean {
        return _progressState.value.masteredLetters.size == learningOrder.size
    }
    
    fun resetCurrentQuiz() {
        _currentQuizState.value = QuizState()
    }
    
    fun getProgressPercentage(): Float {
        val state = _progressState.value
        return (state.masteredLetters.size.toFloat() / state.totalLetters.toFloat()) * 100
    }
}

// États et data classes
data class AlphabetProgressState(
    val currentLetterIndex: Int = 0,
    val currentPhase: LearningPhase = LearningPhase.VISUAL_RECOGNITION,
    val masteredLetters: Set<AdlamLetter> = emptySet(),
    val totalLetters: Int = 28
)

enum class LearningPhase {
    VISUAL_RECOGNITION,    // Phase 1: Reconnaître visuellement
    AUDIO_RECOGNITION,     // Phase 2: Reconnaître par l'audio (futur)
    PRODUCTION            // Phase 3: Écrire/tracer (futur)
}

data class QuizState(
    val visualQuestions: List<VisualRecognitionQuestion> = emptyList(),
    val audioQuestions: List<AudioRecognitionQuestion> = emptyList(),
    val answeredQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val currentPhase: LearningPhase = LearningPhase.VISUAL_RECOGNITION
)

data class VisualRecognitionQuestion(
    val questionNumber: Int,
    val targetLetter: AdlamLetter,
    val options: List<AdlamLetter>,
    val correctAnswer: AdlamLetter
)

data class AudioRecognitionQuestion(
    val questionNumber: Int,
    val targetLetter: AdlamLetter,
    val audioFileName: String,
    val options: List<AdlamLetter>,
    val correctAnswer: AdlamLetter
)

data class MasteryResult(
    val isMastered: Boolean,
    val accuracy: Float,
    val requiredAccuracy: Float,
    val questionsAnswered: Int,
    val minimumQuestions: Int
)

enum class AnswerResult {
    CORRECT,
    INCORRECT,
    ERROR
}