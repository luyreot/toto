package data

import extensions.clearAfter
import model.Numbers
import model.TotoType
import util.FileConstants.FILE_TXT_5x35_PREDICTIONS
import util.FileConstants.FILE_TXT_5x35_PREDICTIONS_RANDOM
import util.FileConstants.FILE_TXT_6x42_PREDICTIONS
import util.FileConstants.FILE_TXT_6x42_PREDICTIONS_RANDOM
import util.FileConstants.FILE_TXT_6x49_PREDICTIONS
import util.FileConstants.FILE_TXT_6x49_PREDICTIONS_RANDOM
import util.GlobalConfig
import util.IO
import util.TotoUtils.getDrawingScore
import util.TotoUtils.printPredictionScore
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random.Default.nextInt

class PredictNextDrawing(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val fromYear: Int? = null,
    private val numberStats: NumberStats,
    private val drawingScoreStats: DrawingScoreStats,
    private val predictPatternOptimizer: PredictPatternOptimizer,
    private val groupNumberStats: GroupNumberStats
) {

    val nextDrawingsScore = mutableMapOf<IntArray, Int>()

    fun predictNextDrawing() {
        val allDrawings = drawings.drawings.toSet()
        val drawings = getAllDrawings()
        val predictionsSet = mutableSetOf<Numbers>()
        nextDrawingsScore.clear()

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

        removeDrawingsThatHaveBeenDrawn(
            predictions = predictionsSet,
            allDrawings = allDrawings
        )
        removeDrawingsNotInTheSameDrawingGroup(predictionsSet)
        removeDrawingsWithNumbersThatCannotOccurNext(
            predictions = predictionsSet,
            drawings = drawings.reversed()
        )

        // Calculate prediction score
        predictionsSet.forEach { drawing ->
            nextDrawingsScore[drawing.numbers] = getDrawingScore(
                drawings.size,
                drawing.numbers,
                numberStats.patterns,
                numberStats.frequencies,
                numberStats.averageFrequencies,
                drawings
            )
        }

        GlobalConfig.apply {
            val randomDerivedPicks = getDerivativeRandomPredictions(
                if (loadPreviousRandomPicks) getPreviousRandomPicks() else getRandomPicks()
            )

            if (checkPredictionScore) {
                println("====== All predictions:")
                printPredictionScore(GlobalConfig.PredictionScoreTester.drawing, nextDrawingsScore.keys.toList())

                if (calculateDerivedPredictions) {
                    println("====== All derived predictions:")
                    printPredictionScore(GlobalConfig.PredictionScoreTester.drawing, randomDerivedPicks)
                }
            }

            if (savePredictionsToFile) {
                saveAllPredictionsToFile()
            }
        }
    }

    private fun getAllDrawings() = if (fromYear == null) drawings.drawings else drawings.drawingsSubset

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

    private fun removeDrawingsThatHaveBeenDrawn(
        predictions: MutableCollection<Numbers>,
        allDrawings: Collection<Numbers>
    ) {
        predictions.removeIf { allDrawings.contains(it) }
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

    private fun getRandomPicks(): List<IntArray> {
        val randomPicks = mutableListOf<IntArray>()

        nextDrawingsScore.keys.toMutableList().apply {
            for (i in 0 until size / 1000) {
                this.shuffle()
            }
            for (i in 0 until 8) {
                randomPicks.add(elementAt(nextInt(size)))
            }

            println("Total of $size results.")
            println("Random picks:")
            randomPicks.forEach { println(drawingToString(it)) }
        }

        return randomPicks
    }

    private fun getPreviousRandomPicks(): List<IntArray> = if (GlobalConfig.loadPreviousRandomPicks) {
        listOf(
            intArrayOf(
                4, 17, 27, 38, 41, 44
            ),
            intArrayOf(
                1, 17, 29, 35, 39, 47
            ),
            intArrayOf(
                4, 17, 29, 38, 42, 49
            ),
            intArrayOf(
                15, 21, 28, 32, 34, 47
            ),
            intArrayOf(
                9, 16, 20, 34, 44, 48
            ),
            intArrayOf(
                14, 20, 21, 35, 47, 48
            ),
            intArrayOf(
                1, 14, 23, 37, 42, 49
            ),
            intArrayOf(
                6, 12, 24, 35, 44, 49
            )
        )
    } else {
        throw IllegalArgumentException("loadPreviousRandomPicks is false!")
    }

    private fun getDerivativeRandomPredictions(randomPicks: List<IntArray>): List<IntArray> {
        // Create all possible drawings from those 8 drawings' numbers.
        // Each number can occur at any position in the drawings.
        val numbersList = Array<List<Int>>(totoType.size) { emptyList() }
        for (i in 0 until totoType.size) {
            numbersList[i] = randomPicks.flatMap { drawing -> drawing.map { number -> number } }.toSet().toList()
        }

        val derivedPredictions = generateDrawings(numbersList)
        val randomDerivedPicks = mutableListOf<Numbers>()

        derivedPredictions.apply {
            for (i in 0 until size / 1000) {
                this.shuffle()
            }

            for (i in 0 until 8) {
                randomDerivedPicks.add(Numbers(elementAt(nextInt(size))))
            }

            removeDrawingsThatHaveBeenDrawn(
                predictions = randomDerivedPicks,
                allDrawings = getAllDrawings()
            )

            println("Total of $size results.")
            println("Random derived picks:")
            randomDerivedPicks.forEach { println(drawingToString(it.numbers)) }
        }

        return derivedPredictions
    }

    private fun saveAllPredictionsToFile() {
        val stringBuilder = StringBuilder()
        val stringBuilderRandom = StringBuilder()

        stringBuilder.appendLine("(${nextDrawingsScore.keys.size})")
        stringBuilderRandom.appendLine("(${nextDrawingsScore.keys.size})")

        nextDrawingsScore.keys.forEach { prediction ->
            stringBuilder.appendLine(drawingToString(prediction))
        }

        nextDrawingsScore.keys.shuffled(Random()).forEach { prediction ->
            stringBuilderRandom.appendLine(drawingToString(prediction))
        }

        when (totoType) {
            TotoType.T_6X49 -> {
                IO.saveTxtFile(FILE_TXT_6x49_PREDICTIONS, stringBuilder.toString())
                IO.saveTxtFile(FILE_TXT_6x49_PREDICTIONS_RANDOM, stringBuilderRandom.toString())
            }

            TotoType.T_6X42 -> {
                IO.saveTxtFile(FILE_TXT_6x42_PREDICTIONS, stringBuilder.toString())
                IO.saveTxtFile(FILE_TXT_6x42_PREDICTIONS_RANDOM, stringBuilderRandom.toString())
            }

            TotoType.T_5X35 -> {
                IO.saveTxtFile(FILE_TXT_5x35_PREDICTIONS, stringBuilder.toString())
                IO.saveTxtFile(FILE_TXT_5x35_PREDICTIONS_RANDOM, stringBuilderRandom.toString())
            }
        }
    }

    private fun drawingToString(drawing: IntArray): String {
        return drawing.toList().toString().replace("[", "").replace("]", "")
    }

    private companion object {
        const val NUMBERS_PER_GROUP_PER_DRAWING: Int = 2
    }
}