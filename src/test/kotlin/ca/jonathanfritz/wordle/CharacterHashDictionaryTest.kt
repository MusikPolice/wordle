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
        // initial dictionary of words is immutable
        val words = listOf("fever", "cover", "slick", "probe", "audio")
        val dict = CharacterHashDictionary(words)

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