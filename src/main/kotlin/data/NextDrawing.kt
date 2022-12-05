package data

import extensions.clearAfter
import extensions.sortByValueDescending
import model.GroupStrategy
import model.Numbers
import model.TotoType
import model.groupStrategies
import util.PatternUtils.convertToGroupPatternDelta
import util.TotoUtils.getDrawingScore

class NextDrawing(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val fromYear: Int? = null,
    private val numberStats: NumberStats,
    private val oddEvenPatternStats: OddEvenPatternStats,
    private val predictOddEvenPattern: PredictOddEvenPattern,
    private val lowHighPatternStats: LowHighPatternStats,
    private val predictLowHighPattern: PredictLowHighPattern,
    private val groupPatternStats: GroupPatternStats,
    private val predictGroupPattern: PredictGroupPattern,
    private val groupPatternDeltaStats: GroupPatternDeltaStats,
    private val groupStrategy: GroupStrategy,
    private val groupDeltaStrategy: GroupStrategy,
    private val combinedPatternStats: CombinedPatternStats,
    private val drawingScoreStats: DrawingScoreStats
) {

    val nextDrawingsTopScore = mutableMapOf<IntArray, Int>()
    val nextDrawingsAverageScore = mutableMapOf<IntArray, Int>()

    private val groupStrategyMethod = groupStrategies[groupStrategy] as? (Int) -> Int
    private val groupDeltaStrategyMethod = groupStrategies[groupDeltaStrategy] as? (Int, Int) -> Int

    init {
        if (groupStrategyMethod == null)
            throw IllegalArgumentException("Group strategy method is null!")

        if (groupDeltaStrategyMethod == null)
            throw IllegalArgumentException("Group Delta strategy method is null!")
    }

    fun predictNextDrawing() {
        val predictionNumbers = Array<List<Int>>(totoType.size) { emptyList() }

        combinedPatternStats.patterns.find { it.groupPattern == Numbers(predictGroupPattern.nextPattern) }?.let { combinedPattern ->
            val lowHighExist = combinedPattern.lowHighs.keys.contains(Numbers(predictLowHighPattern.nextPattern))
            val oddEvenExist = combinedPattern.oddEvens.keys.contains(Numbers(predictOddEvenPattern.nextPattern))
            TODO()
        }

        for (i in 0 until totoType.size) {
            val isOdd = predictOddEvenPattern.nextPattern[i] == 0
            val isLow = predictLowHighPattern.nextPattern[i] == 0
            val group = when {
                isLow && predictGroupPattern.nextPattern[i] > 2 -> 2
                isLow.not() && predictGroupPattern.nextPattern[i] < 2 -> 2
                else -> predictGroupPattern.nextPattern[i]
            }

            predictionNumbers[i] = getPredictionNumbers(
                isOdd = isOdd,
                isLow = isLow,
                group = group
            )
        }

        val numberCombinations: MutableList<IntArray> = generateDrawings(predictionNumbers)

        val allDrawings = drawings.drawings.toSet()
        for (i in numberCombinations.size - 1 downTo 0) {
            // Remove already existing drawings
            if (allDrawings.contains(Numbers(numberCombinations[i]))) {
                numberCombinations.removeAt(i)
            }

            // Remove drawings that have occurred as delta pattern
            val doesDeltaPatternExist = groupPatternDeltaStats
                .patterns
                .keys
                .contains(Numbers(convertToGroupPatternDelta(numberCombinations[i].copyOf(), groupDeltaStrategyMethod)))
            if (doesDeltaPatternExist) {
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
        isOdd: Boolean,
        isLow: Boolean,
        group: Int
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

    private fun isEven(number: Int): Boolean = number and 1 == 0

    private fun isHigh(number: Int): Boolean = number > totoType.lowHighMidPoint

    private fun isFromSameGroup(
        group: Int,
        number: Int
    ): Boolean = group == groupStrategyMethod?.invoke(number)

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