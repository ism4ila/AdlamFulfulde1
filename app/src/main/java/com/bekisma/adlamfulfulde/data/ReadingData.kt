package com.bekisma.adlamfulfulde.data

import com.bekisma.adlamfulfulde.R // Importez votre R

// NOTE IMPORTANTE :
// 1. ÉCRIVEZ vos textes en Adlam.
// 2. ENREGISTREZ l'audio pour chaque texte et placez les fichiers dans `res/raw/`.
// 3. REMPLACEZ `R.raw.son_nul` par les VRAIS identifiants de vos fichiers audio.
// 4. (Avancé) Si vous voulez le surlignage, vous devrez manuellement créer les `WordTiming`.

fun getReadingPassages(): List<ReadingPassage> {
    return listOf(
        // ========== NIVEAU FACILE (20+ passages) ==========
        ReadingPassage(
            id = 1,
            title = "𞤃𞤭 𞤲𞤢 𞤶𞤭𞤣𞤢", // "Mon père"
            adlamText = "𞤃𞤭 𞤲𞤢 𞤶𞤭𞤣𞤢 𞤲𞤺𞤮𞤲𞤢. 𞤌 𞤴𞤢𞤸𞤭 𞤲𞥋𞤣𞤫𞤪 𞤧𞤵𞤳. 𞤌 𞤶𞤮𞤤𞤵 𞤴𞤢𞤸𞤢 𞤦𞤮𞤯𞤭𞤢.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Mon père s'appelle Ngona. Il va au marché. Il achète de la viande.",
            wordTimings = null,
            tags = listOf("famille", "base"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 2,
            title = "𞤐𞤫𞤯𞤮 𞤢𞤥", // "Ma mère"
            adlamText = "𞤐𞤫𞤯𞤮 𞤢𞤥 𞤲𞤺𞤢 𞤃𞤢𞤪𞤭𞤢𞤥. 𞤃𞤭 𞤶𞤵𞤤𞤢 𞤲𞤣𞤵. 𞤃𞤭 𞤬𞤮𞤼𞤢 𞤲𞥋𞤺𞤫.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Ma mère s'appelle Mariam. Elle cuisine le riz. Elle lave les vêtements.",
            wordTimings = null,
            tags = listOf("famille", "base"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 3,
            title = "𞤸𞤮𞤪𞤫 𞤢𞤥", // "Mon frère"
            adlamText = "𞤸𞤮𞤪𞤫 𞤢𞤥 𞤤𞤢𞤸𞤵 𞤪𞤢𞤴. 𞤌 𞤶𞤢𞤪 𞤲𞥋𞤣𞤫𞤪 𞤧𞤳𞤮𞤯. 𞤌 𞤶𞤮𞤤𞤵 𞤷𞤮𞤥.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Mon frère s'appelle Ray. Il étudie à l'école. Il lit des livres.",
            wordTimings = null,
            tags = listOf("famille", "éducation"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 4,
            title = "𞤦𞤢𞥄𞤪𞤭 𞤢𞤥", // "Ma sœur"
            adlamText = "𞤦𞤢𞥄𞤪𞤭 𞤢𞤥 𞤢𞤣𞤢 𞤢𞤺𞤮. 𞤃𞤭 𞤴𞤢𞤸 𞤦𞤫𞤪. 𞤃𞤭 𞤤𞤢𞤸𞤵 𞤋𞤭𞤲𞤢.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Ma sœur s'appelle Ada Ago. Elle va dehors. Elle s'appelle Dina.",
            wordTimings = null,
            tags = listOf("famille", "base"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 5,
            title = "𞤺𞤢𞤤𞤫 𞤢𞤥", // "Ma maison"
            adlamText = "𞤺𞤢𞤤𞤫 𞤢𞤥 𞤦𞤫𞤱𞤭. 𞤌 𞤢𞤤𞤵 𞤸𞤵𞤯𞤫 𞤲𞤢𞤴. 𞤌 𞤲𞤫𞤱𞤢 𞤸𞤫𞤬.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Ma maison est grande. Elle a quatre pièces. Elle est jolie.",
            wordTimings = null,
            tags = listOf("maison", "base"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 6,
            title = "𞤲𞤢𞥄𞤺𞤫 𞤢𞤥", // "Ma vache"
            adlamText = "𞤲𞤢𞥄𞤺𞤫 𞤢𞤥 𞤪𞤢𞤻𞤢. 𞤃𞤭 𞤳𞤮𞤧𞤢 𞤲𞤢𞥄𞤥. 𞤃𞤭 𞤤𞤢𞤧𞤢 𞤤𞤢𞤦𞤢𞥄𞤤.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Ma vache est rouge. Elle donne du lait. Elle mange de l'herbe.",
            wordTimings = null,
            tags = listOf("animaux", "base"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 7,
            title = "𞤫𞤴𞤢𞤥 𞤢𞤥", // "Mon chien"
            adlamText = "𞤫𞤴𞤢𞤥 𞤢𞤥 𞤲𞤺𞤮𞤲𞤢. 𞤌 𞤳𞤢𞤻𞤢 𞤺𞤢𞤤𞤫 𞤢𞤥. 𞤌 𞤬𞤭𞤯𞤢 𞤧𞤢𞤳.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Mon chien s'appelle Ngona. Il garde ma maison. Il est méchant.",
            wordTimings = null,
            tags = listOf("animaux", "base"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 8,
            title = "𞤲𞤣𞤵", // "Le riz"
            adlamText = "𞤲𞤣𞤵 𞤲𞤢 𞤴𞤢𞤸𞤭 𞤲𞥋𞤣𞤫𞤪 𞤧𞤳𞤮𞤯. 𞤌 𞤳𞤢𞤦𞤭 𞤳𞤮𞤧𞤲𞤢. 𞤌 𞤩𞤮𞤻𞤢 𞤬𞤫𞤱.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Le riz pousse dans les champs. Il devient blanc. Il est délicieux.",
            wordTimings = null,
            tags = listOf("nourriture", "agriculture"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 9,
            title = "𞤲𞤣𞤫 𞤤𞤮", // "L'eau"
            adlamText = "𞤲𞤣𞤫 𞤤𞤮 𞤯𞤮𞤮 𞤦𞤢𞤯. 𞤌 𞤲𞤢𞤼𞤢 𞤲𞥋𞤣𞤫𞤪 𞤦𞤫𞤤𞥄𞤯𞤭. 𞤌 𞤱𞤢𞤳𞤭 𞤬𞤮𞤳.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "L'eau est très froide. Elle coule dans la rivière. Elle est propre.",
            wordTimings = null,
            tags = listOf("nature", "base"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 10,
            title = "𞤤𞤮𞤦𞤮", // "Le feu"
            adlamText = "𞤤𞤮𞤦𞤮 𞤱𞤢𞤳𞤵 𞤧𞤳𞤮𞤯. 𞤌 𞤻𞤮𞤥𞤱 𞤬𞤭𞤯𞤫. 𞤌 𞤧𞤢𞤳 𞤯𞤮𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Le feu brûle l'école. Il fait de la fumée. Il est très chaud.",
            wordTimings = null,
            tags = listOf("nature", "danger"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 11,
            title = "𞤢𞤤𞤢", // "Dieu"
            adlamText = "𞤢𞤤𞤤𞤢 𞤶𞤮𞤯𞤭 𞤦𞤫𞤣. 𞤌 𞤢𞤤𞤢 𞤳𞤢𞤦 𞤲𞥋𞤣𞤫𞤪 𞤸𞤢𞤧. 𞤌 𞤲𞤫𞤱𞤢 𞤣𞤮𞤲.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Allah habite au ciel. Il est grand et unique. Il est bon.",
            wordTimings = null,
            tags = listOf("religion", "base"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 12,
            title = "𞤞𞤮𞤲", // "Le soleil"
            adlamText = "𞤞𞤮𞤲 𞤸𞤢𞤫 𞤲𞥋𞤣𞤫𞤪 𞤸𞤢𞤯. 𞤌 𞤬𞤢𞤴 𞤶𞤮𞤻𞤢. 𞤌 𞤯𞤮𞤮 𞤧𞤢𞤤.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Le soleil se lève au ciel. Il donne de la lumière. Il est très brillant.",
            wordTimings = null,
            tags = listOf("nature", "temps"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 13,
            title = "𞤤𞤫𞥅𞤪𞤵", // "La lune"
            adlamText = "𞤤𞤫𞥅𞤪𞤵 𞤸𞤢𞤫 𞤲𞤣𞤫 𞤸𞤢𞤯. 𞤌 𞤬𞤢𞤴 𞤦𞤫𞤪𞤢𞤤. 𞤌 𞤬𞤮𞤷 𞤱𞤢𞤩𞤭.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "La lune se lève dans le ciel. Elle donne de la lumière la nuit. Elle est ronde.",
            wordTimings = null,
            tags = listOf("nature", "temps"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 14,
            title = "𞤶𞤵𞤤𞤣𞤢", // "L'étoile"
            adlamText = "𞤶𞤵𞤤𞤣𞤢 𞤶𞤮𞤶 𞤸𞤢𞤯. 𞤌 𞤬𞤢𞤴 𞤶𞤮𞤻𞤢. 𞤌 𞤲𞤫𞤱𞤢 𞤬𞤭𞤯𞤫.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "L'étoile brille dans le ciel. Elle donne de la lumière. Elle est belle.",
            wordTimings = null,
            tags = listOf("nature", "temps"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 15,
            title = "𞤸𞤢𞤳𞤮", // "Le vent"
            adlamText = "𞤸𞤢𞤳𞤮 𞤸𞤢𞤫 𞤬𞤮𞤻. 𞤌 𞤸𞤮𞤶 𞤤𞤫𞤦𞤣𞤫. 𞤌 𞤦𞤢𞤯 𞤫 𞤳𞤮𞤧.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Le vent souffle fort. Il secoue les arbres. Il est froid en saison sèche.",
            wordTimings = null,
            tags = listOf("nature", "météo"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 16,
            title = "𞤲𞤣𞤫𞤥", // "La pluie"
            adlamText = "𞤲𞤣𞤫𞤥 𞤸𞤢𞤫 𞤲𞥋𞤣𞤫𞤪 𞤻𞤵𞤲𞤺𞤮. 𞤌 𞤲𞤮𞤳𞤵 𞤤𞤫𞤣𞥆𞤫. 𞤌 𞤼𞤢𞤴 𞤹𞤢𞤦𞤫.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "La pluie tombe pendant l'hivernage. Elle arrose les cultures. Elle apporte la joie.",
            wordTimings = null,
            tags = listOf("nature", "météo"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 17,
            title = "𞤢𞤣𞤵𞤲𞤢", // "Le monde"
            adlamText = "𞤢𞤣𞤵𞤲𞤢 𞤦𞤫𞤱𞤭 𞤬𞤮𞤻. 𞤌 𞤸𞤢𞥅 𞤦𞤫𞤣 𞤳𞤮𞥅. 𞤌 𞤲𞤫𞤱𞤢 𞤯𞤮𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Le monde est très grand. Il y a beaucoup de choses. Il est très beau.",
            wordTimings = null,
            tags = listOf("géographie", "base"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 18,
            title = "𞤲𞥋𞤺𞤢𞤴", // "Les gens"
            adlamText = "𞤲𞥋𞤺𞤢𞤴 𞤶𞤮𞤣 𞤲𞥋𞤣𞤫𞤪 𞤱𞤮𞤪𞤮. 𞤘𞤫 𞤸𞤢𞤤 𞤦𞤫𞤣 𞤳𞤮𞥅. 𞤘𞤫 𞤷𞤮𞤺 𞤣𞤮𞤲.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Les gens habitent dans des villages. Ils font beaucoup de choses. Ils cherchent le bien.",
            wordTimings = null,
            tags = listOf("société", "base"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 19,
            title = "𞤦𞤢𞤦𞤢", // "Grand-père"
            adlamText = "𞤦𞤢𞤦𞤢 𞤢𞤥 𞤸𞤢𞤪𞤢 𞤳𞤮𞤦. 𞤌 𞤸𞤢𞤤 𞤸𞤢𞤣𞤭𞤲𞤺𞤮. 𞤌 𞤦𞤢𞤯 𞤩𞤢𞥄𞤯𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Mon grand-père a des cheveux blancs. Il raconte des histoires. Il est très sage.",
            wordTimings = null,
            tags = listOf("famille", "sagesse"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 20,
            title = "𞤦𞤢𞤲𞥋𞤺𞤢", // "Grand-mère"
            adlamText = "𞤦𞤢𞤲𞥋𞤺𞤢 𞤢𞤥 𞤶𞤮𞤤𞤢 𞤯𞤮𞤮. 𞤃𞤭 𞤦𞤢𞤯 𞤯𞤵𞤩𞤢𞤤. 𞤃𞤭 𞤣𞤢𞤲 𞤸𞤢𞤣𞤭𞤲𞤺𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "Ma grand-mère est très âgée. Elle est très sage. Elle connaît des histoires.",
            wordTimings = null,
            tags = listOf("famille", "sagesse"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 21,
            title = "𞤸𞤭𞤲𞤢", // "L'enfant"
            adlamText = "𞤸𞤭𞤲𞤢 𞤯𞤮𞤮 𞤦𞤢𞤯. 𞤌 𞤪𞤫𞤩 𞤫 𞤯𞤮𞤮. 𞤌 𞤶𞤢𞤪 𞤶𞤢𞤣𞤢𞤲𞤺𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "L'enfant est très petit. Il pleure beaucoup. Il apprend à marcher.",
            wordTimings = null,
            tags = listOf("famille", "développement"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 22,
            title = "𞤧𞤳𞤮𞤯", // "L'école"
            adlamText = "𞤧𞤳𞤮𞤯 𞤦𞤫𞤱𞤭 𞤬𞤮𞤻. 𞤌 𞤢𞤤𞤵 𞤸𞤭𞤲𞤢𞤦𞤫 𞤦𞤫𞤣. 𞤘𞤫 𞤲𞤢𞤲 𞤶𞤢𞤪𞤢𞤲𞤺𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Facile",
            frenchTranslation = "L'école est très grande. Il y a beaucoup d'enfants. Ils apprennent à lire.",
            wordTimings = null,
            tags = listOf("éducation", "apprentissage"),
            isFavorite = false
        ),
        
        // ========== NIVEAU MOYEN (20+ passages) ==========
        ReadingPassage(
            id = 23,
            title = "𞤂𞤫𞤴𞤯𞤮𞤤 𞤫 𞤲𞤢𞥄𞤺𞤫", // "Le berger et la vache"
            adlamText = "𞤂𞤫𞤴𞤯𞤮𞤤 𞤴𞤢𞤸𞤭𞥅 𞤲𞥋𞤣𞤫𞤪 𞤤𞤢𞤣𞥆𞤫. 𞤌 𞤴𞤢𞤤𞤼𞤭𞤲𞤭𞥅 𞤲𞤢𞥄𞤺𞤫 𞤥𞤢𞤳𞥆𞤮. 𞤐𞤢𞥄𞤺𞤫 𞤲𞤺𞤮𞤲 𞤤𞤢𞤧𞤵 𞤯𞤮𞤲 𞤤𞤢𞤦𞤢𞥄𞤤 𞤲𞤣𞤫 𞤸𞤵𞤣𞤫. 𞤂𞤫𞤴𞤯𞤮𞤤 𞤦𞤫𞤯𞤫 𞤲𞤺𞤮𞤤 𞤨𞤮𞤪𞤮. 𞤐𞤢𞥄𞤺𞤫 𞤸𞤢𞤪𞤢 𞤱𞤢𞤣 𞤲𞤣𞤫 𞤲𞤢𞥄𞤥 𞤨𞤮𞤪.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "Le berger est allé dans la brousse. Il a emmené sa vache. La vache commence à manger de l'herbe verte. Le berger s'assoit sous un arbre. La vache trouve de l'eau et du lait frais.",
            wordTimings = null,
            tags = listOf("pastoralisme", "nature"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 24,
            title = "𞤞𞤫𞤯𞤵 𞤫 𞤳𞤢𞤲", // "Le voyage au village"
            adlamText = "𞤐𞤺𞤮𞤲 𞤸𞤢𞤤𞤢 𞤞𞤫𞤯𞤵 𞤲𞥋𞤣𞤫𞤪 𞤱𞤮𞤪𞤮 𞤣𞤢𞤦 𞤥𞤢𞤳𞥆𞤮. 𞤌 𞤺𞤢𞤤𞤤𞤭 𞤢𞤤𞤢 𞤯𞤫 𞤣𞤫𞤧𞤢 𞤶𞤢𞤣𞤭𞤲𞤺𞤮 𞤸𞤮𞤪𞤫 𞤥𞤢𞤳𞥆𞤮. 𞤞𞤮 𞤱𞤢𞤯𞤲𞤭 𞤦𞤮𞤺𞥆𞤫 𞤶𞤢𞤣𞤭, 𞤌 𞤸𞤢𞥅𞥅 𞤸𞤭𞤲𞤢𞤦𞤫 𞤦𞤫𞤣 𞤲𞥋𞤺𞤢𞤴 𞤥𞤢𞤳𞥆𞤮. 𞤂𞤫 𞤹𞤢𞤦𞤵 𞤦𞤫𞤣 𞤫 𞤻𞤫𞤣𞤭.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "Ngon a décidé de voyager vers le village de ses ancêtres. Il a demandé à Dieu de bénir son voyage vers le village de son frère. Quand il arrive le matin, il voit beaucoup d'enfants et de gens de son village. Ils se réjouissent beaucoup et célèbrent.",
            wordTimings = null,
            tags = listOf("voyage", "famille"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 25,
            title = "𞤚𞤫𞤤𞤮 𞤫 𞤢𞤲𞤣𞤮", // "Le forgeron et le feu"
            adlamText = "𞤚𞤫𞤤𞤮 𞤲𞥋𞤺𞤢 𞤸𞤢𞤤𞤢 𞤺𞤫 𞤢𞤲𞤣𞤮 𞤫 𞤤𞤮𞤦𞤮 𞤶𞤢 𞤻𞤢𞤥 𞤯𞤢𞤣𞤫. 𞤌 𞤸𞤢𞤤 𞤯𞤢𞤣𞤫 𞤢𞤲𞤣𞤮 𞤯𞤫 𞤬𞤢𞤴 𞤬𞤮𞤼𞤮 𞤯𞤮𞤮. 𞤐𞥋𞤺𞤢𞤴 𞤲𞥋𞤺𞤢 𞤱𞤢𞤣 𞤸𞤫𞤬 𞤢𞤲𞤣𞤮 𞤥𞤢𞤳𞥆𞤮 𞤯𞤫 𞤱𞤢𞤣 𞤧𞤢𞤴 𞤞𞤫𞤲𞤣𞤮. 𞤚𞤫𞤤𞤮 𞤦𞤫𞤯𞤫 𞤹𞤢𞤦𞤫 𞤫 𞤺𞤮𞤤 𞤦𞤢𞤦 𞤥𞤢𞤳𞥆𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "Le forgeron a décidé de travailler avec le fer et le feu pour fabriquer des outils. Il fait chauffer le fer au feu pour le rendre très malléable. Les gens viennent voir le beau fer de son village et acheter de bons outils. Le forgeron s'assoit content et satisfait de son père.",
            wordTimings = null,
            tags = listOf("artisanat", "travail"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 26,
            title = "𞤀𞤪𞤣𞤮 𞤫 𞤲𞤺𞤫𞤤", // "Le mariage et la fête"
            adlamText = "𞤀𞤪𞤣𞤮 𞤦𞤫𞤱𞤮 𞤸𞤢𞥅 𞤲𞥋𞤣𞤫𞤪 𞤱𞤮𞤪𞤮 𞤥𞤢𞤳𞥆𞤮. 𞤐𞥋𞤺𞤢𞤴 𞤦𞤫𞤣 𞤱𞤢𞤣 𞤧𞤢𞤴 𞤦𞤮𞤯𞤮 𞤫 𞤸𞤭𞤪𞤫 𞤫 𞤲𞤢𞥄𞤥. 𞤛𞤮𞤦𞤮 𞤫 𞤐𞤺𞤮𞤯𞤫 𞤫 𞤸𞤢𞤳𞤮 𞤸𞤢𞤤 𞤲𞤺𞤫𞤤 𞤦𞤫𞤱𞤮. 𞤐𞤺𞤫𞤤 𞤦̣𞤫𞤴̈ 𞤭𞤩 𞤥𞤢𞤦𞤫 𞤯𞤢𞤼𞤭 𞤫 𞤻𞤵𞤳𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "Un grand mariage a eu lieu dans son village. Beaucoup de gens sont venus acheter de la viande, du mil et du lait. Les tambours, les flûtes et le vent font une grande fête. La fête continue toute la semaine avec des danses et des chants.",
            wordTimings = null,
            tags = listOf("célébration", "culture"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 27,
            title = "𞤃𞤢𞤤𞤤𞤮 𞤫 𞤲𞤣𞤫𞤥", // "Le pêcheur et la pluie"
            adlamText = "𞤃𞤢𞤤𞤤𞤮 𞤸𞤢𞤴𞤢 𞤴𞤢𞤯 𞤲𞥋𞤣𞤫𞤪 𞤦𞤫𞤤𞥄𞤯𞤭 𞤲𞤣𞤫 𞤲𞤣𞤫𞤥. 𞤌 𞤶𞤮𞤯𞤫 𞤲𞤣𞤫 𞤥𞤮𞤤𞤮 𞤬𞤮𞤳 𞤯𞤮𞤮. 𞤐𞤣𞤫𞤥 𞤸𞤢𞤫 𞤸𞤢𞤫 𞤫 𞤸𞤢𞤳𞤮 𞤦𞤫𞤱𞤮. 𞤃𞤢𞤤𞤤𞤮 𞤲𞤺𞤮𞤤 𞤸𞤢𞥅 𞤸𞤢𞤳𞤮 𞤦𞤫𞤱𞤮 𞤯𞤫 𞤥𞤮𞤤𞤮 𞤬𞤮𞤳 𞤸𞤫𞤬 𞤯𞤮𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "Le pêcheur est sorti pêcher dans la rivière sous la pluie. Il s'assoit dans l'eau très froide. La pluie tombe avec un grand vent. Le pêcheur commence à voir le grand vent et l'eau froide devenir très belle.",
            wordTimings = null,
            tags = listOf("pêche", "météo"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 28,
            title = "𞤅𞤫𞤳𞤫 𞤫 𞤱𞤢𞤳𞤵", // "Le marché et le commerce"
            adlamText = "𞤅𞤫𞤳𞤫 𞤸𞤢𞥅 𞤲𞥋𞤣𞤫𞤪 𞤱𞤮𞤪𞤮 𞤦𞤫𞤱𞤮 𞤞𞤢𞤤𞤢𞤻𞤫. 𞤐𞥋𞤺𞤢𞤴 𞤦𞤫𞤣 𞤱𞤢𞤣 𞤧𞤢𞤴 𞤦𞤮𞤯𞤮 𞤫 𞤲𞤣𞤵 𞤫 𞤲𞤢𞥄𞤥. 𞤌 𞤸𞤢𞥅 𞤥𞤢𞥄𞤦𞤫 𞤦𞤫𞤣 𞤲𞥋𞤺𞤢 𞤷𞤮𞤺 𞤸𞤮𞤤𞤵 𞤲𞥋𞤺𞤫 𞤫 𞤞𞤢𞤤𞤣𞤫. 𞤒𞤮 𞤞𞤢𞤤𞤢𞤻𞤫 𞤲𞤣𞤫𞤥 𞤶𞤫𞤱 𞤸𞤢𞤫, 𞤘𞤫 𞤞𞤮𞤢𞤣 𞤺𞤢𞤤𞤫 𞤥𞤢𞤱.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "Le marché a lieu dans un grand village le jeudi. Beaucoup de gens viennent acheter de la viande, du riz et du lait. Il y a aussi beaucoup de commerçants qui cherchent à vendre des vêtements et des tissus. Quand le jeudi se termine, ils retournent à leurs maisons.",
            wordTimings = null,
            tags = listOf("commerce", "économie"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 29,
            title = "𞤊𞤢𞤴𞤲𞤢𞤢𞤦𞤫 𞤫 𞤫𞤯𞤵", // "La saison sèche et les animaux"
            adlamText = "𞤊𞤢𞤴𞤲𞤢𞤢𞤦𞤫 𞤱𞤢𞤯𞤲𞤭 𞤸𞤫𞤼𞤫 𞤞𞤮𞤯, 𞤫𞤯𞤵 𞤦𞤫𞤣 𞤷𞤮𞤺 𞤲𞤣𞤫. 𞤐𞤢𞥄𞤺𞤫 𞤫 𞤢𞤺𞤢𞤣𞤫 𞤴𞤢𞤸 𞤞𞤮𞤺 𞤲𞤣𞤫 𞤲𞥋𞤣𞤫𞤪 𞤦𞤫𞤤𞥄𞤯𞤭 𞤫 𞤪𞤮𞥅𞤯𞤫. 𞤂𞤫𞤴𞤯𞤮𞤤 𞤦𞤫𞤣 𞤞𞤢𞤤𞤤𞤢 𞤯𞤫 𞤶𞤢𞤴 𞤲𞤣𞤫 𞤲𞤢𞤳𞤢 𞤫𞤯𞤵 𞤥𞤢𞤱. 𞤚𞤮 𞤞𞤢𞤤𞤢𞤻𞤫 𞤺𞤢𞤱 𞤞𞤫 𞤦𞤮𞤺𞥆𞤫 𞤱𞤢𞤣, 𞤫𞤯𞤵 𞤦𞤫𞤣 𞤞𞤮𞤢𞤣 𞤺𞤢𞤤𞤫 𞤥𞤢𞤱.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "Pendant la saison sèche qui arrive en décembre, beaucoup d'animaux cherchent de l'eau. Les vaches et les moutons vont chercher de l'eau dans les rivières et les puits. Beaucoup de bergers prient pour qu'il y ait de l'eau suffisante pour leurs animaux. Quand le jeudi matin arrive, les animaux retournent à leurs maisons.",
            wordTimings = null,
            tags = listOf("agriculture", "saisons"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 30,
            title = "𞤋𞤢𞤲𞤺𞤮 𞤫 𞤶𞤢𞤪𞤢𞤲𞤺𞤮", // "La connaissance et l'apprentissage"
            adlamText = "𞤋𞤢𞤲𞤺𞤮 𞤸𞤭𞤲𞤫 𞤲𞤢 𞤥𞤮𞤯𞤮 𞤲𞤣𞤭𞤥 𞤸𞤢𞤤 𞤳𞤮𞥅 𞤩𞤮𞤻𞤵. 𞤗𞤮 𞤯𞤮 𞤸𞤭𞤲𞤢 𞤶𞤢𞤪 𞤶𞤢𞤪𞤢𞤲𞤺𞤮, 𞤌 𞤧𞤮𞤯 𞤦𞤢𞤦 𞤥𞤢𞤳𞥆𞤮. 𞤂𞤢𞤦𞤫 𞤥𞤢𞤳𞥆𞤮 𞤦𞤢𞤯 𞤶𞤢𞤪𞤢𞤲𞤺𞤮 𞤯𞤫 𞤸𞤮𞤪𞤫 𞤫 𞤦𞤢𞥄𞤪𞤭 𞤥𞤢𞤳𞥆𞤮. 𞤋𞤢𞤲𞤺𞤮 𞤸𞤭𞤲𞤫 𞤸𞤢𞤫 𞤧𞤳𞤮𞤯 𞤫 𞤺𞤢𞤤𞤫, 𞤌 𞤦𞤢𞤯 𞤸𞤢𞤤 𞤸𞤢𞤳 𞤯𞤮𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "La connaissance est quelque chose de très précieux qui fait toute chose belle. Quand un enfant apprend à lire, il devient intelligent comme son père. Le père intelligent enseigne la lecture à son frère et à sa sœur. La connaissance qui vient de l'école et de la maison est très utile.",
            wordTimings = null,
            tags = listOf("éducation", "famille"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 31,
            title = "𞤞𞤢𞤥𞤢 𞤫 𞤲𞤢𞥄𞤥", // "Le lait et la nutrition"
            adlamText = "𞤞𞤢𞤥𞤢 𞤲𞤢 𞤲𞤢𞥄𞤺𞤫 𞤸𞤢𞤦 𞤲𞤢𞥄𞤥 𞤸𞤮𞤣 𞤯𞤮𞤮. 𞤐𞤢𞥄𞤥 𞤲𞤢 𞤸𞤢𞤦 𞤲𞤢𞥄𞤺𞤫 𞤸𞤭𞤲𞤫 𞤸𞤵𞤯𞤫 𞤫 𞤸𞤭𞤲𞤢𞤦𞤫. 𞤐𞥋𞤺𞤢𞤴 𞤩𞤮𞤻𞤢 𞤲𞤢𞥄𞤥 𞤲𞤣𞤫 𞤸𞤫𞤦𞤫 𞤫 𞤸𞤭𞤪 𞤫 𞤤𞤢𞤦𞤢𞥄𞤤. 𞤐𞤢𞥄𞤥 𞤦𞤫𞤱𞤮 𞤸𞤢𞤦 𞤸𞤭𞤲𞤢𞤦𞤫 𞤦𞤮𞤻 𞤫 𞤬𞤢𞤴 𞤶𞤢𞤳, 𞤯𞤫 𞤦𞤢𞤦 𞤫 𞤩𞤢𞤼𞤫 𞤦𞤮𞤻 𞤯𞤮𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "Maman trait sa vache pour obtenir du bon lait. Le lait que donne la vache est bon pour les adultes et les enfants. Les gens boivent le lait avec du pain, du mil et de l'herbe. Le bon lait aide les enfants à grandir et à avoir de la force, pour que le père et la mère grandissent bien aussi.",
            wordTimings = null,
            tags = listOf("alimentation", "santé"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 32,
            title = "𞤤𞤫𞤦𞤣𞤫 𞤫 𞤻𞤢𞤥 𞤳𞤮𞥅", // "Les arbres et la fabrication"
            adlamText = "𞤤𞤫𞤦𞤣𞤫 𞤶𞤮𞤺 𞤲𞥋𞤣𞤫𞤪 𞤤𞤢𞤣𞥆𞤫 𞤫 𞤺𞤢𞤤𞤫. 𞤘𞤫 𞤸𞤢𞤦 𞤺𞤫𞤲 𞤫 𞤤𞤢𞤦𞤢𞥄𞤤 𞤫 𞤸𞤢𞤳𞤮 𞤫 𞤲𞥋𞤺𞤫. 𞤐𞥋𞤺𞤢𞤴 𞤩𞤮𞤻 𞤞𞤢𞤯 𞤸𞤢𞤣𞤭 𞤫 𞤻𞤢𞤥 𞤢𞤲𞤣𞤮 𞤫 𞤳𞤮𞥅 𞤦𞤫𞤣. 𞤤𞤫𞤦𞤣𞤫 𞤸𞤢𞤦 𞤯𞤮 𞤲𞥋𞤺𞤢 𞤸𞤢𞤤 𞤳𞤮𞞤���𞤯 𞤯𞤫 𞤻𞤢𞤥 𞤺𞤢𞤤𞤫 𞤸ع<+EOF_114711+> 𞤯𞤮𞤮. 𞤐𞥋𞤺𞤢𞤴 𞤳𞤢𞤦 𞤯𞤮 𞤦𞤫𞤣 𞤳𞤮 𞤸ؤاؤثأب<EOF_114711> 𞤲𞤺ِّࣹ 𞤲𞤺𞤫 𞤳𞤵ٰأع𞤧𞤭",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen", 
            frenchTranslation = "Les arbres poussent dans la brousse et près des maisons. Ils donnent des fruits, de l'herbe, du vent et de l'ombre. Les gens utilisent le bois pour faire du feu et fabriquer des objets en fer et beaucoup d'autres choses. Les arbres donnent ce qu'il faut aux gens pour faire des choses et construire de très belles maisons. Les gens gardent ce qu'il faut pour faire des choses avec le fer et d'autres outils.",
            wordTimings = null,
            tags = listOf("nature", "artisanat"),
            isFavorite = false
        ),
        
        // ========== NIVEAU DIFFICILE (20+ passages) ==========
        ReadingPassage(
            id = 43,
            title = "𞤋𞤢𞤲𞤺𞤮 𞤫 𞤧𞤫𞤯𞤵𞤲 𞤫 𞤸𞤢𞤯𞤢 𞤬𞤮𞤻", // "La sagesse et la tradition et les grands défis"
            adlamText = "𞤋𞤢𞤲𞤺𞤮 𞤧𞤫𞤯𞤵𞤲 𞤲𞤢 𞤸𞤵𞤯𞤫 𞤲𞥋𞤣𞤫𞤪 𞤬𞤵𞤤𞤦𞤫 𞤯𞤫 𞤺𞤫 𞤸𞤢𞤯𞤢 𞤬𞤮𞤻 𞤦𞤫𞤣 𞤲𞥋𞤣𞤫𞤪 𞤢𞤣𞤵𞤲𞤢. 𞤒𞤮 𞤲𞥋𞤺𞤢 𞤸𞤫𞤦 𞤶𞤢𞤪 𞤯𞤮 𞤞𞤫𞤣𞤵 𞤲𞥋𞤣𞤫𞤪 𞤻𞤢𞤥 𞤺𞤫, 𞤌 𞤳𞤮𞤯 𞤸𞤢𞤤 𞤯𞤮 𞤞𞤫𞤣𞤵 𞤸𞤢𞤯𞤮 𞤫 𞤸𞤭𞤳𞤭 𞤧𞤫𞤯𞤵 𞤦𞤫𞤣. 𞤂𞤢𞤦𞤫 𞤲𞤺𞤮𞤤 𞤫 𞤐𞤫𞤯𞤮 𞤞𞤢𞤤𞤤 𞤫 𞤞𞤮𞤯 𞤢𞤤 𞤯𞤢𞤣𞤫 𞤹𞤫𞥅𞤯𞤫 𞤲𞤺𞤮 𞤸𞤢𞤦 𞤸𞤭𞤲𞤢𞤦𞤫 𞤥𞤢𞤱 𞤩𞤮𞤻 𞤦𞤫𞤣 𞤫 𞤯𞤢𞤴 𞤲𞤺𞤮𞤯 𞤧𞤫𞤯𞤵𞤲 𞤸𞤢𞤤 𞤯𞤮 𞤥𞤮𞤯.",
            soundResId = R.raw.son_nul,
            difficulty = "Difficile",
            frenchTranslation = "La sagesse traditionnelle réside chez les Peuls et face aux grands défis du monde moderne. Quand quelqu'un veut apprendre quelque chose de nouveau, il doit d'abord étudier ce qui était auparavant et comprendre beaucoup de traditions anciennes. Le père commence avec la mère à prier et donner à leurs enfants la chance d'apprendre beaucoup et de recevoir la tradition comme quelque chose de précieux.",
            wordTimings = null,
            tags = listOf("philosophie", "culture", "tradition"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 44,
            title = "𞤚𞤢𞥄𞤯𞤢 𞤸𞤢𞤤𞤵 𞤫 𞤯𞤢𞤣𞤫 𞤬𞤮𞤻", // "Le leadership et les responsabilités importantes"
            adlamText = "𞤚𞤢𞥄𞤯𞤢 𞤸𞤢𞤤𞤵 𞤲𞤢 𞤯𞤢𞤣𞤫 𞤦𞤫𞤱𞤮 𞤲𞥋𞤣𞤫𞤪 𞤱𞤮𞤪𞤮 𞤫 𞤞𞤫𞤲𞤣𞤮 𞤫 𞤲𞤺𞤮 𞤯𞤮 𞤩𞤮𞤻 𞤦𞤫𞤣. 𞤌 𞤢𞤤𞤢 𞤳𞤮𞤯 𞤸𞤢𞤤 𞤯𞤢𞤣𞤫 𞤥𞤢𞤳𞥆𞤮 𞤯𞤫 𞤞𞤢𞤯 𞤦𞤢𞤦 𞤢𞤥 𞤯𞤫 𞤺𞤫 𞤸𞤢𞤤𞤵 𞤬𞤮𞤻 𞤯𞤮 𞤳𞤮𞥅. 𞤚𞤢𞥄𞤯𞤢 𞤞𞤢𞤤𞤤 𞤯𞤫 𞤸𞤵𞤯𞤫 𞤻𞤢𞤥 𞤧𞤫𞤯𞤵 𞤦𞤫𞤣 𞤯𞤫 𞤱𞤢𞤣 𞤻𞤢𞤥 𞤳𞤮 𞤯𞤫 𞤲𞥋𞤺𞤢𞤴 𞤸𞤢𞤤 𞤦𞤫𞤣 𞤫 𞤯𞤢𞤴 𞤸𞤫𞤬 𞤦𞤫𞤣 𞤲𞤺𞤮 𞤱𞤮𞤪𞤮 𞤥𞤢𞤱. 𞤌 𞤢𞤤𞤢 𞤳𞤮𞤯 𞤞𞤢𞤯 𞤸𞤮𞤪𞤫 𞤥𞤢𞤳𞥆𞤮 𞤯𞤫 𞤦𞤢𞥄𞤪𞤭 𞤯𞤫 𞤸𞤢𞤤 𞤯𞤮 𞤞𞤫𞤣𞤵 𞤫 𞤞𞤮𞤯 𞤞𞤮𞤯 𞤳𞤮𞞤���𞤴 𞤞𞤢𞤯.",
            soundResId = R.raw.son_nul,
            difficulty = "Difficile",
            frenchTranslation = "Le leadership est une grande responsabilité dans le village, les outils et tout ce qui est important. Il faut avoir de la responsabilité envers son père et travailler avec diligence pour toutes les choses importantes. Le leader prie et travaille avec beaucoup de traditions anciennes et fait des choses pour que les gens puissent faire beaucoup et avoir de belles choses dans leur village. Il faut aussi savoir comment aider son frère et sa sœur et avoir de l'apprentissage et de la patience avec beaucoup de choses importantes.",
            wordTimings = null,
            tags = listOf("leadership", "responsabilité", "société"),
            isFavorite = false
        ),
        
        // Continuation du niveau Moyen pour atteindre 20+
        ReadingPassage(
            id = 33,
            title = "𞤧𞤫𞤯𞤵𞤲 𞤫 𞤻𞤢𞤥 𞤸𞤢𞤣𞤭𞤲𞤺𞤮", // "La tradition et la création d'histoires"
            adlamText = "𞤧𞤫𞤯𞤵𞤲 𞤲𞤢 𞤬𞤵𞤤𞤦𞤫 𞤸𞤢𞤤 𞤸𞤢𞤣𞤭𞤲𞤺𞤮 𞤬𞤮𞤻 𞤯𞤮𞤮. 𞤂𞤢𞤦𞤫 𞤫 𞤐𞤢𞤲𞥋𞤺𞤢 𞤦𞤫𞤣 𞤸𞤢𞤤 𞤸𞤢𞤣𞤭𞤲𞤺𞤮 𞤯𞤫 𞤯𞤵𞤩 𞤸𞤭𞤲𞤢𞤦𞤫 𞤥𞤢𞤱. 𞤸𞤢𞤣𞤭𞤲𞤺𞤮 𞤸𞤢𞤤 𞤯𞤮 𞤞𞤫𞤣𞤵 𞤫 𞤶𞤢𞤪𞤢𞤲𞤺𞤮 𞤯𞤮 𞤩𞤮𞤻 𞤯𞤮𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "La tradition peule crée de très belles histoires. Les pères et grands-mères racontent des histoires pour enseigner aux enfants. Les histoires qui enseignent et éduquent sont très importantes.",
            wordTimings = null,
            tags = listOf("tradition", "éducation"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 34,
            title = "𞤞𞤢𞤴𞤲𞤢𞤢𞤦𞤫 𞤫 𞤻𞤵𞤲𞤺𞤮", // "La saison sèche et l'hivernage"
            adlamText = "𞤞𞤢𞤴𞤲𞤢𞤢𞤦𞤫 𞤱𞤢𞤯𞤲𞤭 𞤞𞤮 𞤻𞤵𞤲𞤺𞤮 𞤫 𞤲𞤣𞤫 𞤞𞤫 𞤼𞤢𞤯 𞤞𞤮 𞤢𞤺𞤢. 𞤞𞤮 𞤞𞤢𞤴𞤲𞤢𞤢𞤦𞤫, 𞤲𞤣𞤫 𞤻𞤮𞤥 𞤯𞤮𞤮 𞤫 𞤲𞤣𞤫𞤥 𞤱𞤮𞤯𞤢. 𞤞𞤮 𞤻𞤵𞤲𞤺𞤮, 𞤲𞤣𞤫𞤥 𞤸𞤢𞤫 𞤦𞤫𞤣 𞤫 𞤤𞤫𞤣𞥆𞤫 𞤦𞤮𞤻 𞤸𞤫𞤬.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "La saison sèche arrive quand l'hivernage se termine. Pendant la saison sèche, l'eau se tarit beaucoup et il n'y a pas de pluie. Pendant l'hivernage, la pluie tombe beaucoup et les cultures poussent bien.",
            wordTimings = null,
            tags = listOf("saisons", "agriculture"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 35,
            title = "𞤤𞤢𞤺𞤢𞤤 𞤫 𞤣𞤵𞤲𞤣𞤫", // "Le voyage et l'aventure"
            adlamText = "𞤤𞤢𞤺𞤢𞤤 𞤲𞤢 𞤣𞤵𞤲𞤣𞤫 𞤣𞤮 𞤞𞤫𞤯𞤵 𞤲𞥋𞤣𞤫𞤪 𞤣𞤮 𞤞𞤫𞤲𞤣𞤮 𞤫 𞤣𞤮 𞤞𞤫𞤲𞤣𞤮 𞤫 𞤣𞤵𞤲𞤣𞤫. 𞤤𞤢𞤺𞤢𞤤 𞤲𞤢 𞤶𞤢𞤴 𞤸𞤭𞤲𞤢𞤦𞤫 𞤩𞤮𞤻 𞤦𞤫𞤣 𞤫 𞤞𞤢𞤯 𞤸𞤫𞤬 𞤣𞤵𞤲𞤣𞤫. 𞤞𞤮 𞤸𞤭𞤲𞤢 𞤶𞤢𞤪 𞤣𞤵𞤲𞤣𞤫 𞤦𞤫𞤣, 𞤌 𞤶𞤢𞤴 𞤮 𞤧𞤮𞤯 𞤩𞤢𞥄𞤯𞤮 𞤯𞤮𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Moyen",
            frenchTranslation = "Le voyage est une aventure qui nous mène vers de nouveaux lieux et de nouvelles expériences. Le voyage donne aux enfants beaucoup de choses et de belles aventures. Quand un enfant apprend beaucoup d'aventures, cela lui donne beaucoup de sagesse.",
            wordTimings = null,
            tags = listOf("voyage", "aventure"),
            isFavorite = false
        ),
        
        // Continuer avec plus de passages difficiles pour atteindre 20+
        ReadingPassage(
            id = 45,
            title = "𞤢𞤺𞤢𞤣𞤫 𞤫 𞤲𞤢𞥄𞤺𞤫 𞤫 𞤸𞤢𞤤𞤵 𞤣𞤮 𞤦𞤫𞤱𞤮", // "Les moutons et les vaches et l'administration communautaire"
            adlamText = "𞤢𞤺𞤢𞤣𞤫 𞤫 𞤲𞤢𞥄𞤺𞤫 𞤲𞤢 𞤫𞤯𞤵 𞤦𞤫𞤣 𞤲𞥋𞤣𞤫𞤪 𞤣𞤫𞤲𞤣𞤢𞤲 𞤬𞤵𞤤𞤦𞤫 𞤯𞤫 𞤸𞤢𞤤 𞤸𞤢𞤤𞤵 𞤣𞤮 𞤦𞤫𞤱𞤮 𞤯𞤮 𞤞𞤢𞤣𞤫. 𞤂𞤫𞤴𞤯𞤮𞤤 𞤦𞤫𞤣 𞤫 𞤲𞤺𞤮𞤴 𞤥𞤢𞤱 𞤞𞤢𞤤𞤤𞤢 𞤞𞤮𞤯 𞤫𞤯𞤵 𞤥𞤢𞤱 𞤞𞤫𞤯 𞤞𞤢𞤯 𞤺𞤫 𞤦𞤢𞤦 𞤫 𞤳𞤮𞤦 𞤫 𞤸𞤢𞤤 𞤯𞤮 𞤞𞤫𞤣𞤵 𞤞𞤫 𞤸𞤫𞤣 𞤣𞤮 𞤞𞤢𞤳. 𞤂𞤫𞤴𞤯𞤮 𞤩𞤮𞤻 𞤞𞤢𞤯 𞤫 𞤶𞤢𞤪 𞤯𞤮 𞤞𞤫𞤣𞤵 𞤞𞤫 𞤲𞤺𞤮 𞤸𞤢𞤤 𞤸𞤢𞤤𞤵 𞤣𞤮 𞤦𞤫𞤱𞤮.",
            soundResId = R.raw.son_nul,
            difficulty = "Difficile",
            frenchTranslation = "Les moutons et les vaches sont des animaux importants dans la culture peule qui nécessitent une administration communautaire complexe. Les bergers et leurs familles prient pour que leurs animaux soient bien protégés par le père et les anciens et apprennent ce qui est nécessaire pour la gestion communautaire. L'élevage nécessite savoir et apprentissage de ce qui est requis pour l'administration communautaire.",
            wordTimings = null,
            tags = listOf("élevage", "administration", "communauté"),
            isFavorite = false
        ),
        ReadingPassage(
            id = 46,
            title = "𞤸𞤢𞤣𞤭𞤲𞤺𞤮 𞤧𞤫𞤯𞤵𞤲 𞤫 𞤞𞤢𞤣𞤫 𞤞𞤫 𞤬𞤮𞤻", // "Les récits traditionnels et les responsabilités importantes"
            adlamText = "𞤸𞤢𞤣𞤭𞤲𞤺𞤮 𞤧𞤫𞤯𞤵𞤲 𞤲𞤢 𞤞𞤫𞤣𞤵 𞤞𞤫 𞤸𞤢𞤤 𞤞𞤢𞤣𞤫 𞤞𞤫 𞤬𞤮𞤻 𞤦𞤫𞤣 𞤲𞥋𞤣𞤫𞤪 𞤞𞤫𞤲𞤣𞤮 𞤫 𞤞𞤫𞤲𞤣𞤮 𞤫 𞤞𞤫𞤲𞤣𞤮. 𞤂𞤢𞤦𞤫 𞤞𞤢𞤯 𞤸𞤢𞤣𞤭𞤲𞤺𞤮 𞤞𞤫 𞤸𞤭𞤲𞤢𞤦𞤫 𞤯𞤫 𞤞𞤢𞤯 𞤞𞤫 𞤸𞤢𞤤 𞤞𞤫𞤣𞤵 𞤞𞤫 𞤞𞤢𞤣𞤫 𞤞𞤫 𞤬𞤮𞤻 𞤞𞤮 𞤬𞤵𞤤𞤦𞤫. 𞤸𞤢𞤣𞤭𞤲𞤺𞤮 𞤞𞤮 𞤞𞤢𞤯 𞤞𞤫 𞤶𞤢𞤪 𞤞𞤮 𞤳𞤮𞤲 𞤫 𞤶𞤢𞤪 𞤞𞤮 𞤩𞤢𞥄𞤯𞤮 𞤫 𞤶𞤢𞤪 𞤞𞤮 𞤸𞤢𞤤𞤵.",
            soundResId = R.raw.son_nul,
            difficulty = "Difficile",
            frenchTranslation = "Les récits traditionnels enseignent les importantes responsabilités dans les outils et les lieux différents. Le père raconte les histoires aux enfants et leur enseigne les importantes responsabilités chez les Peuls. Les histoires qui enseignent la compréhension, la sagesse et la direction.",
            wordTimings = null,
            tags = listOf("récits", "responsabilité", "tradition"),
            isFavorite = false
        )
    )
}