package myalgo

import myalgo.model.Drawing
import myalgo.model.UniquePattern

/**
 * Track correlations between numbers from the same drawings.
 * The [size] determines the size of the combination and how many numbers will be chained together.
 * Track how ofter a particular combination occurs.
 */
class SameDrawingCombinations(
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
        drawings.forEach { drawing ->
            // Generate and store the resulting sequences.
            generateCombinations(drawing).forEach { sequence ->
                combinations.merge(UniquePattern(sequence.toIntArray()), 1, Int::plus)
            }
        }
        val sorted = combinations
            .toList()
            .sortedBy { (_, value) -> value }
            .reversed()
            .toMap()
        combinations.clear()
        combinations.putAll(sorted)
    }

    /**
     * The method uses a helper function `generate` which is called recursively to create all possible combinations.
     * When the size of the current combination matches the desired size, it is added to the combinations list.
     * Otherwise, it continues to explore the array, including or excluding each element to generate all possible combinations.
     */
    private fun generateCombinations(drawing: IntArray): List<List<Int>> {
        // List to store all the generated combinations.
        val combinations = mutableListOf<List<Int>>()

        // Recursive helper function to generate combinations.
        fun generate(currentIndex: Int, currentCombination: List<Int>) {
            // If the current combination size matches the desired size, add it to the list of combinations.
            if (currentCombination.size == size) {
                combinations.add(currentCombination)
                return
            }

            // Loop through the array starting from the current index.
            for (i in currentIndex until drawing.size) {
                // Recursively call the generate function with the next index and the updated combination.
                generate(i + 1, currentCombination + drawing[i])
            }
        }

        // Call the generate function to start generating combinations with an empty initial combination.
        generate(0, emptyList())

        // Return the list of all generated combinations.
        return combinations
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