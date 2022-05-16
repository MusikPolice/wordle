package ca.jonathanfritz.wordle

/*
 * There's a bug in here somewhere
 * 12971/12972: Answer is zymic
 * 12972 words remain in the dictionary
 * Guess 1 is harem
 * 2160 words remain in the dictionary
 * Guess 2 is sting TODO: WHERE'S THE M?
 * 329 words remain in the dictionary
 * ...
 * The solution is zymic
 */
class GraphDictionary(
    words: List<String>,
    private val wordLength: Int = 5
) {
    private val charNodes: MutableSet<CharNode> = HashSet()
    private val posCharNodes: MutableSet<PosCharNode> = HashSet()

    init {
        // builds the graph of position -> char -> word
        words.distinct().forEach { word ->
            if (word.length != wordLength) {
                throw IllegalArgumentException("$word has length != $wordLength")
            }

            val wordNode = WordNode(word.lowercase())
            word.forEachIndexed { i, char ->
                // if a node for this character already exists, find it
                val charNode = charNodes.firstOrNull{ it.character == char} ?: CharNode(char)
                charNodes.add(charNode)

                // add character relationship
                wordNode.parents.add(charNode)
                charNode.children.add(wordNode)

                // if a node for this character at this position already exists, find it
                val posCharNode = posCharNodes.firstOrNull { it.position == i && it.character == char } ?: PosCharNode(i, char)
                posCharNodes.add(posCharNode)

                // add position character relationship
                wordNode.parents.add(posCharNode)
                posCharNode.children.add(wordNode)
            }
        }
    }

    fun remove(evaluations: List<CharacterEvaluation>): GraphDictionary {
        if (evaluations.size != wordLength) {
            throw IllegalArgumentException("Specified list of evaluations must have size equal to word length: One evaluation per character in the guessed word")
        }

        evaluations.forEachIndexed { i, charEval ->
            when (charEval.evaluation) {
                Evaluation.PRESENT -> {
                    // char is present somewhere but not here
                    removeCharAt(i, charEval.character)
                }
                Evaluation.ABSENT -> {
                    // char is not present in the solution
                    removeChar(charEval.character)
                }
                Evaluation.CORRECT -> {
                    // char must be present in this position
                    removeNotCharAt(i, charEval.character)
                }
            }
        }

        return this
    }

    // eliminate all words that have char at this position
    fun removeChar(char: Char) {
        for (charNode in charNodes) {
            if (charNode.character == char) {
                removeNode(charNode)
            }
        }
    }

    // eliminate all words that have char in any position
    fun removeCharAt(pos: Int, char: Char) {
        for (posCharNode in posCharNodes) {
            if (posCharNode.position == pos && posCharNode.character == char) {
                removeNode(posCharNode)
            }
        }
    }

    // eliminate all words that do not have char in this position
    fun removeNotCharAt(pos: Int, char: Char) {
        for (posCharNode in posCharNodes) {
            if (posCharNode.position == pos && posCharNode.character != char) {
                removeNode(posCharNode)
            }
        }
    }

    private fun removeNode(posCharNode: Node) {
        for (wordNode in posCharNode.children) {
            for (parent in wordNode.parents) {
                if (parent != posCharNode) {
                    parent.children.remove(wordNode)
                }
            }
        }
        posCharNode.children.clear()
    }

    // returns the unsorted list of remaining words
    fun words(): List<String> {
        val charWords = charNodes.flatMap { it.children }.map { it.word }.toSet()
        val posCharWords = posCharNodes.flatMap { it.children }.map { it.word }.toSet()
        return (charWords + posCharWords).distinct()
    }

    fun words(comparator: Comparator<in String>): List<String> {
        return words().sortedWith(comparator)
    }

    fun size(): Int = words().size

    private abstract class Node {
        open val children: MutableSet<WordNode> = mutableSetOf()
    }

    private class CharNode(
        val character: Char,
        override val children: MutableSet<WordNode> = mutableSetOf()
    ): Node() {
        // equality check ignores children
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as CharNode
            if (character != other.character) return false
            return true
        }

        // hash code ignores children
        override fun hashCode(): Int {
            return character.hashCode()
        }
    }

    private class PosCharNode(
        val position: Int,
        val character: Char,
        override val children: MutableSet<WordNode> = mutableSetOf()
    ): Node() {
        // equality check ignores children
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as PosCharNode
            if (position != other.position) return false
            if (character != other.character) return false
            return true
        }

        // hash code ignores children
        override fun hashCode(): Int {
            var result = position
            result = 31 * result + character.hashCode()
            return result
        }
    }

    private data class WordNode(
        val word: String,
        val parents: MutableSet<Node> = mutableSetOf()
    ) {
        // equality check ignores parents
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as WordNode
            if (word != other.word) return false
            return true
        }

        // hashCode ignores parents
        override fun hashCode(): Int {
            return word.hashCode()
        }
    }
}