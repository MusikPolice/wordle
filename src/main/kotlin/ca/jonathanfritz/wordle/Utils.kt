package ca.jonathanfritz.wordle

import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.exp

class Utils {
    companion object {

        private const val MAX_WEIGHT = 100
        private const val MIN_WEIGHT = 1

        /**
         * Computes an exponential decay on the specified value with the specified rate
         */
        fun decay(value: Int, rate: Float): Float = MAX_WEIGHT * exp(rate * -value)

        /**
         * Scales the specified value such that it falls within the range of 1..100
         */
        fun scale(value: Float, minValue: Float, maxValue: Float): Float =
            (((MAX_WEIGHT - MIN_WEIGHT)/(maxValue - minValue)) * (value - maxValue) + MAX_WEIGHT)

        fun loadLinesFromFile(filename: String): List<String> {
            val path = getPathToFile(filename)
            return Files.readAllLines(path)
        }

        private fun getPathToFile(filename:String): Path  =
            Path.of(Utils::class.java.classLoader.getResource(filename).toURI())
    }
}