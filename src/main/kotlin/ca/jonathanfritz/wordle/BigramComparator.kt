package ca.jonathanfritz.wordle

class BigramComparator: Comparator<String> {

    private var bigramWeights = Utils.loadBigramWeights()
    private val comparatorCache: MutableMap<String, Int> = HashMap()
    private val scoreCache: MutableMap<String, Int> = HashMap()

    // sorts descending so that words with the highest score are at the top of the list
    override fun compare(s1: String?, s2: String?): Int {
        return comparatorCache.computeIfAbsent("$s1.$s2") {
            score(s2!!) - score(s1!!)
        }
    }

    fun score(s: String): Int {
        return scoreCache.computeIfAbsent(s.lowercase()) { string ->
            // TODO: compress this into a single op once debugging is done
            val bigrams = if (string.length == 2) {
                listOf(string)
            } else {
                (0 .. (string.length - 2)).map {
                    string.substring(it, it+2)
                }
            }
            val scores = bigrams
                .distinct()
                .map {
                    bigramWeights[it] ?: 0
                }
            return@computeIfAbsent scores.sum()
        }
    }
}