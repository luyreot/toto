package data

import extensions.clearAfter
import extensions.sortByValueDescending
import model.GroupStrategy
import model.Numbers
import model.TotoType
import model.groupStrategies
import util.TotoUtils.getDrawingScore

class PredictNextDrawing(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val fromYear: Int? = null,
    private val numberStats: NumberStats,
    private val groupStrategy: GroupStrategy,
    private val drawingScoreStats: DrawingScoreStats,
    private val predictPatternOptimizer: PredictPatternOptimizer
) {

    val nextDrawingsTopScore = mutableMapOf<IntArray, Int>()
    val nextDrawingsAverageScore = mutableMapOf<IntArray, Int>()

    private val groupStrategyMethod = groupStrategies[groupStrategy] as? (Int) -> Int

    init {
        if (groupStrategyMethod == null)
            throw IllegalArgumentException("Group strategy method is null!")
    }

    fun predictNextDrawing() {
        val predictionNumbers = Array<List<Int>>(totoType.size) { emptyList() }

        for (i in 0 until totoType.size) {
            val group = predictPatternOptimizer.nextGroupPattern[i]
            val isLow = predictPatternOptimizer.nextLowHighPattern[i] == 0
            val isOdd = predictPatternOptimizer.nextOddEvenPattern[i] == 0

            predictionNumbers[i] = getPredictionNumbers(
                group = group,
                isLow = isLow,
                isOdd = isOdd
            )
        }

        val numberCombinations: MutableList<IntArray> = generateDrawings(predictionNumbers)

        val allDrawings = drawings.drawings.toSet()
        for (i in numberCombinations.size - 1 downTo 0) {
            // Remove already existing drawings
            if (allDrawings.contains(Numbers(numberCombinations[i]))) {
                numberCombinations.removeAt(i)
            }
        }

        val drawings = if (fromYear == null) drawings.drawings else drawings.drawingsSubset

        // Calculate prediction score
        numberCombinations.forEach { drawing ->
            nextDrawingsTopScore[drawing] = getDrawingScore(
                drawings.size,
                drawing,
                numberStats.occurrences,
                numberStats.frequencies,
                numberStats.averageFrequencies,
                drawings
            )
        }
        nextDrawingsTopScore.sortByValueDescending()

        // Store top scores that are between the average score and the possible jump in the positive and negative
        nextDrawingsAverageScore.putAll(
            nextDrawingsTopScore.filter { entry ->
                entry.value < drawingScoreStats.averageSore + drawingScoreStats.averageJump &&
                        entry.value > drawingScoreStats.averageSore - drawingScoreStats.averageJump
            }
        )
    }

    private fun getPredictionNumbers(
        group: Int,
        isLow: Boolean,
        isOdd: Boolean
    ): List<Int> {
        val numbers = mutableListOf<Int>()

        numberStats.occurrences.forEach { (number, _) ->
            val isOddEvenCriteriaFulfilled = (isOdd.not() && isEven(number)) || (isOdd && isEven(number).not())
            val isLowHighCriteriaFulfilled = (isLow.not() && isHigh(number)) || (isLow && isHigh(number).not())
            val isGroupCriteriaFulfilled = isFromSameGroup(group, number)

            if (isOddEvenCriteriaFulfilled && isLowHighCriteriaFulfilled && isGroupCriteriaFulfilled)
                numbers.add(number)
        }

        if (numbers.isEmpty())
            throw IllegalArgumentException("Empty number prediction results!")

        return numbers
    }

    private fun isFromSameGroup(
        group: Int,
        number: Int
    ): Boolean = group == groupStrategyMethod?.invoke(number)

    private fun isHigh(number: Int): Boolean = number > totoType.lowHighMidPoint

    private fun isEven(number: Int): Boolean = number and 1 == 0

    private fun generateDrawings(allPossibleNumbers: Array<List<Int>>): MutableList<IntArray> {
        val combinations = mutableSetOf<Numbers>()
        generateCombination(0, IntArray(allPossibleNumbers.size), allPossibleNumbers, combinations)
        return combinations.map { it.numbers }.toMutableList()
    }

    private fun generateCombination(
        arrayIndex: Int,
        array: IntArray,
        input: Array<List<Int>>,
        output: MutableSet<Numbers>
    ) {
        for (i in 0 until input[arrayIndex].size) {
            array.clearAfter(arrayIndex)

            if (arrayIndex > 0 && array.any { it == input[arrayIndex][i] }) {
                continue
            }

            array[arrayIndex] = input[arrayIndex][i]

            if (arrayIndex == input.size - 1) {
                output.add(Numbers(array.copyOf().sortedArray()))
            } else {
                generateCombination(
                    arrayIndex = arrayIndex + 1,
                    array = array,
                    input = input,
                    output = output
                )
            }
        }
    }
}