package algorithm

import extensions.addPattern
import extensions.calculatePatternProbabilities
import extensions.sortFrequenciesByProbability
import extensions.sortPatternsByProbability
import model.SubPattern
import utils.convertDrawingIntArrayToColorPatternArray
import utils.convertDrawingIntArrayToLowHighPatternArray
import utils.convertDrawingIntArrayToOddEvenPatternArray
import utils.drawingsList

object Patterns {

    var numberPatterns = mutableMapOf<String, SubPattern>()
    var colorPatterns = mutableMapOf<String, SubPattern>()
    var lowHighPatterns = mutableMapOf<String, SubPattern>()
    var oddEvenPatterns = mutableMapOf<String, SubPattern>()

    fun generatePatterns() {
        drawingsList.forEachIndexed { drawingIndex, drawing ->
            drawing.numbers.forEachIndexed { _, number ->
                // Fill the number patterns map
                numberPatterns.addPattern(number, drawingIndex)
            }

            // Fill the color patterns map
            colorPatterns.addPattern(convertDrawingIntArrayToColorPatternArray(drawing.numbers), drawingIndex)
            // Fill the odd-even patterns map
            lowHighPatterns.addPattern(convertDrawingIntArrayToLowHighPatternArray(drawing.numbers), drawingIndex)
            // Fill the high-low patterns map
            oddEvenPatterns.addPattern(convertDrawingIntArrayToOddEvenPatternArray(drawing.numbers), drawingIndex)
        }
    }

    fun calculateProbabilities() {
        val totalDrawingsCount = drawingsList.count()
        // Calculate probabilities of the number patterns
        numberPatterns.calculatePatternProbabilities(totalDrawingsCount)
        // Calculate probabilities of the color patterns
        colorPatterns.calculatePatternProbabilities(totalDrawingsCount)
        // Calculate probabilities of the odd-even patterns
        lowHighPatterns.calculatePatternProbabilities(totalDrawingsCount)
        //Calculate probabilities of the high-low patterns
        oddEvenPatterns.calculatePatternProbabilities(totalDrawingsCount)
    }

    fun sortPatterns() {
        // Sort pattern maps
        numberPatterns = numberPatterns.sortPatternsByProbability()
        colorPatterns = colorPatterns.sortPatternsByProbability()
        lowHighPatterns = lowHighPatterns.sortPatternsByProbability()
        oddEvenPatterns = oddEvenPatterns.sortPatternsByProbability()

        // Sort patterns' frequency maps
        numberPatterns.sortFrequenciesByProbability()
        colorPatterns.sortFrequenciesByProbability()
        lowHighPatterns.sortFrequenciesByProbability()
        oddEvenPatterns.sortFrequenciesByProbability()
    }

}