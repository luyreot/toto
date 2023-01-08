package data

import collection.QueueList
import model.TotoType
import kotlin.math.roundToInt

class PredictGroupPattern(
    private val totoType: TotoType,
    private val period: Int = PERIOD
) {

    val predictedPattern = IntArray(totoType.size)

    private val averageTrueRangePatternArray = Array<QueueList<Float>>(totoType.size) {
        QueueList(period)
    }

    var predictedThreesCount: Int = 0
    var predictedFoursCount: Int = 0
    var predictedFivesCount: Int = 0
    var predictedSixesCount: Int = 0

    init {
        for (i in 0 until totoType.size) {
            predictedPattern[i] = PATTERN_DEFAULT_VALUE
        }
    }

    fun takePattern(pattern: IntArray, drawingIndex: Int) {
        if (pattern.size != totoType.size)
            throw IllegalArgumentException("There is something wrong with the group pattern!")

        if (drawingIndex == 0) {
            for (i in averageTrueRangePatternArray.indices) {
                pattern[i].div(1f).let {
                    averageTrueRangePatternArray[i].add(it)
                    predictedPattern[i] = normalizePrediction(it)
                }
            }

            return
        }

        val nextPattern = pattern.toMutableList()
        var predictedNumCount = 0
        predictedPattern.forEach { predictedNum ->
            if (nextPattern.contains(predictedNum)) {
                predictedNumCount++
                nextPattern.remove(predictedNum)
            }
        }
        when {
            predictedNumCount == 6 && predictedPattern.contentEquals(pattern) -> {
                predictedSixesCount++
            }

            predictedNumCount == 5 -> {
                predictedFivesCount++
            }

            predictedNumCount == 4 -> {
                predictedFoursCount++
            }

            predictedNumCount == 3 -> {
                predictedThreesCount++
            }
        }

        for (i in averageTrueRangePatternArray.indices) {
            getLastPeriodValue(i, pattern[i]).let {
                averageTrueRangePatternArray[i].add(it)
                predictedPattern[i] = normalizePrediction(it)
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

    private companion object {
        const val PATTERN_DEFAULT_VALUE = -1

        // 2 drawings per week x 4 weeks = 1 month
        const val PERIOD = 384
        const val OFFSET = -0.2f
    }
}