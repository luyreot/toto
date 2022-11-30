package data

import extensions.clearAfter
import extensions.sortByValueDescending
import model.TotoGroupStrategy
import model.TotoNumbers
import model.TotoType
import model.groupStrategies
import util.Helper.getDrawingScore

class TotoNextDrawing(
    private val totoType: TotoType,
    private val totoNumbers: TotoDrawnNumbers,
    private val fromYear: Int? = null,
    private val totoNumberStats: TotoNumberStats,
    private val totoOddEvenPatternStats: TotoOddEvenPatternStats,
    private val totoOddEvenPatternPredict: TotoOddEvenPatternPredict,
    private val totoLowHighPatternStats: TotoLowHighPatternStats,
    private val totoLowHighPatternPredict: TotoLowHighPatternPredict,
    private val totoGroupPatternStats: TotoGroupPatternStats,
    private val totoGroupPatternPredict: TotoGroupPatternPredict,
    private val totoGroupPatternDeltaStats: TotoGroupPatternDeltaStats,
    private val totoGroupPatternDeltaPredict: TotoGroupPatternDeltaPredict,
    private val groupStrategy: TotoGroupStrategy,
    private val totoDrawingScoreStats: TotoDrawingScoreStats
) {

    val predictionCombinationsTopScore = mutableMapOf<IntArray, Int>()
    val predictionCombinationsAverageScore = mutableMapOf<IntArray, Int>()

    lateinit var nextOddEvenPattern: IntArray
    lateinit var nextLowHighPattern: IntArray
    lateinit var nextGroupPattern: IntArray

    private val groupStrategyMethod = groupStrategies[groupStrategy] as? (Int) -> Int

    init {
        if (groupStrategyMethod == null)
            throw IllegalArgumentException("Group strategy method is null!")
    }

    fun populateArrays() {
        nextOddEvenPattern = totoOddEvenPatternPredict.nextOddEvenPattern.map { it.toInt() }.toIntArray()
        if (nextOddEvenPattern.size != totoType.drawingSize)
            throw IllegalArgumentException("Something wrong with the predicted odd even pattern!")

        nextLowHighPattern = totoLowHighPatternPredict.nextLowHighPattern.map { it.toInt() }.toIntArray()
        if (nextLowHighPattern.size != totoType.drawingSize)
            throw IllegalArgumentException("Something wrong with the predicted low high pattern!")

        nextGroupPattern = totoGroupPatternPredict.nextGroupPattern.map { it.toInt() }.toIntArray()
        if (nextGroupPattern.size != totoType.drawingSize)
            throw IllegalArgumentException("Something wrong with the predicted group pattern!")
    }

    fun predictNextDrawing() {
        val predictionNumbers = Array<List<Int>>(totoType.drawingSize) { emptyList() }

        for (i in 0 until totoType.drawingSize) {
            val isOdd = nextOddEvenPattern[i] == 0
            val isLow = nextLowHighPattern[i] == 0
            val group = when {
                isLow && nextGroupPattern[i] > 2 -> 2
                isLow.not() && nextGroupPattern[i] < 2 -> 2
                else -> nextGroupPattern[i]
            }

            predictionNumbers[i] = getPredictedNumbers(
                isOdd = isOdd,
                isLow = isLow,
                group = group
            )
        }

        val numberCombinations: MutableList<IntArray> = getPredictionCombinations(predictionNumbers)

        val allDrawings = totoNumbers.allDrawings.toSet()
        for (i in numberCombinations.size - 1 downTo 0) {
            // Remove already existing drawings
            if (allDrawings.contains(TotoNumbers(numberCombinations[i]))) {
                numberCombinations.removeAt(i)
            }

            // Remove drawings that have occurred as delta pattern
            val doesDeltaPatternExist = totoGroupPatternDeltaStats
                .patterns
                .keys
                .contains(
                    TotoNumbers(
                        totoGroupPatternDeltaStats
                            .convertTotoNumbersToGroupPatternDelta(
                                numberCombinations[i].copyOf()
                            )
                    )
                )
            if (doesDeltaPatternExist) {
                numberCombinations.removeAt(i)
            }
        }

        val drawings = if (fromYear == null) totoNumbers.allDrawings else totoNumbers.drawingsSubset

        // Calculate prediction score
        numberCombinations.forEach { drawing ->
            predictionCombinationsTopScore[drawing] = getDrawingScore(
                drawings.size,
                drawing,
                totoNumberStats.occurrences,
                totoNumberStats.frequencies,
                totoNumberStats.averageFrequencies,
                drawings
            )
        }
        predictionCombinationsTopScore.sortByValueDescending()

        // Store top scores that are between the average score and the possible jump in the positive and negative
        predictionCombinationsAverageScore.putAll(
            predictionCombinationsTopScore.filter { entry ->
                entry.value < totoDrawingScoreStats.averageSore + totoDrawingScoreStats.averageJump &&
                        entry.value > totoDrawingScoreStats.averageSore - totoDrawingScoreStats.averageJump
            }
        )
    }

    private fun getPredictedNumbers(
        isOdd: Boolean,
        isLow: Boolean,
        group: Int
    ): List<Int> {
        val results = mutableListOf<Int>()

        totoNumberStats.occurrences.forEach { (number, _) ->
            val isOddEvenCriteriaFulfilled = (isOdd.not() && isEven(number)) || (isOdd && isEven(number).not())
            val isLowHighCriteriaFulfilled = (isLow.not() && isHigh(number)) || (isLow && isHigh(number).not())
            val isGroupCriteriaFulfilled = isFromSameGroup(group, number)

            if (isOddEvenCriteriaFulfilled && isLowHighCriteriaFulfilled && isGroupCriteriaFulfilled)
                results.add(number)
        }

        if (results.isEmpty())
            throw IllegalArgumentException("Empty number prediction results!")

        return results
    }

    private fun isEven(number: Int): Boolean = number and 1 == 0

    private fun isHigh(number: Int): Boolean = number > totoType.lowHighMidPoint

    private fun isFromSameGroup(
        group: Int,
        number: Int
    ): Boolean = group == groupStrategyMethod?.invoke(number)

    private fun getPredictionCombinations(allPossibleNumbers: Array<List<Int>>): MutableList<IntArray> {
        val combinations = mutableSetOf<TotoNumbers>()
        generateCombination(0, IntArray(allPossibleNumbers.size), allPossibleNumbers, combinations)
        return combinations.map { it.numbers }.toMutableList()
    }

    private fun generateCombination(
        arrayIndex: Int,
        array: IntArray,
        input: Array<List<Int>>,
        output: MutableSet<TotoNumbers>
    ) {
        for (i in 0 until input[arrayIndex].size) {
            array.clearAfter(arrayIndex)

            if (arrayIndex > 0 && array.any { it == input[arrayIndex][i] }) {
                continue
            }

            array[arrayIndex] = input[arrayIndex][i]

            if (arrayIndex == input.size - 1) {
                output.add(TotoNumbers(array.copyOf().sortedArray()))
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