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

    fun remove(character: Char, vararg positions: Int = IntArray(wordLength) { i -> i }): CharacterHashDictionary {
        val wordsToRemove = positions.map { characterPosition -> dict[characterPosition] }
            .flatMap { characterMap -> characterMap[character] ?: emptyList() }
            .distinct()

        return CharacterHashDictionary(words - wordsToRemove, wordLength)
    }

    // returns the number of remaining words
    fun size():Int = words().size

    // returns the remaining words in no particular order
    fun words(): List<String> = dict.flatMap { map -> map.flatMap { entry -> entry.value } }.distinct()
    
    // TODO: overload of words() that accepts a function that affects sort order of returned list
}