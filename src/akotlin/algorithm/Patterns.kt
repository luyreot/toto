package akotlin.algorithm

import akotlin.model.ArrayPattern
import akotlin.model.NumberPattern
import akotlin.utils.*

object Patterns {

    lateinit var numberPatterns: Map<Int, NumberPattern>
    lateinit var colorPatterns: Map<String, ArrayPattern>
    lateinit var lowHighPatterns: Map<String, ArrayPattern>
    lateinit var oddEvenPatterns: Map<String, ArrayPattern>

    fun generatePatterns() {
        val numbers = HashMap<Int, NumberPattern>()
        val colors = HashMap<String, ArrayPattern>()
        val lowHighs = HashMap<String, ArrayPattern>()
        val oddEvens = HashMap<String, ArrayPattern>()

        drawingsList.forEachIndexed { drawingIndex, drawing ->
            drawing.numbers.forEachIndexed { _, number ->
                // Fill the number patterns map
                processNumberPattern(numbers, number, drawingIndex)
            }

            // Fill the color patterns map
            processArrayPattern(colors, convertDrawingArrayToColorPatternArray(drawing.numbers), drawingIndex)
            // Fill the odd-even patterns map
            processArrayPattern(lowHighs, convertDrawingArrayToLowHighPatternArray(drawing.numbers), drawingIndex)
            // Fill the high-low patterns map
            processArrayPattern(oddEvens, convertDrawingArrayToOddEvenPatternArray(drawing.numbers), drawingIndex)
        }

        // Calculate probabilities of the number patterns
        calculateNumberPatternProbabilities(numbers, totalDrawingsCount)
        // Calculate probabilities of the color patterns
        calculateArrayPatternProbabilities(colors, totalDrawingsCount)
        // Calculate probabilities of the odd-even patterns
        calculateArrayPatternProbabilities(lowHighs, totalDrawingsCount)
        //Calculate probabilities of the high-low patterns
        calculateArrayPatternProbabilities(oddEvens, totalDrawingsCount)

        // Sort pattern maps
        numberPatterns = numbers.toList().sortedBy { (_, value) -> value }.toMap()
        colorPatterns = colors.toList().sortedBy { (_, value) -> value }.toMap()
        lowHighPatterns = lowHighs.toList().sortedBy { (_, value) -> value }.toMap()
        oddEvenPatterns = oddEvens.toList().sortedBy { (_, value) -> value }.toMap()

        // Sort patterns' frequency maps
        numberPatterns.forEach { (_, pattern) -> pattern.sortFrequencies() }
        colorPatterns.forEach { (_, pattern) -> pattern.sortFrequencies() }
        lowHighPatterns.forEach { (_, pattern) -> pattern.sortFrequencies() }
        oddEvenPatterns.forEach { (_, pattern) -> pattern.sortFrequencies() }
    }

    private fun processNumberPattern(map: HashMap<Int, NumberPattern>, number: Int, drawingIndex: Int) {
        if (map.containsKey(number)) {
            map[number]!!.incrementTimesOccurred()
            map[number]!!.addFrequency(drawingIndex)
        } else {
            map[number] = NumberPattern(number, drawingIndex)
        }
    }

    private fun calculateNumberPatternProbabilities(map: HashMap<Int, NumberPattern>, totalDrawingsCount: Int) = map.forEach { (number, numberPattern) ->
        numberPattern.calculateProbability(totalDrawingsCount)
        val frequenciesTotalCount = numberPattern.frequencies.values.stream()
                .map { it.timesOccurred }
                .reduce(0, Int::plus)

        if (numberPattern.timesOccurred == frequenciesTotalCount + 1) {
            numberPattern.frequencies.forEach { (_, frequencyPattern) ->
                frequencyPattern.calculateProbability(frequenciesTotalCount)
            }
        }
    }

    private fun processArrayPattern(map: HashMap<String, ArrayPattern>, mapValue: IntArray, drawingIndex: Int) {
        val mapKey = convertIntArrayToString(mapValue)
        if (map.containsKey(mapKey)) {
            map[mapKey]!!.incrementTimesOccurred()
            map[mapKey]!!.addFrequency(drawingIndex)
        } else {
            map[mapKey] = ArrayPattern(mapValue, drawingIndex)
        }
    }

    private fun calculateArrayPatternProbabilities(map: HashMap<String, ArrayPattern>, totalDrawingsCount: Int) = map.forEach { (key, pattern) ->
        pattern.calculateProbability(totalDrawingsCount)
        val frequenciesTotalCount = pattern.frequencies.values.stream()
                .map { it.timesOccurred }
                .reduce(0, Int::plus)
        // The frequency count represents how many spaces are between each timeOccurred
        if (pattern.timesOccurred == frequenciesTotalCount + 1) {
            pattern.frequencies.forEach { (_, frequencyPattern) ->
                frequencyPattern.calculateProbability(frequenciesTotalCount)
            }
        }
    }

}