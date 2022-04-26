package ca.jonathanfritz.wordle

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlin.random.Random

class Wordle(
    private val dictionary: List<String>,
    private val solution: String,
    private val numGuesses: Int = 5
) {

    /**
     * Attempts to solve the puzzle
     * @return the number of turns required to find a solution
     */
    fun run(): Int {
        // the letters that we know are present in the solution, in their known positions
        val correctLetters: MutableList<String?> = mutableListOf(*(solution.indices).map { null }.toTypedArray())

        // the letters that we know are present in the solution, even if we don't know their position just yet
        val presentLetters: MutableSet<String> = HashSet()

        // the letters that we know are not present in the solution
        val absentLetters: MutableSet<String> = HashSet()

        val pastGuesses: MutableSet<String> = HashSet()

        for (turn in 0 until numGuesses) {
            val nextGuess = if (turn == 0) {
                // on the first iteration, guess the most frequently used word
                // this could be optimized by considering letter frequencies, ngram frequencies, and eliminating duplicate characters
                dictionary[0]
            } else {
                // on subsequent iterations, regex the dictionary to come up with a good guess
                val regex = buildRegex(correctLetters, absentLetters)
                dictionary.filter { guess ->
                    // any guess must match our regex
                    regex.matchEntire(guess) != null
                }.filter { guess ->
                    // let's not repeat ourselves
                    !pastGuesses.contains(guess)
                }.first { guess ->
                    // and it must contain all the present letters that we've found
                    presentLetters.all { presentLetter -> guess.contains(presentLetter) }
                }
            }
            println("Guess ${turn + 1} is $nextGuess")

            val state = guess(nextGuess)
            pastGuesses.add(nextGuess)
            if (state.all { it == Evaluation.CORRECT }) {
                println("The solution is $nextGuess")
                return turn + 1
            } else {
                // keep track of what we know so far
                state.mapIndexed{ i, evaluation ->
                    val letter = nextGuess[i].toString()
                    when (evaluation) {
                        Evaluation.CORRECT -> correctLetters[i] = letter
                        Evaluation.PRESENT -> presentLetters.add(letter)
                        Evaluation.ABSENT -> absentLetters.add(letter)
                    }
                }

                println("Correct letters: $correctLetters")
                println("Known present letters: $presentLetters")
                println("Known absent letters: $absentLetters")
                println("")
            }
        }

        // the game only allows for a limited number of guesses
        println("Out of guesses! The correct solution was $solution")
        return numGuesses
    }

    private fun buildRegex(
        correctLetters: List<String?>,
        absentLetters: Set<String>
    ): Regex {
        // assemble a regex like [abc] that matches any letter that might exist at a position in the solution
        val missingCharClass = listOf("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t",
            "u","v","w","x","y","z").filter { !absentLetters.contains(it) }.joinToString("","[","]")

        // this is obviously not the optimal regex, but it works and I'm lazy
        val sb = StringBuilder()
        sb.append("^")
        correctLetters.map {
            when (it) {
                null -> missingCharClass
                else -> it
            }
        }.forEach { sb.append(it) }
        sb.append("$")
        return Regex(sb.toString())
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

enum class Evaluation {
    CORRECT,    // a correct letter in the correct place
    ABSENT,     // an incorrect letter
    PRESENT     // a correct letter in the wrong place
}

fun main(args: Array<String>) {
    println("Loading dictionary")
    val dictionary = Utils.loadLinesFromFile("fiveletterwords.txt")
    println("Found ${dictionary.size} words\n")

    val parser = ArgParser("Wordle")
    val random by parser.option(ArgType.Boolean, shortName = "r", description = "Solve a random puzzle")
    val solution by parser.option(ArgType.String, shortName = "s", description = "The solution of the puzzle to solve")
    val quantify by parser.option(ArgType.Boolean, shortName = "q", description = "Quantify skill of the algorithm")
    parser.parse(args)

    if (random == true) {
        Wordle(dictionary, dictionary[Random.nextInt(dictionary.size)]).run()
    } else if (solution != null && solution!!.isNotBlank()) {
        Wordle(dictionary, solution!!).run()
    } else if (quantify == true) {
        val score = dictionary.map { it ->
            Wordle(dictionary, it, 100).run()
        }.average()
        println("On average, it took $score guesses to solve the puzzle")
    } else {
        println("No valid flags specified. Try --help for instructions")
    }
}