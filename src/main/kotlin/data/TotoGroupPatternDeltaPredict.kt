package data

import model.TotoType
import kotlin.math.roundToInt

class TotoGroupPatternDeltaPredict(
    private val totoType: TotoType,
    private val correctPatternUpwards: Float = CORRECT_UPWARDS_VALUE,
    private val correctPatternDownwards: Float = CORRECT_DOWNWARDS_VALUE
) {

    val nextGroupPattern = FloatArray(totoType.drawingSize)

//    var correctlyPredictedPatternPart: Int = 0
//    var correctlyPredictedPatternFull: Int = 0

    init {
        for (i in 0 until totoType.drawingSize) {
            nextGroupPattern[i] = PATTERN_DEFAULT_VALUE
        }
    }

    fun handleNextGroupDeltaPattern(pattern: IntArray, drawingIndex: Int) {
        if (pattern.size != totoType.drawingSize)
            throw IllegalArgumentException("There is something wrong with the group pattern!")

        // Handle first pattern ever
        if (nextGroupPattern.all { index -> index == PATTERN_DEFAULT_VALUE }) {
            pattern.forEachIndexed { index, value ->
                nextGroupPattern[index] = value.toFloat()
            }
            return
        }

        /*
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
        */

        // Handle subsequent patterns
        pattern.forEachIndexed { index, value ->
            when {
                // We are getting a number that is higher than the prediction
                value > nextGroupPattern[index].roundToInt() -> {
                    nextGroupPattern[index] = nextGroupPattern[index] + correctPatternUpwards
                }
                // We are getting a number that is lower than the prediction
                value < nextGroupPattern[index].roundToInt() -> {
                    nextGroupPattern[index] = nextGroupPattern[index] - correctPatternDownwards
                    if (nextGroupPattern[index] < 0.5f) {
                        nextGroupPattern[index] = 0.5f
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