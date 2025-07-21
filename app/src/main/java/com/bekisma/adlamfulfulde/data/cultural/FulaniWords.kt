package com.bekisma.adlamfulfulde.data.cultural

data class FulaniWord(
    val word: String,
    val pronunciation: String,
    val meaning: String,
    val category: WordCategory,
    val difficulty: Int = 1, // 1-3 (facile à difficile)
    val culturalContext: String? = null
)

enum class WordCategory {
    FAMILY,
    GREETINGS,
    ANIMALS,
    COLORS,
    NUMBERS,
    NATURE,
    FOOD,
    BODY_PARTS,
    DAILY_LIFE,
    TOOLS,
    ACTIONS,
    EXPRESSIONS
}

object FulaniWords {
    val basicWords = listOf(
        // Salutations
        FulaniWord("𞤻𞤢𞤤𞤢", "salam", "Paix/Salut", WordCategory.GREETINGS, 1, "Salutation traditionnelle"),
        FulaniWord("𞤶𞤢𞤥", "jam", "Paix", WordCategory.GREETINGS, 1, "Réponse à la salutation"),
        FulaniWord("𞤫𞤲 𞤶𞤢𞤥", "en jam", "Tu vas bien", WordCategory.GREETINGS, 2),
        
        // Famille
        FulaniWord("𞤦𞤢𞤦𞤢", "baba", "Papa", WordCategory.FAMILY, 1),
        FulaniWord("𞤲𞤢𞤲𞤢", "nana", "Maman", WordCategory.FAMILY, 1),
        FulaniWord("𞤣𞤫𞤲", "den", "Enfant", WordCategory.FAMILY, 1),
        FulaniWord("𞤥𞤢𞤢𞤴𞤮", "maayo", "Frère/Sœur", WordCategory.FAMILY, 2),
        
        // Animaux
        FulaniWord("𞤲𞤢𞤺𞤮", "naago", "Vache", WordCategory.ANIMALS, 1, "Animal central dans la culture Fulani"),
        FulaniWord("𞤦𞤭𞤤𞤤𞤮", "billo", "Chèvre", WordCategory.ANIMALS, 1),
        FulaniWord("𞤧𞤫𞤣𞤣𞤮", "seddo", "Mouton", WordCategory.ANIMALS, 2),
        FulaniWord("𞤢𞤤𞤣𞤮", "aldo", "Oiseau", WordCategory.ANIMALS, 1),
        
        // Couleurs
        FulaniWord("𞤦𞤮𞤣𞤫𞤫𞤺𞤮", "bodeewo", "Rouge", WordCategory.COLORS, 2),
        FulaniWord("𞤩𞤫𞤤𞤤𞤫𞤦", "pelleb", "Blanc", WordCategory.COLORS, 2),
        FulaniWord("𞤦𞤢𞤤𞤫𞤦", "baleb", "Noir", WordCategory.COLORS, 2),
        
        // Nature
        FulaniWord("𞤤𞤫𞤳𞤳𞤮", "lekko", "Arbre", WordCategory.NATURE, 1),
        FulaniWord("𞤲𞤣𞤭𞤤", "ndil", "Eau", WordCategory.NATURE, 1),
        FulaniWord("𞤤𞤢𞤺𞤢𞤤", "lagal", "Soleil", WordCategory.NATURE, 2),
        FulaniWord("𞤤𞤫𞤱𞤳", "lewk", "Lune", WordCategory.NATURE, 2),
        
        // Corps humain
        FulaniWord("𞤸𞤮𞤤𞤮", "holo", "Tête", WordCategory.BODY_PARTS, 1),
        FulaniWord("𞤳𞤢𞤤𞤮", "kalo", "Main", WordCategory.BODY_PARTS, 1),
        FulaniWord("𞤲𞤣𞤫𞤴", "ndey", "Œil", WordCategory.BODY_PARTS, 1),
        FulaniWord("𞤦𞤭𞤤", "bil", "Bouche", WordCategory.BODY_PARTS, 1),
        
        // Nourriture
        FulaniWord("𞤲𞤢𞤢𞤲𞤳𞤮", "naanko", "Lait", WordCategory.FOOD, 2, "Aliment de base des Fulani"),
        FulaniWord("𞤸𞤢𞤢𞤳𞤮", "haako", "Viande", WordCategory.FOOD, 2),
        FulaniWord("𞤳𞤮𞤧𞤢𞤥", "kosam", "Mil", WordCategory.FOOD, 2),
        
        // Vie quotidienne
        FulaniWord("𞤨𞤵𞤪𞤭", "puri", "Maison", WordCategory.DAILY_LIFE, 1),
        FulaniWord("𞤲𞤢𞤤𞤤𞤫𞤯", "nalley", "Travail", WordCategory.DAILY_LIFE, 2),
        FulaniWord("𞤶𞤢𞤲𞤺𞤮", "jango", "Voyage", WordCategory.DAILY_LIFE, 2, "Tradition nomade"),
        FulaniWord("𞤣𞤢𞤲", "dan", "Jour", WordCategory.DAILY_LIFE, 1),
        FulaniWord("𞤳𞤢𞤪𞤭", "kari", "Nuit", WordCategory.DAILY_LIFE, 1)
    )
    
    val wordsByCategory = basicWords.groupBy { it.category }
    val wordsByDifficulty = basicWords.groupBy { it.difficulty }
    
    fun getRandomWordsByCategory(category: WordCategory, count: Int = 5): List<FulaniWord> {
        return wordsByCategory[category]?.shuffled()?.take(count) ?: emptyList()
    }
    
    fun getWordsByDifficulty(difficulty: Int): List<FulaniWord> {
        return wordsByDifficulty[difficulty] ?: emptyList()
    }
}