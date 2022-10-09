package data

import model.TotoType

class TotoPredict(
    private val totoType: TotoType
) {

    val nextLowHighPattern = FloatArray(totoType.drawingSize)
    val nextOddEvenPattern = FloatArray(totoType.drawingSize)
    val nextGroupPattern = FloatArray(totoType.drawingSize)

    init {
        for (i in 0 until totoType.drawingSize) {
            nextLowHighPattern[i] = 0.5f
            nextOddEvenPattern[i] = 0.5f
            nextGroupPattern[i] = 2f
        }
    }

    fun addLowHighPattern(pattern: IntArray) {
        if (pattern.size != nextLowHighPattern.size ||
            pattern.size != totoType.drawingSize
        ) {
            throw IllegalArgumentException("Something wrong with low high pattern's size $pattern!")
        }

        for (i in 0 until totoType.drawingSize) {
            if (pattern[i] == 0 && nextLowHighPattern[i] <= 0.01563f) {
                continue
            }

            nextLowHighPattern[i] = (nextLowHighPattern[i] + pattern[i]).div(2)
        }
    }

    fun addOddEvenPattern(pattern: IntArray) {
        if (pattern.size != nextOddEvenPattern.size ||
            pattern.size != totoType.drawingSize
        ) {
            throw IllegalArgumentException("Something wrong with odd event pattern's size $pattern!")
        }

        for (i in 0 until totoType.drawingSize) {
            if (pattern[i] == 0 && nextOddEvenPattern[i] <= 0.01563f) {
                continue
            }

            nextOddEvenPattern[i] = (nextOddEvenPattern[i] + pattern[i]).div(2)
        }
    }

    fun addGroupPattern(pattern: IntArray) {
        if (pattern.size != nextGroupPattern.size ||
            pattern.size != totoType.drawingSize
        ) {
            throw IllegalArgumentException("Something wrong with group pattern's size $pattern!")
        }

        for (i in 0 until totoType.drawingSize) {
            if (pattern[i] == 0 && nextGroupPattern[i] <= 0.01563f) {
                continue
            }

            nextGroupPattern[i] = (nextGroupPattern[i] + pattern[i]).div(2)
        }
    }
}