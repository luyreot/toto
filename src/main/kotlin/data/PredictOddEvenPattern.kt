package data

import model.TotoType
import kotlin.math.roundToInt

class PredictOddEvenPattern(
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
     * Check if the next odd even pattern is actually the next pattern.
     * Correct if any of the indexes are not correct.
     * Re-calculate our next odd even pattern.
     */
    fun handleNextPattern(pattern: IntArray, drawingIndex: Int, didOccurMoreThanAverage: Boolean) {
        if (pattern.size != totoType.size)
            throw IllegalArgumentException("There is something wrong with the odd even pattern!")

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
                // We are getting 1 but we are predicting 0
                value > nextPattern[index].roundToInt() -> {
                    nextPattern[index] = nextPattern[index] + if (didOccurMoreThanAverage)
                        correctPatternUpwards
                    else
                        correctPatternUpwards / averageDivisorUpwards

                    // Correct for too high values
                    if (nextPattern[index] > 1.49f) {
                        nextPattern[index] = 1f
                    }
                }
                // We are getting 0 but we are predicting 1
                value < nextPattern[index].roundToInt() -> {
                    nextPattern[index] = nextPattern[index] - if (didOccurMoreThanAverage)
                        correctPatternDownwards
                    else
                        correctPatternDownwards / averageDivisorDownwards

                    nextPattern[index] = nextPattern[index] - correctPatternDownwards

                    // Correct for negative values
                    if (nextPattern[index] < 0f) {
                        nextPattern[index] = 0f
                    }
                }

                else -> {
                    // Value correctly predicted
//                    correctlyPredictedIndexCount++
                }
            }
        }
    }

    fun normalizePrediction() {
        nextPattern.forEachIndexed { index, value ->
            nextPattern[index] = value.roundToInt().toFloat()
        }
    }

    private companion object {
        const val PATTERN_DEFAULT_VALUE = -1f
        const val CORRECT_UPWARDS_VALUE = 0.4f
        const val CORRECT_DOWNWARDS_VALUE = 0.1f
        const val AVERAGE_DIVISOR_UPWARDS_VALUE = 5
        const val AVERAGE_DIVISOR_DOWNWARDS_VALUE = 10
    }
}