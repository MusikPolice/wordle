package ca.jonathanfritz.wordle.comparators

import ca.jonathanfritz.wordle.Utils
import kotlin.math.roundToInt

// first guess from this comparator is "harem"
// TODO: caches can be removed if we switch to insertion sort
class MonogramComparator : WordleComparator {

    private var monogramWeights: Map<Char, Float> = HashMap()
    private val comparatorCache: MutableMap<String, Int> = HashMap()
    private val scoreCache: MutableMap<String, Int> = HashMap()

    private val monogramDecayRate = 0.25f

    init {
        // monograms file contains one character per line, sorted most common to least
        val decayed = Utils.loadLinesFromFile("englishmonograms.txt")
            .map {
                it[0].lowercaseChar()   // everything is lower cased so that equality checks work
            }
            .mapIndexed { i, char ->
                char to Utils.decay(i, monogramDecayRate)
            }
            .toMap()

        val maxVal = decayed.values.maxOrNull() ?: 0f
        val minVal = decayed.values.minOrNull() ?: 0f

        monogramWeights = decayed.map {
            val (char, f) = it
            char to Utils.scale(f, minVal, maxVal)
        }.toMap()
    }

    // sorts descending so that words with the highest score are at the top of the list
    override fun compare(s1: String?, s2: String?): Int {
        return comparatorCache.computeIfAbsent("$s1.$s2") {
            score(s2!!) - score(s1!!)
        }
    }

    override fun name() = "MonogramComparator"

    // sums the score of each character in the string, ignoring duplicate characters
    override fun score(s: String): Int {
        return scoreCache.computeIfAbsent(s) { string ->
            string.map { it }.distinct().sumOf { char -> monogramWeights[char]?.roundToInt() ?: 0 }
        }
    }
}