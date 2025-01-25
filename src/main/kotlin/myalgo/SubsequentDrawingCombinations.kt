package myalgo

import myalgo.model.Drawing
import myalgo.model.UniquePattern

/**
 * Track correlations between numbers from subsequent drawings.
 * The [size] determines the size of the combination and how many numbers will be chained together.
 * Track how ofter a particular combination occurs.
 */
class SubsequentDrawingCombinations(
    private val drawings: List<Drawing>,
    private val size: Int
) {

    val combinationsByMedian: Map<UniquePattern, Int>
        get() = _combinationsByMedian
    private val _combinationsByMedian = mutableMapOf<UniquePattern, Int>()

    val combinationsByMean: Map<UniquePattern, Int>
        get() = _combinationsByMean
    private val _combinationsByMean = mutableMapOf<UniquePattern, Int>()

    private val combinations = mutableMapOf<UniquePattern, Int>()

    init {
        createCombinations()
        calculateMediansAndMeans()
    }

    private fun createCombinations() {
        // Extract all drawings.
        val drawings = this.drawings.map { it.numbers }
        // Control to know when to exit the for loop.
        val control = size - 1
        drawings.forEachIndexed { drawingIndex, _ ->
            // We exit the loop when there are not enough drawings to create a combination.
            if (drawingIndex + control >= drawings.size) {
                val sorted = combinations
                    .toList()
                    .sortedBy { (_, value) -> value }
                    .reversed()
                    .toMap()
                combinations.clear()
                combinations.putAll(sorted)
                return
            }

            // Create arrays to store drawings from which the numbers will be extracted.
            val array = Array(size) { intArrayOf() }

            // Populate the array of arrays with the drawings.
            for (i in 0 until size) {
                val index = drawingIndex + i
                array[i] = drawings[index]
            }

            // Generate and store the resulting sequences.
            generateCombinations(array).forEach { sequence ->
                combinations.merge(UniquePattern(sequence.toIntArray()), 1, Int::plus)
            }
        }
    }

    fun generateCombinations(
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
            generateCombinations(array, currentIndex + 1, currentSequence + number)
        }
    }

    private fun calculateMediansAndMeans() {
        combinations.keys
            .map { removeLastArrayPosition(it) }
            .toSet()
            .forEach { combination ->
                val filtered = combinations.filter {
                    removeLastArrayPosition(it.key) == combination
                }

                val sorted = filtered.values.sorted()
                val isSizeOdd = sorted.size % 2 != 0
                val middleIndex = sorted.size / 2
                val median = if (isSizeOdd) {
                    sorted[middleIndex].toDouble()
                } else {
                    sorted[middleIndex].plus(sorted[middleIndex - 1]).toDouble().div(2)
                }
                val mean = filtered.values.sum().toDouble().div(filtered.size)

                filtered.forEach { (seq, freq) ->
                    if (freq > median) {
                        _combinationsByMedian[seq] = freq
                    }
                    if (freq > mean) {
                        _combinationsByMean[seq] = freq
                    }
                }
            }
    }

    private fun removeLastArrayPosition(
        combination: UniquePattern
    ): UniquePattern = UniquePattern(combination.array.sliceArray(0 until combination.array.size - 1))
}