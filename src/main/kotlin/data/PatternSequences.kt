package data

import model.Drawing
import model.PatternType
import model.UniquePattern

/**
 * Track correlations between patterns from subsequent drawings.
 * The [sequenceSize] determines the size of the sequence.
 * It represents how many patterns will be chained together.
 * Track how ofter a particular sequence occurs.
 */
class PatternSequences(
    val drawings: List<Drawing>,
    val patternType: PatternType,
    val sequenceSize: Int
) {

    val patternSequences: Map<UniquePattern, Int>
        get() = _patternSequences
    private val _patternSequences = mutableMapOf<UniquePattern, Int>()

    init {
        setSequences()
    }

    private fun setSequences() {
        // Control to know when to exit the for loop.
        val control = sequenceSize - 1
        val patterns: List<IntArray> = when (patternType) {
            PatternType.GROUP -> drawings.map { it.groupPattern }
            PatternType.LOW_HIGH -> drawings.map { it.lowHighPattern }
            PatternType.ODD_EVEN -> drawings.map { it.oddEvenPattern }
        }
        patterns.forEachIndexed { drawingIndex, _ ->
            // We exit the for loop when there are not enough drawings to create a sequence.
            if (drawingIndex + control >= patterns.size) return

            // Create arrays to store patterns.
            val array = Array(sequenceSize) { intArrayOf() }

            // Populate the array of arrays with the patterns.
            for (i in 0 until sequenceSize) {
                val index = drawingIndex + i
                array[i] = patterns[index]
            }

            val combinedArray = array.reduce { acc, arr -> acc + arr }
            _patternSequences.merge(UniquePattern(combinedArray), 1, Int::plus)
        }
    }
}