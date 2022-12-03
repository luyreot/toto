package util

import model.Numbers

object PatternUtils {

    fun convertToGroupPattern(
        numbers: IntArray,
        groupStrategyMethod: ((Int) -> Int)?,
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = groupStrategyMethod?.invoke(numbers[i])
                ?: throw IllegalArgumentException("Group strategy method is null!")
        }

        return numbers
    }

    fun convertToLowHighPattern(
        numbers: IntArray,
        lowHighMidPoint: Int
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = if (numbers[i] <= lowHighMidPoint) 0 else 1
        }

        return numbers
    }

    fun convertOddEvenPattern(
        numbers: IntArray
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = if ((numbers[i] and 1) == 0) 1 else 0
        }

        return numbers
    }

    fun didPatternOccurMoreThanAverage(
        patternsCache: Map<Numbers, Int>,
        pattern: Numbers,
    ): Boolean = patternsCache[pattern]?.let { it > patternsCache.values.sum() / patternsCache.size } ?: false

    fun convertToGroupPatternDelta(
        numbers: IntArray,
        groupStrategyMethod: ((Int, Int) -> Int)?,
    ): IntArray {
        val result = IntArray(numbers.size)

        for (i in numbers.indices) {
            if (i == 0) {
                result[i] = numbers[i]
                continue
            }

            result[i] = groupStrategyMethod?.invoke(numbers[i], numbers[i - 1])
                ?: throw IllegalArgumentException("Group strategy method is null!")
        }

        return result
    }

    fun unconvertGroupPatternDelta(
        drawingSize: Int,
        pattern: IntArray
    ): IntArray {
        val drawing = IntArray(drawingSize)

        for (i in 0 until drawingSize) {
            if (i == 0) {
                drawing[i] = pattern[i]
                continue
            }
            drawing[i] = drawing[i - 1] + pattern[i]
        }

        return drawing
    }
}