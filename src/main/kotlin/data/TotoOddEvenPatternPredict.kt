package data

import model.TotoType
import kotlin.math.roundToInt

class TotoOddEvenPatternPredict(
    private val totoType: TotoType,
    private val correctPatternUpwards: Float = CORRECT_UPWARDS_VALUE,
    private val correctPatternDownwards: Float = CORRECT_DOWNWARDS_VALUE,
) {

    val nextOddEvenPattern = FloatArray(totoType.drawingSize)

    //var correctlyPredictedIndex: Int = 0
    //var correctlyPredictedPattern: Int = 0

    init {
        for (i in 0 until totoType.drawingSize) {
            nextOddEvenPattern[i] = PATTERN_DEFAULT_VALUE
        }
    }

    fun handleNextOddEvenPattern(pattern: IntArray, drawingIndex: Int) {
        // check if the our next odd even pattern is actually the next pattern
        // correct if any of the indexes is not correct
        // re-calculate our next odd even pattern

        if (pattern.size != totoType.drawingSize)
            throw IllegalArgumentException("There is something wrong with the odd even pattern!  ")

        // Handle first pattern ever
        if (nextOddEvenPattern.all { index -> index == PATTERN_DEFAULT_VALUE }) {
            pattern.forEachIndexed { index, value ->
                nextOddEvenPattern[index] = value.toFloat()
            }
            return
        }

        // Handle second pattern
        if (drawingIndex == 2) {
            pattern.forEachIndexed { index, value ->
                nextOddEvenPattern[index] = (nextOddEvenPattern[index] + value).div(drawingIndex).roundToInt().toFloat()
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
            correctlyPredictedPattern++
        }
        */

        // Check and correct whether our next pattern is the one we are getting as a parameter
        pattern.forEachIndexed { index, value ->
            when {
                // We want 1 but we are predicting 0
                value > nextOddEvenPattern[index].roundToInt() -> {
                    nextOddEvenPattern[index] = nextOddEvenPattern[index] + correctPatternUpwards
                    if (nextOddEvenPattern[index] > 1) {
                        nextOddEvenPattern[index] = 1f
                    }
                }
                // We want 0 but we are predicting 1
                value < nextOddEvenPattern[index].roundToInt() -> {
                    nextOddEvenPattern[index] = nextOddEvenPattern[index] - correctPatternDownwards
                    if (nextOddEvenPattern[index] < 0) {
                        nextOddEvenPattern[index] = 0f
                    }
                }
                else -> {
                    // Value correctly predicted
                    //correctlyPredictedIndex++
                }
            }

            nextOddEvenPattern[index] = ((nextOddEvenPattern[index] * (drawingIndex - 1)) + value).div(drawingIndex)
        }
    }

    fun normalizePrediction() {
        nextOddEvenPattern.forEachIndexed { index, value ->
            nextOddEvenPattern[index] = nextOddEvenPattern[index].roundToInt().toFloat()
        }
    }

    private companion object {
        const val PATTERN_DEFAULT_VALUE = -1f
        const val CORRECT_UPWARDS_VALUE = 0.4f
        const val CORRECT_DOWNWARDS_VALUE = 0.2f
    }
}