package data

import extensions.greaterOrEqual
import extensions.sortByValueDescending
import model.TotoFrequency
import model.TotoNumbers
import model.TotoType

/**
 * Holds information about:
 * - occurrences of odd/even patterns for each toto drawing.
 * - the spacing between issues when a particular pattern has occurred, via the [TotoFrequency] data class
 *
 * An odd/even pattern for a toto drawing will look like this:
 * 0 - odd, 1 - even
 * 5,14,22,25,34,49 -> 0,1,1,0,1,0
 */
class TotoOddEvenPatternStats(
    private val totoType: TotoType,
    private val totoNumbers: TotoDrawnNumbers,
    private val totoPredict: TotoOddEvenPatternPredict,
    private val fromYear: Int? = null
) {

    val patterns: Map<TotoNumbers, Int>
        get() = patternsCache

    private val patternsCache = mutableMapOf<TotoNumbers, Int>()

    val frequencies: Map<TotoNumbers, List<TotoFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<TotoNumbers, MutableList<TotoFrequency>>()

    fun calculateTotoOddEvenPatternStats() {
        val drawings = if (fromYear == null) totoNumbers.allDrawings else totoNumbers.drawingsSubset
        val lastTotoPatternOccurrenceMap = mutableMapOf<TotoNumbers, Int>()

        drawings.forEachIndexed { index, totoNumbers ->
            // Already got the toto numbers of a single drawing
            val oddEvenPattern = TotoNumbers(convertTotoNumbersToOddEvenPattern(totoNumbers.numbers.copyOf()))

            totoPredict.handleNextOddEvenPattern(
                oddEvenPattern.numbers,
                index,
                didOddEvenPatternOccurMoreThanAverage(oddEvenPattern)
            )

            // Save the pattern in the map
            patternsCache.merge(oddEvenPattern, 1, Int::plus)

            // Frequencies

            if (lastTotoPatternOccurrenceMap.containsKey(oddEvenPattern).not()) {
                lastTotoPatternOccurrenceMap[oddEvenPattern] = index
                return@forEachIndexed
            }

            lastTotoPatternOccurrenceMap[oddEvenPattern]?.let { lastDrawingIndex ->
                val newFrequency = index - lastDrawingIndex

                lastTotoPatternOccurrenceMap[oddEvenPattern] = index

                if (frequenciesCache.containsKey(oddEvenPattern).not()) {
                    frequenciesCache[oddEvenPattern] = mutableListOf(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val doesNewFrequencyExist: Boolean = frequenciesCache[oddEvenPattern]
                    ?.any { it.frequency == newFrequency }
                    ?: false
                if (doesNewFrequencyExist.not()) {
                    frequenciesCache[oddEvenPattern]?.add(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val frequencyIndex: Int = frequenciesCache[oddEvenPattern]
                    ?.indexOfFirst { it.frequency == newFrequency }
                    ?: -1
                if (frequencyIndex == -1) {
                    frequenciesCache[oddEvenPattern]?.add(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                val totoFrequency: TotoFrequency? = frequenciesCache[oddEvenPattern]?.get(frequencyIndex)
                if (totoFrequency == null) {
                    frequenciesCache[oddEvenPattern]?.add(TotoFrequency(frequency = newFrequency))
                    return@forEachIndexed
                }

                frequenciesCache[oddEvenPattern]?.set(
                    frequencyIndex,
                    totoFrequency.copy(count = totoFrequency.count + 1)
                )
            }
        }

        totoPredict.normalizePrediction()

        validateTotoOddEvenPatternOccurrences()
        validateTotoOddEvenPatternFrequencies()

        sortTotoOddEvenPatternOccurrences()
        sortTotoOddEvenPatternFrequencies()
    }

    private fun convertTotoNumbersToOddEvenPattern(
        numbers: IntArray
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = if ((numbers[i] and 1) == 0) 1 else 0
        }

        return numbers
    }

    private fun didOddEvenPatternOccurMoreThanAverage(pattern: TotoNumbers): Boolean =
        patternsCache[pattern]?.let { it > patternsCache.values.sum() / patternsCache.size } ?: false

    private fun validateTotoOddEvenPatternOccurrences() {
        // Size of the toto numbers should be the same as the total sum of the patterns
        val oddEvenPatternSize = patternsCache.values.sum()
        val totoNumberSize = totoNumbers.numbers.count {
            it.position == 0 && it.year.greaterOrEqual(fromYear, true)
        }
        if (oddEvenPatternSize != totoNumberSize)
            throw IllegalArgumentException("Pattern size is incorrect!")

        // No odd/even pattern should have an occurrence of 0
        if (patternsCache.values.any { it == 0 })
            throw IllegalArgumentException("Invalid pattern occurrence value!")

        // All odd/even patterns should contain 0 or 1 values
        val anyInvalidPatterns = patternsCache.keys.any { oddEvenPattern ->
            oddEvenPattern.numbers.any { arrayItem -> arrayItem != 0 && arrayItem != 1 }
        }
        if (anyInvalidPatterns)
            throw IllegalArgumentException("Invalid odd/even pattern!")
    }

    private fun validateTotoOddEvenPatternFrequencies() {
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
    private fun sortTotoOddEvenPatternOccurrences() {
        patternsCache.sortByValueDescending()
    }

    /**
     * Sort by the same sort order that is used for the [patternsCache].
     * See [sortTotoOddEvenPatternOccurrences].
     */
    private fun sortTotoOddEvenPatternFrequencies() {
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