package data

import model.Drawing
import model.UniquePattern

/**
 * Track correlations between numbers from the same drawings.
 * The [combinationSize] determines the size of the sequence.
 * It represents how many numbers will be chained together.
 * Track how ofter a particular sequence occurs.
 */
class SameDrawingNumberCombinations(
    private val combinationSize: Int,
    private val drawings: List<Drawing>
) {

    val numberCombinations: Map<UniquePattern, Int>
        get() = _numberCombinations
    private val _numberCombinations = mutableMapOf<UniquePattern, Int>()

    init {
        this.setCombinations()
    }

    private fun setCombinations() {
        // Extract all drawings.
        val drawings = this.drawings.map { it.numbers }
        drawings.forEach { drawing ->
            // Generate and store the resulting sequences.
            generateCombinations(drawing).forEach { sequence ->
                _numberCombinations.merge(UniquePattern(sequence.toIntArray()), 1, Int::plus)
            }
        }
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
            if (currentCombination.size == combinationSize) {
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
}