package data

import model.TotoType
import kotlin.math.roundToInt

class TotoGroupPatternDeltaPredict(
    private val totoType: TotoType,
    private val correctPatternUpwards: Float = CORRECT_UPWARDS_VALUE,
    private val correctPatternDownwards: Float = CORRECT_DOWNWARDS_VALUE
) {

    val nextGroupPattern = FloatArray(totoType.drawingSize)
    val nextUnwrappedGroupPattern = FloatArray(totoType.drawingSize)

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
                    // Cannot be greater than 49, because this would mean that will exceed the possible number values.
                    if (nextGroupPattern[index] >= 48.45f) {
                        nextGroupPattern[index] = 48.49f
                    }
                }
                // We are getting a number that is lower than the prediction
                value < nextGroupPattern[index].roundToInt() -> {
                    nextGroupPattern[index] = nextGroupPattern[index] - correctPatternDownwards
                    // Cannot be less than 1. We will round to 1.
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

    // TODO create as a TotoGroupStrategy
    fun unwrapPattern() {
        for (i in 0 until totoType.drawingSize) {
            if (i == 0) {
                nextUnwrappedGroupPattern[i] = nextGroupPattern[i]
                continue
            }
            nextUnwrappedGroupPattern[i] = nextUnwrappedGroupPattern[i - 1] + nextGroupPattern[i]
        }

    }

    /**
     * BestPattern(up=1.3000002, down=1.5000002, highestPart=7125, highestFull=1)
     * BestPattern(up=3.0999992, down=4.9999976, highestPart=7125, highestFull=1)
     * BestPattern(up=3.0999992, down=5.0999975, highestPart=7125, highestFull=1)
     * BestPattern(up=3.0999992, down=5.1999974, highestPart=7125, highestFull=1)
     * BestPattern(up=3.199999, down=5.2999973, highestPart=7125, highestFull=1)
     * BestPattern(up=3.199999, down=5.399997, highestPart=7125, highestFull=1)
     * BestPattern(up=3.299999, down=5.599997, highestPart=7125, highestFull=1)
     */
    private companion object {
        const val PATTERN_DEFAULT_VALUE = -1f
        const val CORRECT_UPWARDS_VALUE = 3.1f
        const val CORRECT_DOWNWARDS_VALUE = 5.1f
    }
}