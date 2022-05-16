package ca.jonathanfritz.wordle

import java.nio.file.Files
import java.nio.file.Path

class Utils {
    companion object {

        fun loadMonogramWeights(): Map<Char, Int> {
            // monograms file contains one character per line, sorted most common to least
            return loadLinesFromFile("englishmonograms.txt")
                .reversed()                 // reverse the list so that more common characters get a higher score
                .map {
                    it[0].lowercaseChar()   // everything is lower cased so that equality checks work
                }
                .mapIndexed { i, char ->
                    char to (i + 1) * 2     // E will get a score of 26*2 = 52, while Q will get a score of 1*2 = 2
                }
                .toMap()
        }

        fun loadBigramWeights(): Map<String, Int> {
            // bigrams file contains one bigram per line, sorted most common to least
            return loadLinesFromFile("englishbigrams.txt")
                .reversed()                 // reverse the list so that more common bigrams get a higher score
                .map { it.lowercase() }     // lower case everything so that equality checks work
                .mapIndexed {i, bigram ->
                    bigram to (i + 1) * 2   // TH will get a score of 676*2 = 1352, while QZ will get a score of 1*2 = 2
                }
                .toMap()
        }

        fun loadLinesFromFile(filename: String): List<String> {
            val path = getPathToFile(filename)
            return Files.readAllLines(path)
        }

        private fun getPathToFile(filename:String): Path  =
            Path.of(Utils::class.java.classLoader.getResource(filename).toURI())
    }
}