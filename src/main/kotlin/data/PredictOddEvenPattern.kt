package data

import collection.QueueList
import model.TotoType
import kotlin.math.roundToInt

class PredictOddEvenPattern(
    private val totoType: TotoType,
    private val period: Int = PERIOD
) {

    val nextPattern = FloatArray(totoType.size)

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
                    nextPattern[i] = it
                }
            }

            return
        }

        val predictedPattern = nextPattern.map { (it + 0.5f).toInt() }.toIntArray()
        val predictedPatternList = predictedPattern.toList()
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
        if (predictedPattern.contentEquals(pattern)) {
            predictedSixesCount++
        }

        for (i in averageTrueRangePatternArray.indices) {
            val divisor = if (averageTrueRangePatternArray[i].isAtMaxSize())
                period
            else
                averageTrueRangePatternArray[i].size + 1

            val sum = if (averageTrueRangePatternArray[i].isAtMaxSize())
                averageTrueRangePatternArray[i].sum() - averageTrueRangePatternArray[i][0]
            else
                averageTrueRangePatternArray[i].sum()

            val newAtrValue: Float = ((sum + pattern[i]) / divisor)
            averageTrueRangePatternArray[i].add(newAtrValue)
            nextPattern[i] = newAtrValue

//            averageTrueRangePatternArray[i].last()?.let { atrValue ->
//                val multiplier = if (averageTrueRangePatternArray[i].isAtMaxSize())
//                    period - 1
//                else
//                    averageTrueRangePatternArray[i].size
//
//                val divisor = if (averageTrueRangePatternArray[i].isAtMaxSize())
//                    period
//                else
//                    averageTrueRangePatternArray[i].size + 1
//
//                val newAtrValue: Float = (atrValue * multiplier + pattern[i]) / divisor
//                averageTrueRangePatternArray[i].add(newAtrValue)
//                nextPattern[i] = newAtrValue
//            }
        }
    }

    fun normalizePredictionPattern() {
        nextPattern.forEachIndexed { index, value ->
            nextPattern[index] = value.roundToInt().toFloat()
        }
    }

    //    Period - 32
//    PredictedThreesCount - 9366
//    PredictedFoursCount - 8423
//    PredictedFivesCount - 6022
//    PredictedSixesCount - 197
//    Period - 80
//    PredictedThreesCount - 9591
//    PredictedFoursCount - 8911
//    PredictedFivesCount - 6665
//    PredictedSixesCount - 141
    // previous algo produces result of 203
    private companion object {
        const val PATTERN_DEFAULT_VALUE = -1f

        // 2 drawings per week x 4 weeks = 1 month
        const val PERIOD = 32
    }
}