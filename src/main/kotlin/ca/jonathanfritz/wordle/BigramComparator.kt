package ca.jonathanfritz.wordle

class BigramComparator: Comparator<String> {

    private var bigramWeights: Map<String, Int> = HashMap()
    private val comparatorCache: MutableMap<String, Int> = HashMap()
    private val scoreCache: MutableMap<String, Int> = HashMap()

    init {
        // bigrams file contains one bigram per line, sorted most common to least
        bigramWeights = Utils.loadLinesFromFile("englishbigrams.txt")
            .reversed()                 // reverse the list so that more common bigrams get a higher score
            .map { it.lowercase() }     // lower case everything so that equality checks work
            .mapIndexed {i, bigram ->
                bigram to (i + 1) * 2   // TH will get a score of 676*2 = 1352, while QZ will get a score of 1*2 = 2
            }
            .toMap()
    }

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