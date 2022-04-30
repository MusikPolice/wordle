package ca.jonathanfritz.wordle

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MonogramComparatorTest {

    private val comparator = MonogramComparator()

    @Test
    fun scoringTest() {
        // the highest possible letter score
        assertThat(comparator.score("e")).isEqualTo(52)

        // duplicate letters are ignored
        assertThat(comparator.score("eeee")).isEqualTo(52)

        // known score check
        assertThat(comparator.score("cat")).isEqualTo(96)
        assertThat(comparator.score("mom")).isEqualTo(86)
        assertThat(comparator.score("kid")).isEqualTo(70)
        assertThat(comparator.score("dad")).isEqualTo(56)
    }

    @Test
    fun comparisonTest() {
        // equality test
        assertThat(comparator.compare("hello", "hello")).isEqualTo(0)

        // duplicate letters do not count towards score
        assertThat(comparator.compare("hello", "helloo")).isEqualTo(0)

        // j is scored lower than h
        assertThat(comparator.compare("jello", "hello")).isEqualTo(40)

        // operations are commutative
        assertThat(comparator.compare("hello", "jello")).isEqualTo(-40)

        // score ranking is correct
        assertThat(comparator.compare("a", "q")).isEqualTo(-46)
    }

    @Test
    fun sortingTest() {
        val words = listOf("mom", "dad", "kid", "cat")
        val sorted = words.sortedWith(comparator)
        assertThat(sorted).isEqualTo(listOf("cat", "mom", "kid", "dad"))
    }
}