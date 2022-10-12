package data

import model.TotoType
import kotlin.math.roundToInt

class TotoGroupPatternPredict(
    private val totoType: TotoType,
    private val correctPatternUpwards: Float = CORRECT_UPWARDS_VALUE,
    private val correctPatternDownwards: Float = CORRECT_DOWNWARDS_VALUE,
) {

    val nextGroupPattern = FloatArray(totoType.drawingSize)

    var correctlyPredictedPatternPart: Int = 0
    var correctlyPredictedPatternFull: Int = 0

    init {
        for (i in 0 until totoType.drawingSize) {
            nextGroupPattern[i] = PATTERN_DEFAULT_VALUE
        }
    }

    /**
     * Check if the our next group pattern is actually the next pattern.
     * Correct if any of the indexes are not correct.
     * Re-calculate our next group pattern.
     */
    fun handleNextGroupPattern(pattern: IntArray, drawingIndex: Int) {
        if (pattern.size != totoType.drawingSize)
            throw IllegalArgumentException("There is something wrong with the group pattern!")

        // Handle first pattern ever
        if (nextGroupPattern.all { index -> index == PATTERN_DEFAULT_VALUE }) {
            pattern.forEachIndexed { index, value ->
                nextGroupPattern[index] = value.toFloat()
            }
            return
        }

        // Handle second pattern
        if (drawingIndex == 2) {
            pattern.forEachIndexed { index, value ->
                nextGroupPattern[index] = (nextGroupPattern[index] + value).div(drawingIndex).roundToInt().toFloat()
            }
            return
        }

//        /*
        var didCorrectlyPredictPattern = true
        nextGroupPattern.map { it.roundToInt() }.forEachIndexed { index, item ->
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
//        */

        // Check and correct whether our next pattern is the one we are getting as a parameter
        pattern.forEachIndexed { index, value ->
            when {
                // We want 1 but we are predicting 0
                value > nextGroupPattern[index].roundToInt() -> {
                    nextGroupPattern[index] = nextGroupPattern[index] + correctPatternUpwards
//                    if (nextGroupPattern[index] > 1) {
//                        nextGroupPattern[index] = 1f
//                    }
                }
                // We want 0 but we are predicting 1
                value < nextGroupPattern[index].roundToInt() -> {
                    nextGroupPattern[index] = nextGroupPattern[index] - correctPatternDownwards
//                    if (nextGroupPattern[index] < 0) {
//                        nextGroupPattern[index] = 0f
//                    }
                }
                else -> {
                    // Value correctly predicted
                    correctlyPredictedPatternPart++
                }
            }

            nextGroupPattern[index] = ((nextGroupPattern[index] * (drawingIndex - 1)) + value).div(drawingIndex)
        }
    }

    fun normalizePrediction() {
        nextGroupPattern.forEachIndexed { index, value ->
            nextGroupPattern[index] = value.roundToInt().toFloat()
        }
    }

    private companion object {
        const val PATTERN_DEFAULT_VALUE = -1f
        const val CORRECT_UPWARDS_VALUE = 0f
        const val CORRECT_DOWNWARDS_VALUE = 0f
    }
}