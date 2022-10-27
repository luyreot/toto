package data

import model.TotoType
import kotlin.math.roundToInt

class TotoLowHighPatternPredict(
    private val totoType: TotoType,
    private val correctPatternUpwards: Float = CORRECT_UPWARDS_VALUE,
    private val correctPatternDownwards: Float = CORRECT_DOWNWARDS_VALUE,
    private val averageDivisorUpwards: Int = AVERAGE_DIVISOR_UPWARDS_VALUE,
    private val averageDivisorDownwards: Int = AVERAGE_DIVISOR_DOWNWARDS_VALUE
) {

    val nextLowHighPattern = FloatArray(totoType.drawingSize)

//    var correctlyPredictedPatternPart: Int = 0
//    var correctlyPredictedPatternFull: Int = 0

    init {
        for (i in 0 until totoType.drawingSize) {
            nextLowHighPattern[i] = PATTERN_DEFAULT_VALUE
        }
    }

    /**
     * Check if the next low high pattern is actually the next pattern.
     * Correct if any of the indexes are not correct.
     * Re-calculate our next low high pattern.
     */
    fun handleNextLowHighPattern(pattern: IntArray, drawingIndex: Int, occurredMoreThanAverage: Boolean) {
        if (pattern.size != totoType.drawingSize)
            throw IllegalArgumentException("There is something wrong with the low high pattern!")

        /*
        var didCorrectlyPredictPattern = true
        nextLowHighPattern.map { it.roundToInt() }.forEachIndexed { index, item ->
            if (didCorrectlyPredictPattern.not()) {
                return@forEachIndexed
            }
            if (item != pattern[index]) {
                didCorrectlyPredictPattern = false
                return@forEachIndexed
            }
        }
        if (didCorrectlyPredictPattern) {
            correctlyPredictedPatternFull++
        }
        */

        // Check and correct whether our next pattern is the one we are getting as a parameter
        pattern.forEachIndexed { index, value ->
            when {
                // We are getting 1 but we are predicting 0
                value > nextLowHighPattern[index].roundToInt() -> {
                    nextLowHighPattern[index] = nextLowHighPattern[index] + if (occurredMoreThanAverage)
                        correctPatternUpwards
                    else
                        correctPatternUpwards / averageDivisorUpwards

                    if (nextLowHighPattern[index] > 1.49f) {
                        nextLowHighPattern[index] = 1.49f
                    }
                }
                // We are getting 0 but we are predicting 1
                value < nextLowHighPattern[index].roundToInt() -> {
                    nextLowHighPattern[index] = nextLowHighPattern[index] - if (occurredMoreThanAverage)
                        correctPatternDownwards
                    else
                        correctPatternDownwards / averageDivisorDownwards

                    if (nextLowHighPattern[index] < 0f) {
                        nextLowHighPattern[index] = 0f
                    }
                }
                else -> {
                    // Value correctly predicted
//                    correctlyPredictedPatternPart++
                }
            }
        }
    }

    fun normalizePrediction() {
        nextLowHighPattern.forEachIndexed { index, value ->
            nextLowHighPattern[index] = value.roundToInt().toFloat()
        }
    }

    private companion object {
        const val PATTERN_DEFAULT_VALUE = -1f
        const val CORRECT_UPWARDS_VALUE = 0.7f
        const val CORRECT_DOWNWARDS_VALUE = 0.6f
        const val AVERAGE_DIVISOR_UPWARDS_VALUE = 11
        const val AVERAGE_DIVISOR_DOWNWARDS_VALUE = 10
    }
}