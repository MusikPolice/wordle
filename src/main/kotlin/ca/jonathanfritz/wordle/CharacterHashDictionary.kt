package ca.jonathanfritz.wordle

class CharacterHashDictionary(
    private val words: List<String>,
    private val wordLength: Int = 5
) {

    // list contains one element for each character position
    // each element contains a map keyed on character, where the map value is the set of words that have the key
    // character in the list position
    private val dict: MutableList<MutableMap<Char, MutableSet<String>>> = ArrayList(wordLength)

    init {
        words.forEach{ word ->
            word.forEachIndexed{ i, char ->
                if (dict.size < i + 1) {
                    dict.add(HashMap())
                }
                if (!dict[i].containsKey(char)) {
                    dict[i][char] = HashSet()
                }
                dict[i][char]?.add(word)
            }
        }
    }

    fun remove(evaluations: List<CharacterEvaluation>): CharacterHashDictionary {
        if (evaluations.size != wordLength) {
            throw IllegalArgumentException("Specified list of evaluations must have size equal to word length: One evaluation per character in the guessed word")
        }

        val wordsToRemove = evaluations.flatMapIndexed { i, charEval ->
            when (charEval.evaluation) {
                Evaluation.PRESENT -> {
                    // char is present somewhere but not here
                    // eliminate all words that have char at this position
                    dict[i][charEval.character] ?: emptyList()
                }
                Evaluation.ABSENT -> {
                    // char is not present in the solution
                    // eliminate all words that have char in any position
                    dict.flatMap { characterMap ->
                        characterMap[charEval.character]?.toList() ?: emptyList()
                    }.distinct()
                }
                Evaluation.CORRECT -> {
                    // char must be present in this position
                    // eliminate all words that do not have char in this position
                    (words - (dict[i][charEval.character] ?: emptyList()))
                }
            }
        }.distinct()
        println("Identified ${wordsToRemove.size} invalid words")

        // TODO: Re-indexing the data structure is really slow for big word lists - consider mutating in place?
        return CharacterHashDictionary(words - wordsToRemove, wordLength)
    }

    // returns the number of remaining words
    fun size():Int = words().size

    // returns the remaining words in no particular order
    fun words(): List<String> = dict.flatMap { map -> map.flatMap { entry -> entry.value } }.distinct()

    // returns the remaining words sorted with the specified comparator
    fun words(comparator: Comparator<in String>): List<String> {
        return words().sortedWith(comparator)
    }
}