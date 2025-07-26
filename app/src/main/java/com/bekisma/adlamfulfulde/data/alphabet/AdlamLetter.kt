package com.bekisma.adlamfulfulde.data.alphabet

import androidx.annotation.StringRes
import com.bekisma.adlamfulfulde.R

/**
 * Énumération représentant les 28 lettres de l'alphabet Adlam
 * Chaque lettre contient ses informations complètes pour l'apprentissage
 */
enum class AdlamLetter(
    val unicode: String,           // Caractère Unicode Adlam (souvent la forme majuscule)
    val lowercaseUnicode: String,  // Forme minuscule (peut être identique à l'unicode si pas de distinction claire)
    val latinName: String,         // Nom en alphabet latin
    val phoneticSound: String,     // Son phonétique
    @StringRes val displayNameRes: Int, // Nom d'affichage localisé
    @StringRes val descriptionRes: Int, // Description pour l'apprentissage
    val soundFileName: String,     // Nom du fichier audio dans res/raw (ex: "adlam_alif.mp3")
    @StringRes val exampleWordRes: Int, // Ressource string pour l'exemple de mot
    @StringRes val exampleWordTranslationRes: Int, // Ressource string pour la traduction de l'exemple de mot
    val difficulty: LetterDifficulty = LetterDifficulty.EASY,
    val category: LetterCategory = LetterCategory.CONSONANT
) {
    // Ordre alphabétique Adlam correct
    ALIF(
        unicode = "𞤀",
        lowercaseUnicode = "𞤢",
        latinName = "Alif",
        phoneticSound = "[a]",
        displayNameRes = R.string.letter_alif,
        descriptionRes = R.string.letter_alif_desc,
        soundFileName = "adlam1_1",
        exampleWordRes = R.string.example_word_alif,
        exampleWordTranslationRes = R.string.example_word_translation_alif,
        difficulty = LetterDifficulty.EASY,
        category = LetterCategory.VOWEL
    ),

    DAALI(
        unicode = "𞤁",
        lowercaseUnicode = "𞤡",
        latinName = "Daali",
        phoneticSound = "[d]",
        displayNameRes = R.string.letter_daali,
        descriptionRes = R.string.letter_daali_desc,
        soundFileName = "adlam2_1",
        exampleWordRes = R.string.example_word_daali,
        exampleWordTranslationRes = R.string.example_word_translation_daali,
        difficulty = LetterDifficulty.EASY,
        category = LetterCategory.CONSONANT
    ),

    LAAM(
        unicode = "𞤂",
        lowercaseUnicode = "𞤤",
        latinName = "Laam",
        phoneticSound = "[l]",
        displayNameRes = R.string.letter_laam,
        descriptionRes = R.string.letter_laam_desc,
        soundFileName = "adlam3_1",
        exampleWordRes = R.string.example_word_laam,
        exampleWordTranslationRes = R.string.example_word_translation_laam,
        difficulty = LetterDifficulty.EASY,
        category = LetterCategory.CONSONANT
    ),

    MIIM(
        unicode = "𞤃",
        lowercaseUnicode = "𞤣",
        latinName = "Miim",
        phoneticSound = "[m]",
        displayNameRes = R.string.letter_miim,
        descriptionRes = R.string.letter_miim_desc,
        soundFileName = "adlam4_1",
        exampleWordRes = R.string.example_word_miim,
        exampleWordTranslationRes = R.string.example_word_translation_miim,
        difficulty = LetterDifficulty.EASY,
        category = LetterCategory.CONSONANT
    ),

    BAA(
        unicode = "𞤄",
        lowercaseUnicode = "𞤥",
        latinName = "Baa",
        phoneticSound = "[b]",
        displayNameRes = R.string.letter_baa,
        descriptionRes = R.string.letter_baa_desc,
        soundFileName = "adlam5_1",
        exampleWordRes = R.string.example_word_baa,
        exampleWordTranslationRes = R.string.example_word_translation_baa,
        difficulty = LetterDifficulty.EASY,
        category = LetterCategory.CONSONANT
    ),

    SINNYIIYHE(
        unicode = "𞤅",
        lowercaseUnicode = "𞤦",
        latinName = "Sinnyiiyhe",
        phoneticSound = "[s]",
        displayNameRes = R.string.letter_sinnyiiyhe,
        descriptionRes = R.string.letter_sinnyiiyhe_desc,
        soundFileName = "adlam6_1",
        exampleWordRes = R.string.example_word_sinnyiiyhe,
        exampleWordTranslationRes = R.string.example_word_translation_sinnyiiyhe,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    PULAAR(
        unicode = "𞤆",
        lowercaseUnicode = "𞤧",
        latinName = "Pulaar",
        phoneticSound = "[p]",
        displayNameRes = R.string.letter_pulaar,
        descriptionRes = R.string.letter_pulaar_desc,
        soundFileName = "adlam7_1",
        exampleWordRes = R.string.example_word_pulaar,
        exampleWordTranslationRes = R.string.example_word_translation_pulaar,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    BHE(
        unicode = "𞤇",
        lowercaseUnicode = "𞤨",
        latinName = "Bhe",
        phoneticSound = "[ɓ]",
        displayNameRes = R.string.letter_bhe,
        descriptionRes = R.string.letter_bhe_desc,
        soundFileName = "adlam8_1",
        exampleWordRes = R.string.example_word_bhe,
        exampleWordTranslationRes = R.string.example_word_translation_bhe,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    RAA(
        unicode = "𞤈",
        lowercaseUnicode = "𞤩",
        latinName = "Raa",
        phoneticSound = "[r]",
        displayNameRes = R.string.letter_raa,
        descriptionRes = R.string.letter_raa_desc,
        soundFileName = "adlam9_1",
        exampleWordRes = R.string.example_word_raa,
        exampleWordTranslationRes = R.string.example_word_translation_raa,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    E(
        unicode = "𞤉",
        lowercaseUnicode = "𞤫",
        latinName = "E",
        phoneticSound = "[e]",
        displayNameRes = R.string.letter_e,
        descriptionRes = R.string.letter_e_desc,
        soundFileName = "adlam10_1",
        exampleWordRes = R.string.example_word_e,
        exampleWordTranslationRes = R.string.example_word_translation_e,
        difficulty = LetterDifficulty.EASY,
        category = LetterCategory.VOWEL
    ),

    FA(
        unicode = "𞤊",
        lowercaseUnicode = "𞤪",
        latinName = "Fa",
        phoneticSound = "[f]",
        displayNameRes = R.string.letter_fa,
        descriptionRes = R.string.letter_fa_desc,
        soundFileName = "adlam11_1",
        exampleWordRes = R.string.example_word_fa,
        exampleWordTranslationRes = R.string.example_word_translation_fa,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    I(
        unicode = "𞤋",
        lowercaseUnicode = "𞤭",
        latinName = "I",
        phoneticSound = "[i]",
        displayNameRes = R.string.letter_i,
        descriptionRes = R.string.letter_i_desc,
        soundFileName = "adlam12_1",
        exampleWordRes = R.string.example_word_i,
        exampleWordTranslationRes = R.string.example_word_translation_i,
        difficulty = LetterDifficulty.EASY,
        category = LetterCategory.VOWEL
    ),

    O(
        unicode = "𞤌",
        lowercaseUnicode = "𞤮",
        latinName = "O",
        phoneticSound = "[o]",
        displayNameRes = R.string.letter_o,
        descriptionRes = R.string.letter_o_desc,
        soundFileName = "adlam13_1",
        exampleWordRes = R.string.example_word_o,
        exampleWordTranslationRes = R.string.example_word_translation_o,
        difficulty = LetterDifficulty.EASY,
        category = LetterCategory.VOWEL
    ),

    U_IMPLOSIVE(
        unicode = "𞤍",
        lowercaseUnicode = "𞤯",
        latinName = "Daal",
        phoneticSound = "[ɗ]",
        displayNameRes = R.string.letter_u,
        descriptionRes = R.string.letter_u_desc,
        soundFileName = "adlam14_1",
        exampleWordRes = R.string.example_word_u,
        exampleWordTranslationRes = R.string.example_word_translation_u,
        difficulty = LetterDifficulty.HARD,
        category = LetterCategory.CONSONANT
    ),

    YHE(
        unicode = "𞤎",
        lowercaseUnicode = "𞤰",
        latinName = "Yhe",
        phoneticSound = "[ƴ]",
        displayNameRes = R.string.letter_yhe,
        descriptionRes = R.string.letter_yhe_desc,
        soundFileName = "adlam15_1",
        exampleWordRes = R.string.example_word_yhe,
        exampleWordTranslationRes = R.string.example_word_translation_yhe,
        difficulty = LetterDifficulty.HARD,
        category = LetterCategory.CONSONANT
    ),

    WAW(
        unicode = "𞤏",
        lowercaseUnicode = "𞤱",
        latinName = "Waw",
        phoneticSound = "[w]",
        displayNameRes = R.string.letter_waw,
        descriptionRes = R.string.letter_waw_desc,
        soundFileName = "adlam16_1",
        exampleWordRes = R.string.example_word_waw,
        exampleWordTranslationRes = R.string.example_word_translation_waw,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    NUN(
        unicode = "𞤐",
        lowercaseUnicode = "𞤲",
        latinName = "Nun",
        phoneticSound = "[n]",
        displayNameRes = R.string.letter_nun,
        descriptionRes = R.string.letter_nun_desc,
        soundFileName = "adlam17_1",
        exampleWordRes = R.string.example_word_nun,
        exampleWordTranslationRes = R.string.example_word_translation_nun,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    KAF(
        unicode = "𞤑",
        lowercaseUnicode = "𞤳",
        latinName = "Kaf",
        phoneticSound = "[k]",
        displayNameRes = R.string.letter_kaf,
        descriptionRes = R.string.letter_kaf_desc,
        soundFileName = "adlam18_1",
        exampleWordRes = R.string.example_word_kaf,
        exampleWordTranslationRes = R.string.example_word_translation_kaf,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    YAA(
        unicode = "𞤒",
        lowercaseUnicode = "𞤴",
        latinName = "Yaa",
        phoneticSound = "[y]",
        displayNameRes = R.string.letter_yaa,
        descriptionRes = R.string.letter_yaa_desc,
        soundFileName = "adlam19_1",
        exampleWordRes = R.string.example_word_yaa,
        exampleWordTranslationRes = R.string.example_word_translation_yaa,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    U(
        unicode = "𞤓",
        lowercaseUnicode = "𞤵",
        latinName = "U",
        phoneticSound = "[u]",
        displayNameRes = R.string.letter_he,
        descriptionRes = R.string.letter_he_desc,
        soundFileName = "adlam20_1",
        exampleWordRes = R.string.example_word_he,
        exampleWordTranslationRes = R.string.example_word_translation_he,
        difficulty = LetterDifficulty.EASY,
        category = LetterCategory.VOWEL
    ),

    JE(
        unicode = "𞤔",
        lowercaseUnicode = "𞤶",
        latinName = "Je",
        phoneticSound = "[j]",
        displayNameRes = R.string.letter_waw_laabi,
        descriptionRes = R.string.letter_waw_laabi_desc,
        soundFileName = "adlam21_1",
        exampleWordRes = R.string.example_word_waw_laabi,
        exampleWordTranslationRes = R.string.example_word_translation_waw_laabi,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    CHE(
        unicode = "𞤕",
        lowercaseUnicode = "𞤷",
        latinName = "Che",
        phoneticSound = "[c]",
        displayNameRes = R.string.letter_arre,
        descriptionRes = R.string.letter_arre_desc,
        soundFileName = "adlam22_1",
        exampleWordRes = R.string.example_word_arre,
        exampleWordTranslationRes = R.string.example_word_translation_arre,
        difficulty = LetterDifficulty.HARD,
        category = LetterCategory.CONSONANT
    ),

    HE(
        unicode = "𞤖",
        lowercaseUnicode = "𞤸",
        latinName = "He",
        phoneticSound = "[h]",
        displayNameRes = R.string.letter_che,
        descriptionRes = R.string.letter_che_desc,
        soundFileName = "adlam23_1",
        exampleWordRes = R.string.example_word_che,
        exampleWordTranslationRes = R.string.example_word_translation_che,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    GBE(
        unicode = "𞤗",
        lowercaseUnicode = "𞤹",
        latinName = "Gbe",
        phoneticSound = "[ɠ]",
        displayNameRes = R.string.letter_je,
        descriptionRes = R.string.letter_je_desc,
        soundFileName = "adlam24_1",
        exampleWordRes = R.string.example_word_je,
        exampleWordTranslationRes = R.string.example_word_translation_je,
        difficulty = LetterDifficulty.HARD,
        category = LetterCategory.CONSONANT
    ),

    GAA(
        unicode = "𞤘",
        lowercaseUnicode = "𞤺",
        latinName = "Gaa",
        phoneticSound = "[g]",
        displayNameRes = R.string.letter_tee,
        descriptionRes = R.string.letter_tee_desc,
        soundFileName = "adlam25_1",
        exampleWordRes = R.string.example_word_tee,
        exampleWordTranslationRes = R.string.example_word_translation_tee,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    NYE(
        unicode = "𞤙",
        lowercaseUnicode = "𞤻",
        latinName = "Nye",
        phoneticSound = "[ñ]",
        displayNameRes = R.string.letter_nye,
        descriptionRes = R.string.letter_nye_desc,
        soundFileName = "adlam26_1",
        exampleWordRes = R.string.example_word_nye,
        exampleWordTranslationRes = R.string.example_word_translation_nye,
        difficulty = LetterDifficulty.HARD,
        category = LetterCategory.CONSONANT
    ),

    TEE(
        unicode = "𞤚",
        lowercaseUnicode = "𞤼",
        latinName = "Tee",
        phoneticSound = "[t]",
        displayNameRes = R.string.letter_gbe,
        descriptionRes = R.string.letter_gbe_desc,
        soundFileName = "adlam27_1",
        exampleWordRes = R.string.example_word_gbe,
        exampleWordTranslationRes = R.string.example_word_translation_gbe,
        difficulty = LetterDifficulty.MEDIUM,
        category = LetterCategory.CONSONANT
    ),

    NGA(
        unicode = "𞤛",
        lowercaseUnicode = "𞤽",
        latinName = "Nga",
        phoneticSound = "[ŋ]",
        displayNameRes = R.string.letter_kpokpo,
        descriptionRes = R.string.letter_kpokpo_desc,
        soundFileName = "adlam28_1",
        exampleWordRes = R.string.example_word_kpokpo,
        exampleWordTranslationRes = R.string.example_word_translation_kpokpo,
        difficulty = LetterDifficulty.HARD,
        category = LetterCategory.CONSONANT
    ),

    // Consonnes nasales combinées
    ND_COMBINATION(
        unicode = "𞤐𞤁",
        lowercaseUnicode = "𞤲𞤡",
        latinName = "Nd",
        phoneticSound = "[nd]",
        displayNameRes = R.string.letter_nd_combination,
        descriptionRes = R.string.letter_nd_combination_desc,
        soundFileName = "adlam_nd_1",
        exampleWordRes = R.string.example_word_nd_combination,
        exampleWordTranslationRes = R.string.example_word_translation_nd_combination,
        difficulty = LetterDifficulty.VERY_HARD,
        category = LetterCategory.COMBINED
    ),

    MB_COMBINATION(
        unicode = "𞤐𞤄",
        lowercaseUnicode = "𞤲𞤥",
        latinName = "Mb",
        phoneticSound = "[mb]",
        displayNameRes = R.string.letter_mb_combination,
        descriptionRes = R.string.letter_mb_combination_desc,
        soundFileName = "adlam_mb_1",
        exampleWordRes = R.string.example_word_mb_combination,
        exampleWordTranslationRes = R.string.example_word_translation_mb_combination,
        difficulty = LetterDifficulty.VERY_HARD,
        category = LetterCategory.COMBINED
    ),

    NJ_COMBINATION(
        unicode = "𞤐𞤔",
        lowercaseUnicode = "𞤲𞤶",
        latinName = "Nj",
        phoneticSound = "[nj]",
        displayNameRes = R.string.letter_nj_combination,
        descriptionRes = R.string.letter_nj_combination_desc,
        soundFileName = "adlam_nj_1",
        exampleWordRes = R.string.example_word_nj_combination,
        exampleWordTranslationRes = R.string.example_word_translation_nj_combination,
        difficulty = LetterDifficulty.VERY_HARD,
        category = LetterCategory.COMBINED
    ),

    NG_COMBINATION(
        unicode = "𞤐𞤘",
        lowercaseUnicode = "𞤲𞤺",
        latinName = "Ŋg",
        phoneticSound = "[ŋg]",
        displayNameRes = R.string.letter_ng_combination,
        descriptionRes = R.string.letter_ng_combination_desc,
        soundFileName = "adlam_ng_1",
        exampleWordRes = R.string.example_word_ng_combination,
        exampleWordTranslationRes = R.string.example_word_translation_ng_combination,
        difficulty = LetterDifficulty.VERY_HARD,
        category = LetterCategory.COMBINED
    );
    
    companion object {
        fun getByUnicode(unicode: String): AdlamLetter? {
            return values().find { it.unicode == unicode }
        }
        
        fun getVowels(): List<AdlamLetter> {
            return values().filter { it.category == LetterCategory.VOWEL }
        }
        
        fun getConsonants(): List<AdlamLetter> {
            return values().filter { it.category == LetterCategory.CONSONANT }
        }
        
        fun getByDifficulty(difficulty: LetterDifficulty): List<AdlamLetter> {
            return values().filter { it.difficulty == difficulty }
        }
    }
}

enum class LetterDifficulty {
    EASY,       // Lettres simples, distinctives
    MEDIUM,     // Lettres avec quelques similarités
    HARD,       // Lettres complexes ou similaires
    VERY_HARD   // Lettres très complexes ou rares
}

enum class LetterCategory {
    VOWEL,      // Voyelles
    CONSONANT,  // Consonnes
    SEMI_VOWEL,  // Semi-voyelles
    COMBINED    // Consonnes nasales combinées
}