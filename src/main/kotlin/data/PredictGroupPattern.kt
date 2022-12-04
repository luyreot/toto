package data

import collection.QueueList
import model.TotoType
import kotlin.math.roundToInt

class PredictGroupPattern(
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
            throw IllegalArgumentException("There is something wrong with the group pattern!")

        if (drawingIndex == 0) {
            for (i in averageTrueRangePatternArray.indices) {
                pattern[i].div(1f).let {
                    averageTrueRangePatternArray[i].add(it)
                    nextPattern[i] = it
                }
            }

            return
        }


        // it + 0.3f toInt
        val predictedPattern = nextPattern.map { (it).roundToInt() }.toIntArray()
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

//            atrArray[i].last()?.let { atrValue ->
//                val multiplier = if (atrArray[i].isAtMaxSize()) atrPeriod - 1 else atrArray[i].size
//                val divisor = if (atrArray[i].isAtMaxSize()) atrPeriod else atrArray[i].size + 1
//                val newAtrValue: Float = (atrValue * multiplier + pattern[i]) / divisor
//                atrArray[i].add(newAtrValue)
//                nextPattern[i] = newAtrValue
//        }
        }
    }


    //    round to int, multiply previous atr by current size and then add the new value and divide by size + 1
//    Period - 48
//    CorrectlyPredictedIndexCount - 30544
//    CorrectlyPredictedCount - 283
//    ---
//    Period - 56
//    CorrectlyPredictedIndexCount - 30543
//    CorrectlyPredictedCount - 283
    //
    // round to int, sum previous atrs and then add the new value and divide by size
//    Period - 160
//    CorrectlyPredictedIndexCount - 30232
//    CorrectlyPredictedCount - 289
    private companion object {
        const val PATTERN_DEFAULT_VALUE = 0f

        // 4 weeks (1 month) x 2 drawings x 4 = 32
        const val PERIOD = 32
    }
}