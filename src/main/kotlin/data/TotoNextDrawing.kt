package data

import extensions.sortByValueDescending
import model.TotoGroupStrategy
import model.TotoNumber
import model.TotoType
import model.groupStrategies

class TotoNextDrawing(
    private val totoType: TotoType,
    private val totoNumbers: TotoNumbers,
    private val totoNumberStats: TotoNumberStats,
    private val totoOddEvenPatternStats: TotoOddEvenPatternStats,
    private val totoOddEvenPatternPredict: TotoOddEvenPatternPredict,
    private val totoLowHighPatternStats: TotoLowHighPatternStats,
    private val totoLowHighPatternPredict: TotoLowHighPatternPredict,
    private val totoGroupPatternStats: TotoGroupPatternStats,
    private val totoGroupPatternPredict: TotoGroupPatternPredict,
    private val groupStrategy: TotoGroupStrategy
) {

    val predictionCombinations = mutableMapOf<IntArray, Int>()

    lateinit var nextOddEvenPattern: IntArray
    lateinit var nextLowHighPattern: IntArray
    lateinit var nextGroupPattern: IntArray

    private val groupStrategyMethod = groupStrategies[groupStrategy] as (Int) -> Int

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
        val numberOfResults = 2 // get # different numbers for each slot
        val predictionNumbers = Array<List<Int>>(totoType.drawingSize) { emptyList() }

        for (i in 0 until totoType.drawingSize) {
            val isOdd = nextOddEvenPattern[i] == 0
            val isLow = nextLowHighPattern[i] == 0
            val group = nextGroupPattern[i]
            predictionNumbers[i] = getPredictedNumbers(
                isOdd = isOdd,
                isLow = isLow,
                group = group,
                numberOfResults = numberOfResults
            )
        }

        val numberCombinations: MutableList<IntArray> = getPredictionCombinations(predictionNumbers)

        // Remove already existing drawings
        for (i in numberCombinations.size - 1 downTo 0) {
            if (doesDrawingExists(numberCombinations[i])) {
                numberCombinations.removeAt(i)
            }
        }

        // Calculate prediction score
        numberCombinations.forEach { drawing ->
            predictionCombinations[drawing] = getDrawingScore(drawing)
        }
        predictionCombinations.sortByValueDescending()
    }

    private fun getPredictedNumbers(
        isOdd: Boolean,
        isLow: Boolean,
        group: Int,
        numberOfResults: Int
    ): List<Int> {
        val results = mutableListOf<Int>()

        totoNumberStats.occurrences.forEach { (number, _) ->
            val isOddEvenCriteriaFulfilled = (isOdd.not() && isEven(number)) || (isOdd && isEven(number).not())
            val isLowHighCriteriaFulfilled = (isLow.not() && isHigh(number)) || (isLow && isHigh(number).not())
            val isGroupCriteriaFulfilled = isFromSameGroup(group, number)

            if (isOddEvenCriteriaFulfilled && isLowHighCriteriaFulfilled && isGroupCriteriaFulfilled)
                results.add(number)

            if (results.size == numberOfResults)
                return results
        }

        if (results.isEmpty())
            throw IllegalArgumentException("Empty number prediction results!")

        // There are only 2 even / odd numbers from group 2 that are high - 26, 28 and 27, 29
        if (results.size != numberOfResults && (group == 2 && isLow.not()).not())
            throw IllegalArgumentException("Something is wrong with the number prediction results!")

        return results
    }

    private fun isEven(number: Int): Boolean = number and 1 == 0

    private fun isHigh(number: Int): Boolean = number > totoType.lowHighMidPoint

    private fun isFromSameGroup(group: Int, number: Int): Boolean = group == groupStrategyMethod.invoke(number)

    /**
     * Currently works for list size of 2.
     */
    private fun getPredictionCombinations(appPossibleNumbers: Array<List<Int>>): MutableList<IntArray> {
        val combinations = mutableListOf<IntArray>()
        val numberOfCombinations = appPossibleNumbers
            .map { list -> list.size }
            .reduce { acc, size -> acc * size } // 2 x 2 x 2 x 2 x 2 x 2 = 64

        for (i in 0 until numberOfCombinations) {
            combinations.add(IntArray(totoType.drawingSize))
        }

        appPossibleNumbers.forEachIndexed { arrayIndex, possibleNumbersList ->
            combinations.forEachIndexed { combinationIndex, combinationArray ->
                // Must be either 0 or 1
                val possibleNumberIndex = when (arrayIndex) {
                    0 -> combinationIndex / 32 % 2
                    1 -> combinationIndex / 16 % 2
                    2 -> combinationIndex / 8 % 2
                    3 -> combinationIndex / 4 % 2
                    4 -> combinationIndex / 2 % 2
                    5 -> combinationIndex % 2
                    else -> 0
                }
                combinationArray[arrayIndex] = possibleNumbersList[possibleNumberIndex]
            }
        }

        return combinations
    }

    fun doesDrawingExists(drawing: IntArray): Boolean {
        totoNumbers.numbers
            .sortedWith(compareBy<TotoNumber> { it.year }.thenBy { it.issue }.thenBy { it.position })
            .let { sortedTotoNumbers ->
                val currentDrawing = IntArray(totoType.drawingSize)

                sortedTotoNumbers.forEach { totoNumber ->
                    currentDrawing[totoNumber.position] = totoNumber.number
                    if (totoNumber.position == totoType.drawingSize - 1) {
                        currentDrawing.forEachIndexed { index, number ->
                            if (drawing[index] != number) {
                                return@forEach
                            }
                        }
                        return true
                    }
                }
            }

        return false
    }

    private fun getDrawingScore(drawing: IntArray): Int {
        var score = 0
        drawing.forEach { number ->
            score += totoNumberStats.occurrences[number] ?: 0
        }
        return score
    }
}