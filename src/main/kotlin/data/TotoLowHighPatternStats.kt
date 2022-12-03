package data

import extensions.greaterOrEqual
import extensions.sortByValueDescending
import model.TotoFrequency
import model.TotoNumbers
import model.TotoType

/**
 * Holds information about:
 * - occurrences of low/high patterns for each toto drawing
 * - the spacing between issues when a particular pattern has occurred, via the [TotoFrequency] data class
 *
 * A low/high pattern for a toto drawing will look like this:
 * 0 - <= 25, 1 - > 25 (when the toto type is 6x49)
 * 5,14,22,25,34,49 -> 0,0,0,0,1,1
 */
class TotoLowHighPatternStats(
    private val totoType: TotoType,
    private val totoNumbers: TotoDrawnNumbers,
    private val totoPredict: TotoLowHighPatternPredict,
    private val fromYear: Int? = null
) {

    val patterns: Map<TotoNumbers, Int>
        get() = patternsCache

    private val patternsCache = mutableMapOf<TotoNumbers, Int>()

    val frequencies: Map<TotoNumbers, List<TotoFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<TotoNumbers, MutableList<TotoFrequency>>()

    fun calculateTotoLowHighPatternStats() {
        val drawings = if (fromYear == null) totoNumbers.allDrawings else totoNumbers.drawingsSubset
        val lastTotoPatternOccurrenceMap = mutableMapOf<TotoNumbers, Int>()

        drawings.forEachIndexed { index, totoNumbers ->
            // Already got the toto numbers of a single drawing
            val lowHighPattern = TotoNumbers(convertTotoNumbersToLowHighPattern(totoNumbers.numbers.copyOf()))

            totoPredict.handleNextLowHighPattern(
                lowHighPattern.numbers,
                index,
                didLowHighPatternOccurMoreThanAverage(lowHighPattern)
            )

            // Save the pattern in the map
            patternsCache.merge(lowHighPattern, 1, Int::plus)

            // Frequencies

            if (lastTotoPatternOccurrenceMap.containsKey(lowHighPattern).not()) {
                lastTotoPatternOccurrenceMap[lowHighPattern] = index
                return@forEachIndexed
            }

            lastTotoPatternOccurrenceMap[lowHighPattern]?.let { lastDrawingIndex ->
                val newFrequency = index - lastDrawingIndex

                lastTotoPatternOccurrenceMap[lowHighPattern] = index

                if (frequenciesCache.containsKey(lowHighPattern).not()) {
                    frequenciesCache[lowHighPattern] = mutableListOf(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val doesNewFrequencyExist: Boolean = frequenciesCache[lowHighPattern]
                    ?.any { it.frequency == newFrequency }
                    ?: false
                if (doesNewFrequencyExist.not()) {
                    frequenciesCache[lowHighPattern]?.add(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val frequencyIndex: Int = frequenciesCache[lowHighPattern]
                    ?.indexOfFirst { it.frequency == newFrequency }
                    ?: -1
                if (frequencyIndex == -1) {
                    frequenciesCache[lowHighPattern]?.add(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val totoFrequency: TotoFrequency? = frequenciesCache[lowHighPattern]?.get(frequencyIndex)
                if (totoFrequency == null) {
                    frequenciesCache[lowHighPattern]?.add(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                frequenciesCache[lowHighPattern]?.set(
                    frequencyIndex,
                    totoFrequency.copy(count = totoFrequency.count + 1)
                )
            }
        }

        totoPredict.normalizePrediction()

        validateTotoLowHighPatternOccurrences()
        validateTotoLowHighPatternFrequencies()

        sortTotoLowHighPatternOccurrences()
        sortTotoLowHighPatternFrequencies()
    }

    private fun convertTotoNumbersToLowHighPattern(
        numbers: IntArray
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = if (numbers[i] <= totoType.lowHighMidPoint) 0 else 1
        }

        return numbers
    }

    private fun didLowHighPatternOccurMoreThanAverage(pattern: TotoNumbers): Boolean =
        patternsCache[pattern]?.let { it > patternsCache.values.sum() / patternsCache.size } ?: false

    private fun validateTotoLowHighPatternOccurrences() {
        // Size of the toto numbers should be the same as the total sum of the patterns
        val lowHighPatternSize = patternsCache.values.sum()
        val totoNumberSize = totoNumbers.numbers.count {
            it.position == 0 && it.year.greaterOrEqual(fromYear, true)
        }
        if (lowHighPatternSize != totoNumberSize)
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

    private fun validateTotoLowHighPatternFrequencies() {
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
    private fun sortTotoLowHighPatternOccurrences() {
        patternsCache.sortByValueDescending()
    }

    /**
     * Sort by the same sort order that is used for the [patternsCache].
     * See [sortTotoLowHighPatternOccurrences].
     */
    private fun sortTotoLowHighPatternFrequencies() {
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
}