package util

import model.Numbers
import model.TotoType

object PatternUtils {

    fun convertToGroupPattern(
        numbers: IntArray,
        groupStrategyMethod: (Int) -> Int,
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = groupStrategyMethod.invoke(numbers[i])
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

    fun anyInvalidPatterns(
        totoType: TotoType,
        patterns: Set<Numbers>
    ): Boolean = patterns.any { pattern ->
        pattern.numbers.any { num ->
            when (totoType) {
                TotoType.T_6X49 -> num < 0 || num > 4
                TotoType.T_6X42 -> num < 0 || num > 5
                TotoType.T_5X35 -> num < 0 || num > 4
            }
        }
    }
}