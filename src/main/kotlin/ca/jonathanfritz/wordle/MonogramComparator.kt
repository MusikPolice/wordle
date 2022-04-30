package ca.jonathanfritz.wordle

class MonogramComparator : Comparator<String> {

    private var monograms: Map<Char, Int> = HashMap()

    init {
        // monograms file contains one character per line, sorted most common to least
        monograms = Utils.loadLinesFromFile("englishmonograms.txt")
            .reversed()                 // reverse the list so that more common characters get a higher score
            .map {
                it[0].lowercaseChar()   // everything is lower cased so that equality checks work
            }
            .mapIndexed { i, char ->
                char to (i + 1) * 2     // E will get a score of 26*2 = 52, while Q will get a score of 1*2 = 2
            }
            .toMap()
    }

    // sorts descending so that words with the highest score are at the top of the list
    override fun compare(s1: String?, s2: String?) = score(s2) - score(s1)

    // sums the score of each character in the string, ignoring duplicate characters
    fun score(s: String?) = s?.map { it }?.distinct()?.sumOf { char -> monograms[char] ?: 0 } ?: 0
}