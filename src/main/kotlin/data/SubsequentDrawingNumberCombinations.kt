package data

import model.Drawing
import model.UniquePattern

/**
 * Track correlations between numbers from subsequent drawings.
 * The [sequenceSize] determines the size of the sequence.
 * It represents how many numbers will be chained together.
 * Track how ofter a particular sequence occurs.
 */
class SubsequentDrawingNumberCombinations(
    val sequenceSize: Int,
    private val drawings: List<Drawing>
) {

    val numberSequences: Map<UniquePattern, Int>
        get() = _numberSequences
    private val _numberSequences = mutableMapOf<UniquePattern, Int>()

    init {
        setSequences()
    }

    private fun setSequences() {
        // Extract all drawings.
        val drawings = this.drawings.map { it.numbers }
        // Control to know when to exit the for loop.
        val control = sequenceSize - 1
        drawings.forEachIndexed { drawingIndex, _ ->
            // We exit the for loop when there are not enough drawings to create a sequence.
            if (drawingIndex + control >= drawings.size) return

            // Create arrays to store drawings from which the numbers will be extracted.
            val array = Array(sequenceSize) { intArrayOf() }

            // Populate the array of arrays with the drawings.
            for (i in 0 until sequenceSize) {
                val index = drawingIndex + i
                array[i] = drawings[index]
            }

            // Generate and store the resulting sequences.
            generateSequences(array).forEach { sequence ->
                _numberSequences.merge(UniquePattern(sequence.toIntArray()), 1, Int::plus)
            }
        }
    }

    fun generateSequences(
        array: Array<IntArray>,
        currentIndex: Int = 0,
        currentSequence: List<Int> = listOf()
    ): List<List<Int>> {
        // Exit when a number was extracted from each array.
        // Return the extracted number sequence.
        if (currentIndex == array.size) {
            return listOf(currentSequence)
        }

        // Get the array from which the number should be extracted.
        val currentArray = array[currentIndex]
        // Extract a number from the current array
        return currentArray.flatMap { number ->
            // Call the method with the same array of arrays,
            // increment index by 1 in order to extract a number from the next sub-array
            // and add the extracted number to the sequence.
            generateSequences(array, currentIndex + 1, currentSequence + number)
        }
    }
}