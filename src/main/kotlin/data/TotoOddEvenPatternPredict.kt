package data

import model.TotoType
import kotlin.math.roundToInt

class TotoOddEvenPatternPredict(
    private val totoType: TotoType,
    private val correctPatternUpwards: Float = CORRECT_UPWARDS_VALUE,
    private val correctPatternDownwards: Float = CORRECT_DOWNWARDS_VALUE
) {

    val nextOddEvenPattern = FloatArray(totoType.drawingSize)

//    var correctlyPredictedPatternPart: Int = 0
//    var correctlyPredictedPatternFull: Int = 0

    init {
        for (i in 0 until totoType.drawingSize) {
            nextOddEvenPattern[i] = PATTERN_DEFAULT_VALUE
        }
    }

    /**
     * Check if the our next odd even pattern is actually the next pattern.
     * Correct if any of the indexes are not correct.
     * Re-calculate our next odd even pattern.
     */
    fun handleNextOddEvenPattern(pattern: IntArray, drawingIndex: Int) {
        if (pattern.size != totoType.drawingSize)
            throw IllegalArgumentException("There is something wrong with the odd even pattern!")

        // Handle first pattern ever
        if (nextOddEvenPattern.all { index -> index == PATTERN_DEFAULT_VALUE }) {
            pattern.forEachIndexed { index, value ->
                nextOddEvenPattern[index] = value.toFloat()
            }
            return
        }

        /*
        var didCorrectlyPredictPattern = true
        nextOddEvenPattern.map { it.roundToInt() }.forEachIndexed { index, item ->
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
                value > nextOddEvenPattern[index].roundToInt() -> {
                    nextOddEvenPattern[index] = nextOddEvenPattern[index] + correctPatternUpwards
                    if (nextOddEvenPattern[index] > 1.49f) {
                        nextOddEvenPattern[index] = 1f
                    }
                }
                // We are getting 0 but we are predicting 1
                value < nextOddEvenPattern[index].roundToInt() -> {
                    nextOddEvenPattern[index] = nextOddEvenPattern[index] - correctPatternDownwards
                    if (nextOddEvenPattern[index] < 0f) {
                        nextOddEvenPattern[index] = 0f
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
        nextOddEvenPattern.forEachIndexed { index, value ->
            nextOddEvenPattern[index] = value.roundToInt().toFloat()
        }
    }

    private companion object {
        const val PATTERN_DEFAULT_VALUE = -1f
        const val CORRECT_UPWARDS_VALUE = 0.4f
        const val CORRECT_DOWNWARDS_VALUE = 0.2f
    }
}