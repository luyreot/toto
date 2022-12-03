package util

import model.TotoNumbers

object PatternUtils {

    fun convertTotoNumbersToGroupPattern(
        numbers: IntArray,
        groupStrategyMethod: ((Int) -> Int)?,
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = groupStrategyMethod?.invoke(numbers[i])
                ?: throw IllegalArgumentException("Group strategy method is null!")
        }

        return numbers
    }

    fun convertTotoNumbersToLowHighPattern(
        numbers: IntArray,
        lowHighMidPoint: Int
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = if (numbers[i] <= lowHighMidPoint) 0 else 1
        }

        return numbers
    }

    fun convertTotoNumbersToOddEvenPattern(
        numbers: IntArray
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = if ((numbers[i] and 1) == 0) 1 else 0
        }

        return numbers
    }

    fun didPatternOccurMoreThanAverage(
        patternsCache: Map<TotoNumbers, Int>,
        pattern: TotoNumbers
    ): Boolean =
        patternsCache[pattern]?.let { it > patternsCache.values.sum() / patternsCache.size } ?: false
}