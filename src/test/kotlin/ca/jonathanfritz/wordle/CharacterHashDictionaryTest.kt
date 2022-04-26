package ca.jonathanfritz.wordle

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CharacterHashDictionaryTest {

    @Test
    fun constructorTest() {
        val expectedWords = listOf("fever", "focus", "slick", "probe", "audio")
        val dict = CharacterHashDictionary(expectedWords)
        assertThat(dict.size()).isEqualTo(5)
        assertThat(dict.words()).containsExactlyInAnyOrder(*expectedWords.toTypedArray())
    }

    @Test
    fun removeTest() {
        val words = listOf("fever", "focus", "slick", "probe", "audio")
        val dict = CharacterHashDictionary(words)

        // removing all instances of 'i' should drop "slick" and "audio"
        var updated = dict.remove('i')
        assertThat(updated.size()).isEqualTo(3)
        assertThat(updated.words()).containsExactlyInAnyOrder("fever", "focus","probe")

        // removing instances of 'i' at position 2 should only drop "slick"
        updated = dict.remove('i', 2)
        assertThat(updated.size()).isEqualTo(4)
        assertThat(updated.words()).containsExactlyInAnyOrder("fever", "focus","probe", "audio")

        // removing instances of 'f' at position 3 should not drop any words
        updated = dict.remove('f', 3)
        assertThat(updated.size()).isEqualTo(5)
        assertThat(updated.words()).containsExactlyInAnyOrder(*words.toTypedArray())
    }
}