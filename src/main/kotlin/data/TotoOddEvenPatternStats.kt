package data

import extensions.clear
import extensions.sortByValueDescending
import model.TotoFrequency
import model.TotoNumber
import model.TotoPattern
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
    private val totoNumbers: TotoNumbers,
    private val totoPredict: TotoPredict
) {

    val patterns: Map<TotoPattern, Int>
        get() = patternsCache

    private val patternsCache = mutableMapOf<TotoPattern, Int>()

    val frequencies: Map<TotoPattern, List<TotoFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<TotoPattern, MutableList<TotoFrequency>>()

    fun calculateTotoOddEvenPatternStats() {
        totoNumbers.numbers
            .sortedWith(compareBy<TotoNumber> { it.year }.thenBy { it.issue }.thenBy { it.position })
            .let { sortedTotoNumbers ->
                val currentDrawing = IntArray(totoType.drawingSize)
                var currentDrawingIndex = 0
                val lastTotoPatternOccurrenceMap = mutableMapOf<TotoPattern, Int>()

                sortedTotoNumbers.forEach { totoNumber ->
                    // Fill the array with the numbers corresponding to the individual drawing
                    currentDrawing[totoNumber.position] = totoNumber.number

                    // Add the odd even pattern on the last number from the current issue
                    if (totoNumber.position == totoType.drawingSize - 1) {
                        currentDrawingIndex += 1

                        // Already got the toto numbers of a single drawing
                        val oddEvenPattern = TotoPattern(
                            pattern = convertTotoNumbersToOddEvenPattern(currentDrawing.copyOf())
                        )

                        // Save the pattern in the map
                        patternsCache.merge(oddEvenPattern, 1, Int::plus)

                        totoPredict.addOddEvenPattern(oddEvenPattern.pattern)

                        // Reset the tmp array for the next toto drawing
                        currentDrawing.clear()

                        // Frequencies

                        if (lastTotoPatternOccurrenceMap.containsKey(oddEvenPattern).not()) {
                            lastTotoPatternOccurrenceMap[oddEvenPattern] = currentDrawingIndex
                            return@forEach
                        }

                        lastTotoPatternOccurrenceMap[oddEvenPattern]?.let { lastDrawingIndex ->
                            val newFrequency = currentDrawingIndex - lastDrawingIndex

                            lastTotoPatternOccurrenceMap[oddEvenPattern] = currentDrawingIndex

                            if (frequenciesCache.containsKey(oddEvenPattern).not()) {
                                frequenciesCache[oddEvenPattern] =
                                    mutableListOf(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val doesNewFrequencyExist: Boolean =
                                frequenciesCache[oddEvenPattern]?.any { it.frequency == newFrequency }
                                    ?: false
                            if (doesNewFrequencyExist.not()) {
                                frequenciesCache[oddEvenPattern]?.add(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val index: Int =
                                frequenciesCache[oddEvenPattern]?.indexOfFirst { it.frequency == newFrequency } ?: -1
                            if (index == -1) {
                                frequenciesCache[oddEvenPattern]?.add(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val totoFrequency: TotoFrequency? = frequenciesCache[oddEvenPattern]?.get(index)
                            if (totoFrequency == null) {
                                frequenciesCache[oddEvenPattern]?.add(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            frequenciesCache[oddEvenPattern]?.set(
                                index,
                                totoFrequency.copy(count = totoFrequency.count + 1)
                            )
                        }
                    }
                }
            }

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

    private fun validateTotoOddEvenPatternOccurrences() {
        // Size of the toto numbers should be the same as the total sum of the patterns
        val oddEvenPatternSize = patternsCache.values.sum()
        val totoNumberSize = totoNumbers.numbers.count { it.position == 0 }
        if (oddEvenPatternSize != totoNumberSize)
            throw IllegalArgumentException("Pattern size is incorrect!")

        // No odd/even pattern should have an occurrence of 0
        if (patternsCache.values.any { it == 0 })
            throw IllegalArgumentException("Invalid pattern occurrence value!")

        // All odd/even patterns should contain 0 or 1 values
        val anyInvalidPatterns = patternsCache.keys.any { oddEvenPattern ->
            oddEvenPattern.pattern.any { arrayItem -> arrayItem != 0 && arrayItem != 1 }
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
        val sortedFrequencies = mutableMapOf<TotoPattern, MutableList<TotoFrequency>>()

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