package data

import model.UniquePattern
import kotlin.random.Random

class PredictDrawing(
    val drawings: Drawings,
    val numbers: Numbers,
    val groupPatterns: GroupPatterns,
    val lowHighPatterns: LowHighPatterns,
    val oddEvenPatterns: OddEvenPatterns,
    val predictNumberSequences: PredictNumberSequences,
    val numberCombinations: NumberCombinations,
    val numberToPatternCorrelations: NumberToPatternCorrelations,
    val groupPatternToPatternCorrelations: GroupPatternToPatternCorrelations
) {

    init {
        predict()
    }

    private fun predict() {
        val numbersToUse = if (predictNumberSequences.predictionNumbers.size == drawings.totoType.totalNumbers) {
            println("-- USING TOP NUMBERS --")
            numbers.getTopNumbers(.8f).keys
        } else {
            predictNumberSequences.predictionNumbers
        }
        val combinations = generateCombinations(numbersToUse, drawings.totoType.size)
            .map { UniquePattern(it.toIntArray()) }
            .toMutableSet()

        // Remove any combinations that have been drawn previously
        removeDuplicateDrawings(combinations)

        val combinationsToRemove = getNumberCombinationsToRemove()
        removeCombinationsWithLowerNumberCorrelations(
            combinations = combinations,
            combinationsToRemove = combinationsToRemove
        )

        val reducedNumbers: List<Int> = combinations.flatMap { it.array.toList() }
        val takeRatio = .37f
        val groupPatterns = mutableSetOf<UniquePattern>()
        numberToPatternCorrelations.numberToGroupPatterns.forEach { (num, patterns) ->
            if (reducedNumbers.contains(num).not()) return@forEach

            val min = patterns.values.minOf { it }
            val max = patterns.values.maxOf { it }
            val splitPoint = (max - ((max - min) * takeRatio)).toInt()
            groupPatterns.addAll(patterns.filter { it.value > splitPoint }.keys)
        }
        combinations.removeIf {
            val gp = it.array.map { drawings.totoType.getNumberGroup.invoke(it) }.toIntArray()
            groupPatterns.contains(UniquePattern(gp)).not()
        }

        val lowHighPatterns = mutableSetOf<UniquePattern>()
        groupPatternToPatternCorrelations.groupToLowHighPatterns.forEach { (group, lowHighs) ->
            if (groupPatterns.contains(group).not()) return@forEach

            val min = lowHighs.values.minOf { it }
            val max = lowHighs.values.maxOf { it }
            val splitPoint = (max - ((max - min) * takeRatio)).toInt()
            lowHighPatterns.addAll(lowHighs.filter { it.value > splitPoint }.keys)
        }
        combinations.removeIf {
            val lh = it.array.map { drawings.totoType.getNumberLowHigh.invoke(it) }.toIntArray()
            lowHighPatterns.contains(UniquePattern(lh)).not()
        }

        val oddEvenPatterns = mutableSetOf<UniquePattern>()
        groupPatternToPatternCorrelations.groupToOddEvenPatterns.forEach { (group, oddEvens) ->
            if (groupPatterns.contains(group).not()) return@forEach

            val min = oddEvens.values.minOf { it }
            val max = oddEvens.values.maxOf { it }
            val splitPoint = (max - ((max - min) * takeRatio)).toInt()
            oddEvenPatterns.addAll(oddEvens.filter { it.value > splitPoint }.keys)
        }
        combinations.removeIf {
            val oe = it.array.map { drawings.totoType.getNumberOddEven.invoke(it) }.toIntArray()
            oddEvenPatterns.contains(UniquePattern(oe)).not()
        }

        println("Total of ${combinations.size} combinations.")
        getRandomCombinations(8, combinations).forEach {
            println(it.array.toList().toString().replace("[", "").replace("]", ""))
        }
    }

    /**
     * Generate all possible combination with the predicted numbers.
     */
    private fun generateCombinations(numbers: Set<Int>, size: Int): List<List<Int>> {
        // List to store all the generated combinations
        val combinations = mutableListOf<List<Int>>()

        // Recursive helper function to generate combinations
        fun backtrack(start: Int, currentCombination: MutableList<Int>) {
            // If the current combination size matches the desired size, add it to the list of combinations
            if (currentCombination.size == size) {
                combinations.add(currentCombination.sorted())
                return
            }

            // Loop through the elements of the set starting from the 'start' index
            for (i in start until numbers.size) {
                // Add the current element to the current combination
                currentCombination.add(numbers.elementAt(i))

                // Recursively call the backtrack function with the next index and the updated combination
                backtrack(i + 1, currentCombination)

                // Remove the last element from the current combination to backtrack and explore other possibilities
                currentCombination.removeAt(currentCombination.size - 1)
            }
        }

        // Start the recursive generation process with an empty initial combination
        backtrack(0, mutableListOf())

        // Return the list of all generated combinations
        return combinations
    }

    private fun removeDuplicateDrawings(combinations: MutableSet<UniquePattern>) {
        val drawings = this.drawings.drawings.map { UniquePattern(it.numbers) }
        drawings.forEach { combinations.remove(it) }
    }

    private fun getNumberCombinationsToRemove(): Set<UniquePattern> {
        val min = numberCombinations.numberCombinations.minOf { it.value }
        val max = numberCombinations.numberCombinations.maxOf { it.value }
        val takeRatio = .77f
        val splitPoint = (max - ((max - min) * takeRatio)).toInt()

        return numberCombinations.numberCombinations.filter { it.value < splitPoint }.keys
    }

    private fun removeCombinationsWithLowerNumberCorrelations(
        combinations: MutableSet<UniquePattern>,
        combinationsToRemove: Set<UniquePattern>
    ) {
        combinations.removeIf { combo ->
            combinationsToRemove.any { comboToRemove ->
                comboToRemove.array.all { num ->
                    num in combo.array
                }
            }
        }
    }

    private fun getRandomCombinations(
        numOfResults: Int,
        combinations: MutableSet<UniquePattern>
    ): Set<UniquePattern> {
        if (combinations.size < numOfResults) return combinations

        val randomCombos = mutableSetOf<UniquePattern>()
        val random = Random.Default

        while (randomCombos.size < numOfResults) {
            randomCombos.add(combinations.random(random))
        }

        return randomCombos
    }
}