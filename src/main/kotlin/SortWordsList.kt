import java.nio.file.Files
import kotlin.streams.toList

/**
 * Utility function for sorting the list of five letter words by ngram frequency so that the words that contain the
 * most frequently used English language monograms and duograms are ranked towards the top
 */
fun main() {
    val inputPath = Utils.getPathToFile("fiveletterwords.txt")
    val outputPath = Utils.getPathToFile("fiveletterwordssorted.txt")

    val monogramScores = Utils.loadLinesFromFile("englishmonograms.txt")
        .mapIndexed{ i, monogram ->
            monogram to i * 1000
        }
        .toMap()
    val bigramScores = Utils.loadLinesFromFile("englishbigrams.txt")
        .mapIndexed{ i, bigram ->
            bigram to i
        }
        .toMap()

    val dictionary = Files.newBufferedReader(inputPath)
        .lines()
        .map { it!! }
        .toList()

    val monograms = dictionary.map { word ->
        // monograms
        val score = word.toCharArray()
            .map { it.toString() }
            .sumOf { monogramScores[it] ?: 26000 }
        word!! to score
    }.toMap()

    val bigrams = dictionary.map { word ->
        // bigrams
        val score = (word.indices).map {
            word.substring(it, it + 1)
        }.sumOf { bigram ->
            bigramScores[bigram] ?: 0
        }
        word!! to score
    }.toMap()

    val scores = dictionary.map { word ->
        word!! to (monograms[word] ?: 0) + (bigrams[word] ?: 0)
    }.toList().sortedBy { p1, p2 ->
        // sort by score ascending
        p1.second.compareTo(p2.second)
    }

    Files.write(outputPath,
        dictionary
            .map { word ->
                // monograms
                val score = word.toCharArray()
                    .map { it.toString() }
                    .sumOf { monogramScores[it] ?: 26000 }
                word to score
            }
            .map { pair ->
                // bigrams
                val word = pair.first
                val score = (word.indices).map {
                    word.substring(it, it + 1)
                }.sumOf { bigram ->
                    bigramScores[bigram] ?: 0
                }
                word to pair.second + score
            }
            .sorted { p1, p2 ->
                // sort by score ascending
                p1.second.compareTo(p2.second)
            }
            .map {
                // discard the score
                it.first
            }
            .toList()
    )
}