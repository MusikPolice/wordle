package ca.jonathanfritz.wordle

// first guess from this comparator is "harem"
class MonogramComparator : Comparator<String> {

    private var monograms = Utils.loadMonogramWeights()
    private val comparatorCache: MutableMap<String, Int> = HashMap()
    private val scoreCache: MutableMap<String, Int> = HashMap()

    // sorts descending so that words with the highest score are at the top of the list
    override fun compare(s1: String?, s2: String?): Int {
        return comparatorCache.computeIfAbsent("$s1.$s2") {
            score(s2!!) - score(s1!!)
        }
    }

    // sums the score of each character in the string, ignoring duplicate characters
    fun score(s: String): Int {
        return scoreCache.computeIfAbsent(s) { string ->
            string.map { it }.distinct().sumOf { char -> monograms[char] ?: 0 }
        }
    }
}