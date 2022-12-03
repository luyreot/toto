package data

import extensions.sortByValueDescending
import model.*
import util.PatternUtils.convertToGroupPatternDelta

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
class GroupPatternDeltaStats(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val fromYear: Int? = null
) {

    val patterns: Map<Numbers, Int>
        get() = patternsCache
    private val patternsCache = mutableMapOf<Numbers, Int>()

    val patternsGrouped: Map<Int, Map<Numbers, Int>>
        get() = patternsGroupedCache
    private val patternsGroupedCache = mutableMapOf<Int, MutableMap<Numbers, Int>>()

    val frequencies: Map<Numbers, List<Frequency>>
        get() = frequenciesCache
    private val frequenciesCache = mutableMapOf<Numbers, MutableList<Frequency>>()

    val averageDeltaPatterns: Map<Int, NumbersFloat>
        get() = averageDeltaPatternsCache
    private val averageDeltaPatternsCache = mutableMapOf<Int, NumbersFloat>()

    private val groupStrategyMethod = groupStrategies[GroupStrategy.DELTA_SUBTRACT] as? (Int, Int) -> Int

    init {
        if (groupStrategyMethod == null)
            throw IllegalArgumentException("Group strategy method is null!")
    }

    fun calculateStats() {
        val drawings = if (fromYear == null) drawings.drawings else drawings.drawingsSubset
        val lastPatternOccurrenceMap = mutableMapOf<Numbers, Int>()

        drawings.forEachIndexed { index, totoNumbers ->
            val pattern = Numbers(convertToGroupPatternDelta(totoNumbers.numbers.copyOf(), groupStrategyMethod))

            patternsCache.merge(pattern, 1, Int::plus)

            // Frequencies

            if (lastPatternOccurrenceMap.containsKey(pattern).not()) {
                lastPatternOccurrenceMap[pattern] = index
                return@forEachIndexed
            }

            lastPatternOccurrenceMap[pattern]?.let { lastDrawingIndex ->
                val newFrequency = index - lastDrawingIndex

                lastPatternOccurrenceMap[pattern] = index

                if (frequenciesCache.containsKey(pattern).not()) {
                    frequenciesCache[pattern] = mutableListOf(Frequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val doesNewFrequencyExist: Boolean = frequenciesCache[pattern]
                    ?.any { it.frequency == newFrequency }
                    ?: false
                if (doesNewFrequencyExist.not()) {
                    frequenciesCache[pattern]?.add(Frequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val frequencyIndex: Int = frequenciesCache[pattern]
                    ?.indexOfFirst { it.frequency == newFrequency }
                    ?: -1
                if (frequencyIndex == -1) {
                    frequenciesCache[pattern]?.add(Frequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val frequency: Frequency? = frequenciesCache[pattern]?.get(frequencyIndex)
                if (frequency == null) {
                    frequenciesCache[pattern]?.add(Frequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                frequenciesCache[pattern]?.set(
                    frequencyIndex,
                    frequency.copy(count = frequency.count + 1)
                )
            }
        }

        sortOccurrences()
        sortFrequencies()

        groupPatterns()
        averagePatterns()
    }

    /**
     * Sort by how ofter the pattern has appeared.
     */
    private fun sortOccurrences() {
        patternsCache.sortByValueDescending()
    }

    /**
     * Sort by the same sort order that is used for the [patternsCache].
     * See [sortOccurrences].
     */
    private fun sortFrequencies() {
        val sortedOccurrences = patternsCache.keys.toList()
        val sortedFrequencies = mutableMapOf<Numbers, MutableList<Frequency>>()

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
        patternsCache.map { it.key.numbers[0] }.toSet().forEach Pattern@{ mapGroupKey ->
            // Iterate over all patterns and filter for ones that start with that KEY
            val patternsList = patternsCache.keys.filter { it.numbers[0] == mapGroupKey }
            // Iterate over that subset
            patternsList.forEach PatternList@{ pattern ->
                // Small defensive coding if the pattern does not start with the KEY
                if (pattern.numbers[0] != mapGroupKey) return@PatternList

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

        patternsGroupedCache.forEach { (_, patterns) ->
            val sortedPatterns = patterns.toList().sortedBy { it.first }
            patterns.clear()
            patterns.putAll(sortedPatterns)
        }
    }

    // TODO: Can produce an existing delta pattern
    // TODO: Hardcoded for 6x49 -> Optimize
    private fun averagePatterns() {
        patternsGroupedCache.forEach { (number, patterns) ->
            averageDeltaPatternsCache[number] = NumbersFloat(
                floatArrayOf(
                    number.toFloat(),
                    patterns.map { it.key.numbers[1] }.sum().toFloat().div(patterns.size),
                    patterns.map { it.key.numbers[2] }.sum().toFloat().div(patterns.size),
                    patterns.map { it.key.numbers[3] }.sum().toFloat().div(patterns.size),
                    patterns.map { it.key.numbers[4] }.sum().toFloat().div(patterns.size),
                    patterns.map { it.key.numbers[5] }.sum().toFloat().div(patterns.size)
                )
            )
        }
    }
}