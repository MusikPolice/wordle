package ca.jonathanfritz.wordle

import ca.jonathanfritz.wordle.comparators.MonogramComparator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MonogramComparatorTest {

    private val comparator = MonogramComparator()

    @Test
    fun scoringTest() {
        // the highest possible letter score
        assertThat(comparator.score("e")).isEqualTo(100)

        // duplicate letters are ignored
        assertThat(comparator.score("eeee")).isEqualTo(100)

        // the lowest possible letter score
        assertThat(comparator.score("q")).isEqualTo(1)

        // known score check
        assertThat(comparator.score("mom")).isEqualTo(92)
        assertThat(comparator.score("cat")).isEqualTo(72)
        assertThat(comparator.score("dad")).isEqualTo(62)
        assertThat(comparator.score("kid")).isEqualTo(27)
    }

    @Test
    fun comparisonTest() {
        // equality test
        assertThat(comparator.compare("hello", "hello")).isEqualTo(0)

        // duplicate letters do not count towards score
        assertThat(comparator.compare("hello", "helloo")).isEqualTo(0)

        // j is scored lower than h
        assertThat(comparator.compare("jello", "hello")).isEqualTo(47)

        // operations are commutative
        assertThat(comparator.compare("hello", "jello")).isEqualTo(-47)

        // score ranking is correct
        assertThat(comparator.compare("e", "q")).isEqualTo(-99)
    }

    @Test
    fun sortingTest() {
        val words = listOf("mom", "dad", "kid", "cat")
        val sorted = words.sortedWith(comparator)
        assertThat(sorted).isEqualTo(listOf("mom", "cat", "dad", "kid"))
    }
}