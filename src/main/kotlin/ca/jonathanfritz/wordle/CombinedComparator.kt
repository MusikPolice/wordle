package ca.jonathanfritz.wordle

import kotlin.math.ceil

// averages the monogram score and bigram score to produce a combined score
class CombinedComparator: Comparator<String>  {

    private var monograms = Utils.loadMonogramWeights()
    private val bigramWeights = Utils.loadBigramWeights()
    private val comparatorCache: MutableMap<String, Int> = HashMap()
    private val monogramScoreCache: MutableMap<String, Int> = HashMap()
    private val bigramScoreCache: MutableMap<String, Int> = HashMap()

    override fun compare(s1: String?, s2: String?): Int {
        return comparatorCache.computeIfAbsent("$s1.$s2") {
            score(s2!!) - score(s1!!)
        }
    }

    fun score(s: String): Int {
        val monogramScore = monogramScore(s)
        val bigramScore = bigramScore(s)
        return (monogramScore + bigramScore) / 2
    }

    private fun monogramScore(s: String) = monogramScoreCache.computeIfAbsent(s) { string ->
        val chars = string.map { it }.distinct()
        val score = chars.sumOf { char -> monograms[char] ?: 0 }
        val maxScore = chars.size * 52f
        ceil(score / maxScore * 100).toInt()
    }

    private fun bigramScore(s: String) = bigramScoreCache.computeIfAbsent(s.lowercase()) { string ->
        val bigrams = if (string.length == 2) {
            listOf(string)
        } else {
            (0..(string.length - 2)).map {
                string.substring(it, it + 2)
            }
        }.distinct()

        val scores = bigrams
            .map {
                bigramWeights[it] ?: 0
            }
        val max: Float = bigrams.size * 1352f
        return@computeIfAbsent ceil((scores.sum() / max) * 100).toInt()
    }
}