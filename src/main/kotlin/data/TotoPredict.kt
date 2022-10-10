package data

import model.TotoType
import kotlin.math.roundToInt

class TotoPredict(
    private val totoType: TotoType
) {

    val nextOddEvenPattern = FloatArray(totoType.drawingSize)

    var correctlyPredicted: Int = 0

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

        // Check and correct whether our next pattern is the one we are getting as a parameter
        pattern.forEachIndexed { index, value ->
            when {
                // We want 1 but we are predicting 0
                value > nextOddEvenPattern[index].roundToInt() -> {
                    nextOddEvenPattern[index] = nextOddEvenPattern[index] + 0.2f
                }
                // We want 0 but we are predicting 1
                value < nextOddEvenPattern[index].roundToInt() -> {
                    nextOddEvenPattern[index] = nextOddEvenPattern[index] - 0.2f
                }
                else -> {
                    // Value correctly predicted
                    correctlyPredicted++
                }
            }

            nextOddEvenPattern[index] = ((nextOddEvenPattern[index] * (drawingIndex - 1)) + value).div(drawingIndex)
        }
    }

    private companion object {
        const val PATTERN_DEFAULT_VALUE = -1f
    }
}