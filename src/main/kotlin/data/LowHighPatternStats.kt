package data

import extensions.greaterOrEqual
import extensions.sortByValueDescending
import model.Frequency
import model.Numbers
import model.TotoType
import util.PatternUtils.convertToLowHighPattern
import util.PatternUtils.didPatternOccurMoreThanAverage

/**
 * Holds information about:
 * - occurrences of low/high patterns for each toto drawing
 * - the spacing between issues when a particular pattern has occurred, via the [Frequency] data class
 *
 * A low/high pattern for a toto drawing will look like this:
 * 0 - <= 25, 1 - > 25 (when the toto type is 6x49)
 * 5,14,22,25,34,49 -> 0,0,0,0,1,1
 */
class LowHighPatternStats(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val predict: PredictLowHighPattern,
    private val fromYear: Int? = null
) {

    val patterns: Map<Numbers, Int>
        get() = patternsCache
    private val patternsCache = mutableMapOf<Numbers, Int>()

    val frequencies: Map<Numbers, List<Frequency>>
        get() = frequenciesCache
    private val frequenciesCache = mutableMapOf<Numbers, MutableList<Frequency>>()

    fun calculateStats() {
        val drawings = if (fromYear == null) drawings.drawings else drawings.drawingsSubset
        val lastPatternOccurrenceMap = mutableMapOf<Numbers, Int>()

        drawings.forEachIndexed { index, numbers ->
            // Already got the toto numbers of a single drawing
            val pattern = Numbers(convertToLowHighPattern(numbers.numbers.copyOf(), totoType.lowHighMidPoint))

            predict.handleNextPattern(
                pattern.numbers,
                index,
                didPatternOccurMoreThanAverage(patternsCache, pattern)
            )

            // Save the pattern in the map
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

        predict.normalizePrediction()

        validateOccurrences()
        validateFrequencies()

        sortOccurrences()
        sortFrequencies()
    }

    private fun validateOccurrences() {
        // Size of the toto numbers should be the same as the total sum of the patterns
        val patternsSize = patternsCache.values.sum()
        val drawingsSize = drawings.numbers.count {
            it.position == 0 && it.year.greaterOrEqual(fromYear, true)
        }
        if (patternsSize != drawingsSize)
            throw IllegalArgumentException("Pattern size is incorrect!")

        // No low/high pattern should have an occurrence of 0
        if (patternsCache.values.any { it == 0 })
            throw IllegalArgumentException("Invalid pattern occurrence value!")

        // All low/high patterns should contain 0 or 1 values
        val anyInvalidPatterns = patternsCache.keys.any { lowHighPattern ->
            lowHighPattern.numbers.any { arrayItem -> arrayItem != 0 && arrayItem != 1 }
        }
        if (anyInvalidPatterns)
            throw IllegalArgumentException("Invalid low/high pattern!")
    }

    private fun validateFrequencies() {
        val countOfSingleOccurredPatterns = patternsCache.count { it.value == 1 }
        if (patternsCache.size != frequenciesCache.size + countOfSingleOccurredPatterns)
            throw IllegalArgumentException("Patterns size and frequencies sizes do not match!")

        // The number of occurrences should be the same as the total sum of the frequencies plus 1
        patternsCache.forEach { (pattern, occurrence) ->
            val totalFrequencyCount: Int = frequenciesCache[pattern]?.sumOf { it.count } ?: 0

            if (totalFrequencyCount + 1 != occurrence)
                throw IllegalArgumentException("Occurrence and frequencies for $pattern do not match!")
        }
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
}