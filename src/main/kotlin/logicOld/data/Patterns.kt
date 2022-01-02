package logicOld.data

import logicOld.extension.toStringDrawing
import logicOld.model.pattern.PatternArray
import logicOld.model.pattern.PatternNumber
import logicOld.util.Convert
import logicOld.util.Helper

/**
 * Holds information about how often a particular pattern occurs, such as:
 * - a single number
 * - all variations of color, odd even & low high patterns
 */
object Patterns {

    val numbers = mutableMapOf<String, PatternNumber>()
    val colors = mutableMapOf<String, PatternArray>()
    val oddEvens = mutableMapOf<String, PatternArray>()
    val lowHighs = mutableMapOf<String, PatternArray>()

    init {
        Drawings.checkDrawings()
        generate()
        calculateProbabilities()
        sort()
    }

    private fun generate() {
        Drawings.drawings.forEachIndexed { index, drawing ->
            generateNumberPatterns(index, drawing.numbers)
            generateArrayPattern(index, Convert.convertToColorPattern(drawing.numbers), colors)
            generateArrayPattern(index, Convert.convertToOddEvenPattern(drawing.numbers), oddEvens)
            generateArrayPattern(index, Convert.convertToHighLowPattern(drawing.numbers), lowHighs)
        }
    }

    /**
     * Numbers array size already checked in [impl.model.drawing.Drawing] init block.
     */
    private fun generateNumberPatterns(index: Int, numbers: IntArray) {
        numbers.forEach {
            val key = it.toString()
            if (this.numbers.containsKey(key)) {
                this.numbers[key]?.occurred(index)
            } else {
                this.numbers[key] = PatternNumber(it, index)
            }
        }
    }

    /**
     * Numbers array size already checked in [impl.model.drawing.Drawing] init block.
     */
    private fun generateArrayPattern(index: Int, numbers: IntArray, map: MutableMap<String, PatternArray>) {
        val key = numbers.toStringDrawing()
        if (map.containsKey(key)) {
            map[key]?.occurred(index)
        } else {
            map[key] = PatternArray(numbers, index)
        }
    }

    private fun calculateProbabilities() {
        numbers.forEach { (_, v) -> v.calcProbability() }
        colors.forEach { (_, v) -> v.calcProbability() }
        oddEvens.forEach { (_, v) -> v.calcProbability() }
        lowHighs.forEach { (_, v) -> v.calcProbability() }
    }

    private fun sort() {
        Helper.sortPatternMap(numbers)
        Helper.sortPatternMap(colors)
        Helper.sortPatternMap(oddEvens)
        Helper.sortPatternMap(lowHighs)
    }

    fun checkPatterns() {
        val numbersEmpty = numbers.isEmpty()
        val colorsEmpty = colors.isEmpty()
        val oddEvensEmpty = oddEvens.isEmpty()
        val lowHighsEmpty = lowHighs.isEmpty()
        if (numbersEmpty || colorsEmpty || oddEvensEmpty || lowHighsEmpty) {
            val msg = "Some Patterns are empty. " +
                    "\nNumbers: $numbersEmpty " +
                    "\nColors: $colorsEmpty " +
                    "\nOdd Evens: $oddEvensEmpty " +
                    "\nLow Highs: $lowHighsEmpty"
            throw IllegalArgumentException(msg)
        }
    }

}