package ca.jonathanfritz.wordle

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlin.random.Random

class Wordle(
    private val wordList: List<String>,
    private val solution: String,
    private val numGuesses: Int = 5
) {

    /**
     * Attempts to solve the puzzle
     * @return the number of turns required to find a solution
     */
    fun run(comparator: Comparator<in String>, debugOutput: Boolean = false): Int {
        val correctLetters: MutableSet<Char> = HashSet()
        val presentLetters: MutableSet<Char> = HashSet()
        val absentLetters: MutableSet<Char> = HashSet()

        var dictionary = GraphDictionary(wordList)
        for (turn in 0 until numGuesses) {
            val remainingWords = dictionary.words(comparator)
            println("${remainingWords.size} words remain in the dictionary")
            val nextGuess = remainingWords.first()
            println("Guess ${turn + 1} is $nextGuess")

            val state = guess(nextGuess)
            if (state.all { it == Evaluation.CORRECT }) {
                println("The solution is $nextGuess\n")
                return turn + 1
            } else {
                val characterEvaluations = state.mapIndexed{ i, evaluation ->
                    CharacterEvaluation(nextGuess.toCharArray()[i], evaluation)
                }
                dictionary = dictionary.remove(characterEvaluations)

                if (debugOutput) {
                    // keep track of what we know so far
                    state.mapIndexed { i, evaluation ->
                        val letter = nextGuess[i]
                        when (evaluation) {
                            Evaluation.CORRECT -> {
                                correctLetters.add(letter)
                            }
                            Evaluation.PRESENT -> {
                                presentLetters.add(letter)
                            }
                            Evaluation.ABSENT -> {
                                absentLetters.add(letter)
                            }
                        }
                    }

                    println("Correct letters: $correctLetters")
                    println("Known present letters: $presentLetters")
                    println("Known absent letters: $absentLetters")
                    println("")
                }
            }
        }

        // the game only allows for a limited number of guesses
        println("Out of guesses! The correct solution was $solution\n")
        return numGuesses
    }

    private fun guess(guess: String): List<Evaluation> {
        if (guess.length != solution.length) {
            throw RuntimeException("Guess is the wrong length")
        } else {
            // as it turns out, the business logic for wordle is ridiculously simple
            return guess.mapIndexed{ i, char ->
                if (solution[i] == char) {
                    Evaluation.CORRECT
                } else if (solution.contains(char)) {
                    Evaluation.PRESENT
                } else {
                    Evaluation.ABSENT
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    println("Loading dictionary")
    val words = Utils.loadLinesFromFile("fiveletterwords.txt")
    println("Found ${words.size} words\n")

    val parser = ArgParser("Wordle")
    val random by parser.option(ArgType.Boolean, shortName = "r", description = "Solve a random puzzle")
    val solution by parser.option(ArgType.String, shortName = "s", description = "The solution of the puzzle to solve")
    val quantify by parser.option(ArgType.Boolean, shortName = "q", description = "Quantify skill of the algorithm")
    parser.parse(args)

    val comparator = MonogramComparator()
    if (random == true) {
        Wordle(words, words[Random.nextInt(words.size)]).run(comparator, true)
    } else if (solution != null && solution!!.isNotBlank()) {
        Wordle(words, solution!!).run(comparator, true)
    } else if (quantify == true) {
        val score = words.mapIndexed { i, answer ->
            println("$i/${words.size}: Answer is $answer")
            Wordle(words, answer, 100).run(comparator)
        }.average()
        println("On average, it took $score guesses to solve the puzzle")
    } else {
        println("No valid flags specified. Try --help for instructions")
    }
}