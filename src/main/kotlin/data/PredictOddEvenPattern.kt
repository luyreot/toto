package data

import collection.QueueList
import model.TotoType
import kotlin.math.roundToInt

class PredictOddEvenPattern(
    private val totoType: TotoType,
    private val period: Int = PERIOD
) {

    val nextPattern = IntArray(totoType.size)

    private val averageTrueRangePatternArray = Array<QueueList<Float>>(totoType.size) {
        QueueList(period)
    }

    // TODO Hardcoded for 6x49
    var predictedThreesCount: Int = 0
    var predictedFoursCount: Int = 0
    var predictedFivesCount: Int = 0
    var predictedSixesCount: Int = 0

    init {
        for (i in 0 until totoType.size) {
            nextPattern[i] = PATTERN_DEFAULT_VALUE
        }
    }

    fun takePattern(pattern: IntArray, drawingIndex: Int) {
        if (pattern.size != totoType.size)
            throw IllegalArgumentException("There is something wrong with the odd even pattern!")

        if (drawingIndex == 0) {
            for (i in averageTrueRangePatternArray.indices) {
                pattern[i].div(1f).let {
                    averageTrueRangePatternArray[i].add(it)
                    nextPattern[i] = normalizePrediction(it)
                }
            }

            return
        }

        val predictedPatternList = nextPattern.toList()
        val currentPatternList = pattern.toMutableList()
        var predictedIndices = 0
        predictedPatternList.forEach { predictedPatternIndex ->
            if (currentPatternList.contains(predictedPatternIndex)) {
                predictedIndices++
                currentPatternList.remove(predictedPatternIndex)
            }
        }
        if (predictedIndices >= 3) {
            predictedThreesCount++
        }
        if (predictedIndices >= 4) {
            predictedFoursCount++
        }
        if (predictedIndices >= 5) {
            predictedFivesCount++
        }
        if (nextPattern.contentEquals(pattern)) {
            predictedSixesCount++
        }

        for (i in averageTrueRangePatternArray.indices) {
            getSummedPeriodsValue(i, pattern[i]).let {
                averageTrueRangePatternArray[i].add(it)
                nextPattern[i] = normalizePrediction(it)
            }
        }
    }

    private fun normalizePrediction(value: Float): Int = (value + OFFSET).roundToInt()

    private fun getSummedPeriodsValue(arrayIndex: Int, currentPatternValue: Int): Float {
        val sum = if (averageTrueRangePatternArray[arrayIndex].isAtMaxSize())
            averageTrueRangePatternArray[arrayIndex].sum() - averageTrueRangePatternArray[arrayIndex][0]
        else
            averageTrueRangePatternArray[arrayIndex].sum()

        val divisor = if (averageTrueRangePatternArray[arrayIndex].isAtMaxSize())
            period
        else
            averageTrueRangePatternArray[arrayIndex].size + 1

        return (sum + currentPatternValue) / divisor
    }

    private fun getLastPeriodValue(arrayIndex: Int, currentPatternValue: Int): Float {
        val lastValue = averageTrueRangePatternArray[arrayIndex].last()
            ?: throw IllegalArgumentException("There is now last ATR value!")

        val multiplier = if (averageTrueRangePatternArray[arrayIndex].isAtMaxSize())
            period - 1
        else
            averageTrueRangePatternArray[arrayIndex].size

        val divisor = if (averageTrueRangePatternArray[arrayIndex].isAtMaxSize())
            period
        else
            averageTrueRangePatternArray[arrayIndex].size + 1

        return (lastValue * multiplier + currentPatternValue) / divisor
    }

    companion object {
        private const val PATTERN_DEFAULT_VALUE = -1

        // 2 drawings per week x 4 weeks = 1 month
        const val PERIOD = 32
        private const val OFFSET = 0f
    }
}