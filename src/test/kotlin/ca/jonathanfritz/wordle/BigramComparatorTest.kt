package ca.jonathanfritz.wordle

import ca.jonathanfritz.wordle.comparators.BigramComparator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BigramComparatorTest {

    private val comparator = BigramComparator()

    @Test
    fun scoringTest() {
        // the highest possible bigram score
        assertThat(comparator.score("TH")).isEqualTo(100)

        // the lowest possible bigram score
        assertThat(comparator.score("QZ")).isEqualTo(1)

        // duplicate bigrams are ignored
        // this string contains one instance of "th" and another of "ht"
        assertThat(comparator.score("thth")).isEqualTo(100 + 46)

        // known score check - contains "th", "ha", and "at"
        assertThat(comparator.score("that")).isEqualTo(100 + 89 + 94)

        // known score check - contains "fo", "oc", "cu", and "us"
        assertThat(comparator.score("focus")).isEqualTo(72 + 44 + 33 + 66)
    }

    @Test
    fun comparisonTest() {
        // equality test
        assertThat(comparator.compare("hello", "hello")).isEqualTo(0)

        // duplicate bigrams do not count towards score
        assertThat(comparator.compare("helol", "helolo")).isEqualTo(0)

        // CH is scored lower than TH
        assertThat(comparator.compare("chat", "that")).isEqualTo(26)

        // comparisons are commutative (sort of - they get negated when flipped)
        assertThat(comparator.compare("that", "chat")).isEqualTo(-26)

        // score ranking is correct
        assertThat(comparator.compare("NR", "OK")).isGreaterThan(0)
    }

    @Test
    fun sortingTest() {
        val words = listOf("mom", "dad", "kid", "cat")
        val sorted = words.sortedWith(comparator)
        assertThat(sorted).isEqualTo(listOf("cat", "mom", "dad", "kid"))
    }
}