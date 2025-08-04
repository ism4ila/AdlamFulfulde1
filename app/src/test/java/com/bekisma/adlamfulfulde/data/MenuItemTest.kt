package com.bekisma.adlamfulfulde.data

import com.bekisma.adlamfulfulde.R
import org.junit.Test

class MenuItemTest {

    @Test
    fun `MenuItem should be created with correct properties`() {
        val imageRes = R.drawable.abc64
        val titleRes = R.string.alphabet_learning
        val subtitleRes = R.string.discover_the_adlam_alphabet
        val destination = "alphabet"
        
        val menuItem = MenuItem(
            imageRes = imageRes,
            titleRes = titleRes,
            subtitleRes = subtitleRes,
            destination = destination
        )
        
        assert(menuItem.imageRes == imageRes)
        assert(menuItem.titleRes == titleRes)
        assert(menuItem.subtitleRes == subtitleRes)
        assert(menuItem.destination == destination)
    }

    @Test
    fun `MenuItem should support equality comparison`() {
        val menuItem1 = MenuItem(
            imageRes = R.drawable.abc64,
            titleRes = R.string.alphabet_learning,
            subtitleRes = R.string.discover_the_adlam_alphabet,
            destination = "alphabet"
        )
        
        val menuItem2 = MenuItem(
            imageRes = R.drawable.abc64,
            titleRes = R.string.alphabet_learning,
            subtitleRes = R.string.discover_the_adlam_alphabet,
            destination = "alphabet"
        )
        
        assert(menuItem1 == menuItem2)
        assert(menuItem1.hashCode() == menuItem2.hashCode())
    }

    @Test
    fun `MenuItem should detect inequality correctly`() {
        val menuItem1 = MenuItem(
            imageRes = R.drawable.abc64,
            titleRes = R.string.alphabet_learning,
            subtitleRes = R.string.discover_the_adlam_alphabet,
            destination = "alphabet"
        )
        
        val menuItem2 = MenuItem(
            imageRes = R.drawable.abc64,
            titleRes = R.string.alphabet_learning,
            subtitleRes = R.string.discover_the_adlam_alphabet,
            destination = "different_destination"
        )
        
        assert(menuItem1 != menuItem2)
    }

    @Test
    fun `MenuItem should support copy with changes`() {
        val original = MenuItem(
            imageRes = R.drawable.abc64,
            titleRes = R.string.alphabet_learning,
            subtitleRes = R.string.discover_the_adlam_alphabet,
            destination = "alphabet"
        )
        
        val modified = original.copy(destination = "new_destination")
        
        assert(modified.imageRes == original.imageRes)
        assert(modified.titleRes == original.titleRes)
        assert(modified.subtitleRes == original.subtitleRes)
        assert(modified.destination == "new_destination")
        assert(modified != original)
    }

    @Test
    fun `MenuItem should have proper toString representation`() {
        val menuItem = MenuItem(
            imageRes = R.drawable.abc64,
            titleRes = R.string.alphabet_learning,
            subtitleRes = R.string.discover_the_adlam_alphabet,
            destination = "alphabet"
        )
        
        val toString = menuItem.toString()
        
        assert(toString.contains("MenuItem"))
        assert(toString.contains("alphabet"))
    }

    @Test
    fun `MenuItem should handle different resource IDs`() {
        val menuItem = MenuItem(
            imageRes = 0, // Test with 0 resource ID
            titleRes = -1, // Test with negative resource ID
            subtitleRes = Int.MAX_VALUE, // Test with max value
            destination = "test"
        )
        
        assert(menuItem.imageRes == 0)
        assert(menuItem.titleRes == -1)
        assert(menuItem.subtitleRes == Int.MAX_VALUE)
        assert(menuItem.destination == "test")
    }

    @Test
    fun `MenuItem should handle empty and special destination strings`() {
        val emptyDestination = MenuItem(
            imageRes = R.drawable.abc64,
            titleRes = R.string.alphabet_learning,
            subtitleRes = R.string.discover_the_adlam_alphabet,
            destination = ""
        )
        
        val specialCharsDestination = MenuItem(
            imageRes = R.drawable.abc64,
            titleRes = R.string.alphabet_learning,
            subtitleRes = R.string.discover_the_adlam_alphabet,
            destination = "path/with/slashes_and-dashes"
        )
        
        val unicodeDestination = MenuItem(
            imageRes = R.drawable.abc64,
            titleRes = R.string.alphabet_learning,
            subtitleRes = R.string.discover_the_adlam_alphabet,
            destination = "𞤀𞤣𞤤𞤢𞤥"
        )
        
        assert(emptyDestination.destination == "")
        assert(specialCharsDestination.destination == "path/with/slashes_and-dashes")
        assert(unicodeDestination.destination == "𞤀𞤣𞤤𞤢𞤥")
    }

    @Test
    fun `MenuItem should work in collections`() {
        val items = listOf(
            MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "alphabet"),
            MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "numbers"),
            MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "writing")
        )
        
        assert(items.size == 3)
        
        val alphabetItem = items.find { it.destination == "alphabet" }
        assert(alphabetItem != null)
        assert(alphabetItem?.destination == "alphabet")
        
        val destinations = items.map { it.destination }
        assert(destinations.contains("alphabet"))
        assert(destinations.contains("numbers"))
        assert(destinations.contains("writing"))
    }

    @Test
    fun `MenuItem should work as map keys`() {
        val item1 = MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "alphabet")
        val item2 = MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "numbers")
        
        val map = mapOf(
            item1 to "Learning Alphabet",
            item2 to "Learning Numbers"
        )
        
        assert(map[item1] == "Learning Alphabet")
        assert(map[item2] == "Learning Numbers")
        assert(map.size == 2)
    }
}