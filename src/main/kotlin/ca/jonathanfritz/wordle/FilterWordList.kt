package ca.jonathanfritz.wordle

import java.nio.file.Files
import java.nio.file.Path

/**
 * Utility function for pruning the incoming dictionary from kaggle down to only 5 letter words
 * arg0 - the path to the input file. Expected format is csv, with each line containing word,count
 * arg1 - the path to the output file
 */
fun main(args: Array<String>) {
    val inputPath = args[0]
    val outputPath = args[1]
    Files.write(Path.of(outputPath),
        Files.newBufferedReader(Path.of(inputPath))
            .lines()
            .dropWhile { it == "word,count" } // drop the first line
            .map { it.split(",")[0] } // only keep the word - the count doesn't matter for our purposes
            .filter { it.length == 5 } // only keep five letter words
            .toList()
    )
}