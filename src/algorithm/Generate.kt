package algorithm

import extensions.toDrawingString
import service.allPossibleColorPatterns
import service.colorPatterns
import utils.calculateColorPatternCodeScores

object Generate {

    fun getAllPossibleColorPatterns() {
        generateIntArrayPatterns(allPossibleColorPatterns, 4, IntArray(6), 0)
    }

    private fun generateIntArrayPatterns(patterns: MutableSet<String>, end: Int, array: IntArray, index: Int) {
        for (x in 0..end) {
            if (index > 0 && x < array[index - 1]) continue
            array[index] = x
            if (index == array.size - 1) {
                patterns.add(array.sortedArray().toDrawingString())
                if (x == end) return
            } else {
                generateIntArrayPatterns(patterns, end, array, index + 1)
            }
        }
    }

    fun findMissingColorPatterns() {
        val missingPatterns = service.allPossibleColorPatterns - colorPatterns.keys
        if (missingPatterns.isEmpty()) return

        // Note that not every position (1-6) has every color code (0-4)
        val scores = calculateColorPatternCodeScores()
        // TODO calculate score for missing color patterns
    }

}