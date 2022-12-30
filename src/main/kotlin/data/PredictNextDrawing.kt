package data

import data.GroupNumberStats.Companion.NUMBERS_PER_GROUP_PER_DRAWING
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
    private val predictPatternOptimizer: PredictPatternOptimizer,
    private val groupNumberStats: GroupNumberStats
) {

    val nextDrawingsTopScore = mutableMapOf<IntArray, Int>()
    val nextDrawingsAverageScore = mutableMapOf<IntArray, Int>()

    private val groupStrategyMethod = groupStrategies[groupStrategy] as? (Int) -> Int

    init {
        if (groupStrategyMethod == null)
            throw IllegalArgumentException("Group strategy method is null!")
    }

    fun predictNextDrawing() {
        val allDrawings = drawings.drawings.toSet()
        val drawings = if (fromYear == null) drawings.drawings else drawings.drawingsSubset
        val predictionsSet = mutableSetOf<Numbers>()

        // Iterate over each group pattern and its low/high and odd/even patterns
        // to get all number which will be used for generating drawings.
        predictPatternOptimizer.upcommingPatterns.forEach { predictedPatterns ->
            val numbersSet = Array<MutableSet<Int>>(totoType.size) { mutableSetOf() }

            for (i in 0 until totoType.size) {
                val group: Int = predictedPatterns.groupPattern.numbers[i]
                val isLows: List<Boolean> = predictedPatterns.lowHighs.map { it.key.numbers[i] == 0 }
                val isOdds: List<Boolean> = predictedPatterns.oddEvens.map { it.key.numbers[i] == 0 }

                isLows.forEach { isLow ->
                    isOdds.forEach { isOdd ->
                        numbersSet[i].addAll(
                            getPredictionNumbers(
                                group = group,
                                isLow = isLow,
                                isOdd = isOdd
                            )
                        )
                    }
                }
            }

            val numbersList = Array<List<Int>>(totoType.size) { emptyList() }
            numbersSet.forEachIndexed { index, set ->
                numbersList[index] = set.toList()
            }
            generateDrawings(numbersList).forEach {
                predictionsSet.add(Numbers(it))
            }
        }

        // Remove already existing drawings
        val predictionsList: MutableList<Numbers> = predictionsSet.toMutableList()
        for (i in predictionsList.size - 1 downTo 0) {
            if (allDrawings.contains(predictionsList[i])) {
                predictionsList.removeAt(i)
            }
        }

        // Calculate prediction score
        predictionsList.forEach { drawing ->
            nextDrawingsTopScore[drawing.numbers] = getDrawingScore(
                drawings.size,
                drawing.numbers,
                numberStats.patterns,
                numberStats.frequencies,
                numberStats.averageFrequencies,
                drawings
            )
        }

        nextDrawingsTopScore.sortByValueDescending()
        removeDrawingsNotInTheSameGroup(nextDrawingsTopScore)

        // TODO: Optimize predictions based on each number's frequency

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

        numberStats.patterns.forEach { (number, _) ->
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

    private fun removeDrawingsNotInTheSameGroup(
        drawings: MutableMap<IntArray, Int>
    ) {
        val toBeRemoved = mutableListOf<IntArray>()

        drawings.keys.forEach { numbers ->
            for (one in numbers.indices) {
                val averageNumberScore = groupNumberStats.averageGroupOccurrence[numbers[one]] ?: 0
                var numberOfPairsFromSameGroup = 0

                for (two in numbers.indices) {
                    if (one == two) continue

                    val secondNumberScore = groupNumberStats.groups[numbers[one]]?.get(numbers[two]) ?: 0

                    if (secondNumberScore <= averageNumberScore) continue

                    numberOfPairsFromSameGroup++
                }

                if (numberOfPairsFromSameGroup < NUMBERS_PER_GROUP_PER_DRAWING) {
                    toBeRemoved.add(numbers)
                    return@forEach
                }
            }
        }

        toBeRemoved.forEach {
            drawings.remove(it)
        }
    }
}