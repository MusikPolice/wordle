package ca.jonathanfritz.wordle

import org.junit.jupiter.api.Test
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

internal class WordleTest {

    private val allWords = Utils.loadLinesFromFile("fiveletterwords.txt")
    private val comparator = MonogramComparator()

    // Baseline          - avg: 536, p50: 502, p90: 590
    // words()           - avg: 538, p50: 529, p90: 570
    // score cache       - avg: 425, p50: 425, p90: 442
    // comparator cache  - avg: 487, p50: 475, p90: 511
    // inject comparator - avg: 462, p50: 456, p90: 476
    // linked list       - avg: 491, p50: 454, p90: 544
    @Test
    fun runtimeTest() {
        val groupSize = 10
        val runtimes: MutableList<Long> = ArrayList()
        for (i in 0..groupSize) {
            val offset = i*groupSize
            val words = allWords.shuffled().subList(offset, offset+groupSize)

            val startMs = System.currentTimeMillis()
            words.forEach {
                Wordle(allWords, it).run(comparator)
            }
            runtimes.add(System.currentTimeMillis() - startMs)
        }
        println("avg: ${(runtimes.average()/ groupSize.toFloat()).roundToInt()}," +
                " p50: ${(runtimes.sorted()[4]/ groupSize.toFloat()).roundToInt()}," +
                " p90: ${(runtimes.sorted()[8]/groupSize.toFloat()).roundToInt()}")
    }
}