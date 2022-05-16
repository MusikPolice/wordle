package ca.jonathanfritz.wordle.comparators

import ca.jonathanfritz.wordle.Utils
import kotlin.math.roundToInt

// TODO: caches can be removed if we switch to insertion sort
class BigramComparator: WordleComparator {

    private var bigramWeights: Map<String, Float> = HashMap()
    private val comparatorCache: MutableMap<String, Int> = HashMap()
    private val scoreCache: MutableMap<String, Int> = HashMap()

    private val bigramDecayRate = 0.005f

    init {
        // bigrams file contains one bigram per line, sorted most common to least
        val decayed = Utils.loadLinesFromFile("englishbigrams.txt")
            .map { it.lowercase() }     // lower case everything so that equality checks work
            .mapIndexed {i, bigram ->
                bigram to Utils.decay(i, bigramDecayRate)
            }
            .toMap()

        val maxVal = decayed.values.maxOrNull() ?: 0f
        val minVal = decayed.values.minOrNull() ?: 0f

        bigramWeights = decayed.map {
            val (str, f) = it
            str to Utils.scale(f, minVal, maxVal)
        }.toMap()
    }

    // sorts descending so that words with the highest score are at the top of the list
    override fun compare(s1: String?, s2: String?): Int {
        return comparatorCache.computeIfAbsent("$s1.$s2") {
            score(s2!!) - score(s1!!)
        }
    }

    override fun name() = "BigramComparator"

    override fun score(s: String): Int {
        return scoreCache.computeIfAbsent(s.lowercase()) { string ->
            if (string.length == 2) {
                listOf(string)
            } else {
                (0..(string.length - 2)).map {
                    string.substring(it, it + 2)
                }
            }.distinct().sumOf {
                bigramWeights[it]?.roundToInt() ?: 0
            }
        }
    }
}