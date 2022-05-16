package ca.jonathanfritz.wordle.comparators

// averages the scores of the specified comparators to produce a combined score
// TODO: allow weight of each comparator to be specified
class CombinedComparator(
    private vararg val comparators: WordleComparator
): WordleComparator {

    private val comparatorCache: MutableMap<String, Int> = HashMap()

    override fun compare(s1: String?, s2: String?): Int {
        return comparatorCache.computeIfAbsent("$s1.$s2") {
            score(s2!!) - score(s1!!)
        }
    }

    override fun name() = "CombinedComparator"

    override fun score(s: String): Int {
        return comparators.sumOf { it.score(s) } / comparators.size
    }
}