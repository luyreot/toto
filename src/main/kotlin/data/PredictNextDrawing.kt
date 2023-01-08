package data

import extensions.clearAfter
import extensions.sortByValueDescending
import model.Numbers
import model.TotoType
import util.PredictionTester
import util.TotoUtils.getDrawingScore
import java.util.*
import kotlin.math.roundToInt

class PredictNextDrawing(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val fromYear: Int? = null,
    private val numberStats: NumberStats,
    private val drawingScoreStats: DrawingScoreStats,
    private val predictPatternOptimizer: PredictPatternOptimizer,
    private val groupNumberStats: GroupNumberStats
) {

    val nextDrawingsTopScore = mutableMapOf<IntArray, Int>()
    val nextDrawingsAverageScore = mutableMapOf<IntArray, Int>()

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
        predictionsSet.removeIf {
            allDrawings.contains(it)
        }

        removeDrawingsNotInTheSameDrawingGroup(predictionsSet)
        removeDrawingsWithNumbersThatCannotOccurNext(
            predictions = predictionsSet,
            drawings = drawings.reversed()
        )

        // Calculate prediction score
        predictionsSet.forEach { drawing ->
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

        /* No need for this. Best results are coming from the top results.
        // Store top scores that are between the average score and the possible jump in the positive and negative
        nextDrawingsAverageScore.putAll(
            nextDrawingsTopScore.filter { entry ->
                entry.value < drawingScoreStats.averageSore + drawingScoreStats.averageJump &&
                        entry.value > drawingScoreStats.averageSore - drawingScoreStats.averageJump
            }
        )
        */

        println("Total of ${nextDrawingsTopScore.size} top results.")

        PredictionTester.apply {
            if (isTestingPredictions) {
                println("====== Next drawings is ${nextDrawing?.toList()}")
                println("====== Checking top predictions:")
                checkPredictions(nextDrawingsTopScore)
                return
            }
        }

        printRandomTopPredictions()
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
    ): Boolean = group == totoType.groupStrategy.invoke(number)

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

    private fun removeDrawingsNotInTheSameDrawingGroup(
        drawings: MutableSet<Numbers>
    ) {
        drawings.removeIf { drawing ->
            var shouldRemove = false

            for (one in drawing.numbers.indices) {
                val averageNumberScore = groupNumberStats.averageGroupOccurrence[drawing.numbers[one]] ?: 0
                var numberOfPairsFromSameGroup = 0

                for (two in drawing.numbers.indices) {
                    if (one == two) continue

                    val secondNumberScore = groupNumberStats.groups[drawing.numbers[one]]?.get(drawing.numbers[two]) ?: 0

                    if (secondNumberScore <= averageNumberScore) continue

                    numberOfPairsFromSameGroup++
                }

                if (numberOfPairsFromSameGroup < NUMBERS_PER_GROUP_PER_DRAWING) {
                    shouldRemove = true
                    break
                }
            }

            shouldRemove
        }
    }

    private fun removeDrawingsWithNumbersThatCannotOccurNext(
        predictions: MutableSet<Numbers>,
        drawings: List<Numbers>
    ) {
        predictions.removeIf { prediction ->
            var shouldRemove = false

            for (l in prediction.numbers.indices) {
                val number = prediction.numbers[l]

                // Get difference between the upcoming and last drawing when the pattern has occurred
                val upcomingFrequency = getUpcomingDrawingNumberFrequency(
                    number,
                    drawings
                )

                // Get upcoming frequency count
                val upcomingFrequencyCount: Int = numberStats.frequencies[number]
                    ?.find { frequency -> frequency.frequency == upcomingFrequency }?.count
                    ?: 1

                if (numberStats.averageFrequencies.containsKey(number).not()) {
                    throw IllegalArgumentException("Average number frequency is missing for number $number")
                }

                // Remove drawing if any of its number's upcoming frequency count is below average
                if (upcomingFrequencyCount <= (numberStats.averageFrequencies[number]?.roundToInt() ?: 1)) {
                    shouldRemove = true
                    break
                }
            }

            shouldRemove
        }
    }

    private fun getUpcomingDrawingNumberFrequency(
        predictionNumber: Int,
        drawings: List<Numbers>
    ): Int {
        drawings.forEachIndexed { index, drawing ->
            drawing.numbers.forEach { number ->
                if (predictionNumber == number) {
                    return index + 1
                }
            }
        }

        throw IllegalArgumentException("Something went wrong! Could not find any previous drawing with the following number - $predictionNumber")
    }

    private fun printRandomTopPredictions() {
        Random().apply {
            println("Random TOP picks:")
            nextDrawingsTopScore.keys.shuffled(this).let { predictions ->
                printDrawing(predictions.elementAt(nextInt(predictions.size)))
                printDrawing(predictions.elementAt(nextInt(predictions.size)))
                printDrawing(predictions.elementAt(nextInt(predictions.size)))
                printDrawing(predictions.elementAt(nextInt(predictions.size)))
                printDrawing(predictions.elementAt(nextInt(predictions.size)))
                printDrawing(predictions.elementAt(nextInt(predictions.size)))
                printDrawing(predictions.elementAt(nextInt(predictions.size)))
                printDrawing(predictions.elementAt(nextInt(predictions.size)))
            }
        }
    }

    private fun printDrawing(drawing: IntArray) {
        println(drawing.toList().toString().replace("[", "").replace("]", ""))
    }

    private companion object {
        const val NUMBERS_PER_GROUP_PER_DRAWING: Int = 2
    }
}