package data

import extensions.sortByValueDescending
import model.*

/**
 * Alternative way of handling group patterns for drawings.
 *
 * Given a drawing, perform the following algorithm:
 * 1. sort the drawing in ascending order
 * 2. take the first / lowest number as the first result in the output
 * 3. divide subsequent numbers by the previous one in the array
 * 4. use the result in the output
 *
 * To reverse the output to get the original drawing, perform the following algorithm:
 * 1. take the first number as the first result in the output
 * 2. take the second number and add it to the result from step 1
 * 3. use the product from step 2 as the next result in the output
 * 4. perform addition of each subsequent number with the previous product result
 * 5. use each new product in the output
 *
 * Here is an example:
 *
 * Original drawing:
 * 4, 6, 9, 21, 36, 46
 *
 * Delta algorithm operation forward:
 * 4
 * 6 - 4 = 2
 * 9 - 6 = 3
 * 21 - 9 = 12
 * 36 - 21 = 15
 * 46 - 36 = 10
 *
 * Output:
 * 4, 2, 3, 12, 15, 10
 *
 * Delta algorithm reversed (backwards) operation:
 * 4
 * 4 + 2 = 6
 * 6 + 3 = 9
 * 9 + 12 = 21
 * 21 + 15 = 36
 * 36 + 10 = 46
 *
 * Output / Original drawing:
 * 4, 6, 9, 21, 36, 46
 *
 * This algorithm serves the purpose of lowering the total possible numbers that can occur in a drawing.
 * In the case of 6/49 instead of having a total of 49 different numbers we end up with around 15.
 */
class TotoGroupPatternDeltaStats(
    private val totoType: TotoType,
    private val totoNumbers: TotoDrawnNumbers,
    private val totoPredict: TotoGroupPatternDeltaPredict,
    private val fromYear: Int? = null
) {

    val patterns: Map<TotoNumbers, Int>
        get() = patternsCache

    private val patternsCache = mutableMapOf<TotoNumbers, Int>()

    val patternsGrouped: Map<Int, Map<TotoNumbers, Int>>
        get() = patternsGroupedCache

    private val patternsGroupedCache = mutableMapOf<Int, MutableMap<TotoNumbers, Int>>()

    val frequencies: Map<TotoNumbers, List<TotoFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<TotoNumbers, MutableList<TotoFrequency>>()

    private val groupStrategyMethod = groupStrategies[TotoGroupStrategy.DELTA_SUBTRACT] as? (Int, Int) -> Int

    init {
        if (groupStrategyMethod == null)
            throw IllegalArgumentException("Group strategy method is null!")
    }

    fun calculateTotoGroupPatternDeltaStats() {
        val drawings = if (fromYear == null) totoNumbers.allDrawings else totoNumbers.drawingsSubset
        val lastTotoPatternOccurrenceMap = mutableMapOf<TotoNumbers, Int>()

        drawings.forEachIndexed { index, totoNumbers ->
            val groupPattern = TotoNumbers(convertTotoNumbersToGroupPatternDelta(totoNumbers.numbers.copyOf()))

            totoPredict.handleNextGroupDeltaPattern(groupPattern.numbers, index)

            patternsCache.merge(groupPattern, 1, Int::plus)

            // Frequencies

            if (lastTotoPatternOccurrenceMap.containsKey(groupPattern).not()) {
                lastTotoPatternOccurrenceMap[groupPattern] = index
                return@forEachIndexed
            }

            lastTotoPatternOccurrenceMap[groupPattern]?.let { lastDrawingIndex ->
                val newFrequency = index - lastDrawingIndex

                lastTotoPatternOccurrenceMap[groupPattern] = index

                if (frequenciesCache.containsKey(groupPattern).not()) {
                    frequenciesCache[groupPattern] = mutableListOf(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val doesNewFrequencyExist: Boolean = frequenciesCache[groupPattern]
                    ?.any { it.frequency == newFrequency }
                    ?: false
                if (doesNewFrequencyExist.not()) {
                    frequenciesCache[groupPattern]?.add(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val frequencyIndex: Int = frequenciesCache[groupPattern]
                    ?.indexOfFirst { it.frequency == newFrequency }
                    ?: -1
                if (frequencyIndex == -1) {
                    frequenciesCache[groupPattern]?.add(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val totoFrequency: TotoFrequency? = frequenciesCache[groupPattern]?.get(frequencyIndex)
                if (totoFrequency == null) {
                    frequenciesCache[groupPattern]?.add(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                frequenciesCache[groupPattern]?.set(
                    frequencyIndex,
                    totoFrequency.copy(count = totoFrequency.count + 1)
                )
            }
        }

        totoPredict.normalizePrediction()
        totoPredict.unwrapPattern()

        sortTotoGroupPatternOccurrences()
        sortTotoGroupPatternFrequencies()

        groupPatterns()
    }

    fun convertTotoNumbersToGroupPatternDelta(
        numbers: IntArray
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

    /**
     * Sort by how ofter the pattern has appeared.
     */
    private fun sortTotoGroupPatternOccurrences() {
        patternsCache.sortByValueDescending()
    }

    /**
     * Sort by the same sort order that is used for the [patternsCache].
     * See [sortTotoGroupPatternOccurrences].
     */
    private fun sortTotoGroupPatternFrequencies() {
        val sortedOccurrences = patternsCache.keys.toList()
        val sortedFrequencies = mutableMapOf<TotoNumbers, MutableList<TotoFrequency>>()

        sortedOccurrences.forEach { pattern ->
            frequenciesCache[pattern]?.let { frequencies ->
                sortedFrequencies[pattern] = frequencies
            }
        }

        frequenciesCache.apply {
            clear()
            putAll(sortedFrequencies)
        }
    }

    private fun groupPatterns() {
        // Iterate over all patterns and create a set from their 0 index value (KEY)
        patternsCache.map { it.key.numbers[0] }.toSet().forEach { mapGroupKey ->
            // Iterate over all patterns and filter for ones that start with that KEY
            val patternsList = patternsCache.keys.filter { it.numbers[0] == mapGroupKey }
            // Iterate over that subset
            patternsList.forEach { pattern ->
                // Small defensive coding if the pattern does not start with the KEY
                if (pattern.numbers[0] != mapGroupKey) return@forEach

                // Create an empty map for the KEY if it does not exist
                if (patternsGroupedCache[mapGroupKey] == null) {
                    patternsGroupedCache[mapGroupKey] = mutableMapOf()
                }

                // Check if the pattern has been already saved against the KEY
                // If the pattern DOES exist, increment its occurrence value OR set to 1
                if (patternsGroupedCache[mapGroupKey]?.get(pattern) != null) {
                    patternsGroupedCache[mapGroupKey]?.apply {
                        val currentOccurrenceValue: Int? = get(pattern)?.plus(1)
                        set(pattern, currentOccurrenceValue ?: 1)
                    }
                } else {
                    // If the pattern DOES NOT exist, add it and set its occurrence value to the
                    // number of occurrences that it already has OR set it to 1
                    patternsGroupedCache[mapGroupKey]?.set(pattern, patternsCache[pattern] ?: 1)
                }
            }
        }

        val sorted = patternsGroupedCache.toList().sortedBy { it.first }.toMap()
        patternsGroupedCache.clear()
        patternsGroupedCache.putAll(sorted)

        patternsGroupedCache.forEach { (number, patterns) ->
            val sortedPatterns = patterns.toList().sortedBy { it.first }
            patterns.clear()
            patterns.putAll(sortedPatterns)
        }
    }
}