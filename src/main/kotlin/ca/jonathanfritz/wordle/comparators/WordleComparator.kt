package ca.jonathanfritz.wordle.comparators

interface WordleComparator: Comparator<String> {

    fun name(): String

    fun score(s: String): Int

}