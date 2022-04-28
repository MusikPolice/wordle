package ca.jonathanfritz.wordle

enum class Evaluation {
    CORRECT,    // a correct letter in the correct place
    ABSENT,     // an incorrect letter
    PRESENT     // a correct letter in the wrong place
}