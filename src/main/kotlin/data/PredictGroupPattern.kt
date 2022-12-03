package data

import model.TotoType
import kotlin.math.roundToInt

class PredictGroupPattern(
    private val totoType: TotoType,
    private val correctPatternUpwards: Float = CORRECT_UPWARDS_VALUE,
    private val correctPatternDownwards: Float = CORRECT_DOWNWARDS_VALUE,
    private val averageDivisorUpwards: Int = AVERAGE_DIVISOR_UPWARDS_VALUE,
    private val averageDivisorDownwards: Int = AVERAGE_DIVISOR_DOWNWARDS_VALUE
) {

    val nextPattern = FloatArray(totoType.size)

//    var correctlyPredictedIndexCount: Int = 0
//    var correctlyPredictedCount: Int = 0

    init {
        for (i in 0 until totoType.size) {
            nextPattern[i] = PATTERN_DEFAULT_VALUE
        }
    }

    /**
     * Check if the next group pattern is actually the next pattern.
     * Correct if any of the indexes are not correct.
     * Re-calculate our next group pattern.
     */
    fun handleNextPattern(pattern: IntArray, drawingIndex: Int, didOccurMoreThanAverage: Boolean) {
        if (pattern.size != totoType.size)
            throw IllegalArgumentException("There is something wrong with the group pattern!")

        // Handle first pattern
        if (nextPattern.all { index -> index == PATTERN_DEFAULT_VALUE }) {
            pattern.forEachIndexed { index, value ->
                nextPattern[index] = value.toFloat()
            }
            return
        }

        /*
        var didCorrectlyPredictPattern = true
        nextPattern.map { it.roundToInt() }.forEachIndexed { index, item ->
            if (didCorrectlyPredictPattern.not()) {
                return@forEachIndexed
            }
            if (item != pattern[index]) {
                didCorrectlyPredictPattern = false
                return@forEachIndexed
            }
        }
        if (didCorrectlyPredictPattern) {
            correctlyPredictedCount++
        }
        */

        // Check and correct whether our next pattern is the one we are getting as a parameter
        pattern.forEachIndexed { index, value ->
            when {
                // We are getting a number that is higher than the prediction
                value > nextPattern[index].roundToInt() -> {
                    nextPattern[index] = nextPattern[index] + if (didOccurMoreThanAverage)
                        correctPatternUpwards
                    else
                        correctPatternUpwards / averageDivisorUpwards

                    // Correct for too high values
                    // TODO Assuming we are using DIVIDE_BY_10
                    if (nextPattern[index] > 4.49) {
                        nextPattern[index] = 4.49f
                    }
                }
                // We are getting a number that is lower than the prediction
                value < nextPattern[index].roundToInt() -> {
                    nextPattern[index] = nextPattern[index] - if (didOccurMoreThanAverage)
                        correctPatternDownwards
                    else
                        correctPatternDownwards / averageDivisorDownwards

                    // Correct for negative values
                    // TODO Assuming we are using DIVIDE_BY_10
                    if (nextPattern[index] < 0) {
                        nextPattern[index] = 0f
                    }
                }

                else -> {
                    // Value correctly predicted
//                    correctlyPredictedIndexCount++
                }
            }

            nextPattern[index] = ((nextPattern[index] * (drawingIndex - 1)) + value).div(drawingIndex)
        }
    }

    fun normalizePrediction() {
        nextPattern.forEachIndexed { index, value ->
            nextPattern[index] = value.roundToInt().toFloat()
        }
    }

    /**
     * These values managed to predict 214 group patterns correctly.
     */
    private companion object {
        const val PATTERN_DEFAULT_VALUE = -1f
        const val CORRECT_UPWARDS_VALUE = 0.4f
        const val CORRECT_DOWNWARDS_VALUE = 0.3f
        const val AVERAGE_DIVISOR_UPWARDS_VALUE = 2
        const val AVERAGE_DIVISOR_DOWNWARDS_VALUE = 6
    }
}