package ca.jonathanfritz.wordle

import ca.jonathanfritz.wordle.comparators.MonogramComparator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GraphDictionaryTest {

    private val comparator = MonogramComparator()

    @Test
    fun removeCharTest() {
        val dict = GraphDictionary(
            listOf("toast", "zupas", "leges", "model", "bocca", "ekkas", "newed", "guqin", "sunup", "dholl"),
            comparator
        )

        // four words containing the letter 'a' will be removed
        dict.removeChar('a')

        // the only remaining words are those that do not contain the letter 'a'
        // all words have been converted to lowercase
        val expected = listOf("leges", "model", "newed", "guqin", "sunup", "dholl")
        assertThat(dict.words()).containsExactlyInAnyOrder(*expected.toTypedArray())

        // none of the remaining words contain the letter 'z', so none of them will be removed
        dict.removeChar('z')
        assertThat(dict.words()).containsExactlyInAnyOrder(*expected.toTypedArray())
    }

    @Test
    fun removeCharAtTest() {
        val dict = GraphDictionary(
            listOf("toast", "zupas", "leges", "model", "bocca", "ekkas", "newed", "guqin", "sunup", "dholl"),
            comparator
        )

        // three words containing the letter 'o' in position 1 will be removed
        dict.removeCharAt(1, 'o')

        // the only remaining words are those that do not contain the letter 'o' at position 1
        // all words have been converted to lowercase
        val expected = listOf("zupas", "leges", "ekkas", "newed", "guqin", "sunup", "dholl")
        assertThat(dict.words()).containsExactlyInAnyOrder(*expected.toTypedArray())

        // none of the remaining words contain a 'p' in position 3, so none will be removed
        dict.removeCharAt(3, 'p')
        assertThat(dict.words()).containsExactlyInAnyOrder(*expected.toTypedArray())
    }

    @Test
    fun removeNotCharAtTest() {
        val dict = GraphDictionary(
            listOf("token", "zupas", "leges", "model", "robes", "ekkas", "newed", "guqin", "sunup", "dholl"),
            comparator
        )

        // seven words that do not have an 'o' in position 1 will be removed
        dict.removeNotCharAt(1, 'o')

        // the only remaining words are those that contain the letter 'o' at position 1
        val expected = listOf("token", "model", "robes")
        assertThat(dict.words()).containsExactlyInAnyOrder(*expected.toTypedArray())

        // all remaining words contain the letter 'e' at position 3, so none will be removed
        dict.removeNotCharAt(3, 'e')
        assertThat(dict.words()).containsExactlyInAnyOrder(*expected.toTypedArray())
    }

    @Test
    fun removeTest() {
        val words = listOf("fever", "cover", "slick", "probe", "audio")
        val dict = GraphDictionary(words, comparator)

        // simulate a guess of "femur", answer is "cover"
        // this should eliminate "fever", "slick", "audio", and "probe"; leaving only "cover" as a possibility
        val updated = dict.remove(listOf(
            CharacterEvaluation('f', Evaluation.ABSENT),    // removes "fever"
            CharacterEvaluation('e', Evaluation.PRESENT),   // removes "slick" and "audio"
            CharacterEvaluation('m', Evaluation.ABSENT),    // no-op
            CharacterEvaluation('u', Evaluation.ABSENT),    // removes "audio"
            CharacterEvaluation('r', Evaluation.CORRECT)    // removes "probe"
        ))
        assertThat(updated.size()).isEqualTo(1)
        assertThat(updated.words()).containsExactly("cover")
    }
}