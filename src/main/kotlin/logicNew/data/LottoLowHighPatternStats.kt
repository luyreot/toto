package logicNew.data

import kotlinx.coroutines.coroutineScope
import logicNew.extensions.clear
import logicNew.model.LottoFrequency
import logicNew.model.LottoNumber
import logicNew.model.LottoPattern
import logicNew.model.LottoType

/**
 * Holds information about:
 * - occurrences of low/high patterns for each lotto drawing
 * - the spacing between issues when a particular pattern has occurred, via the [LottoFrequency] data class
 *
 * An low/high pattern for a lotto drawing will look like this:
 * 0 - <= 25, 1 - > 25 (when the lotto type is 6x49)
 * 5,14,22,25,34,49 -> 0,0,0,0,1,1
 */
class LottoLowHighPatternStats(
    private val lottoType: LottoType,
    private val lottoNumbers: LottoNumbers
) {

    val patterns: Map<LottoPattern, Int>
        get() = patternsCache

    private val patternsCache = mutableMapOf<LottoPattern, Int>()

    val frequencies: Map<LottoPattern, List<LottoFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<LottoPattern, MutableList<LottoFrequency>>()

    suspend fun calculateLottoLowHighPatternStats() = coroutineScope {
        lottoNumbers.numbers.sortedWith(compareBy<LottoNumber> { it.year }.thenBy { it.issue }.thenBy { it.position })
            .let { sortedLottoNumbers ->
                val currentDrawing = IntArray(lottoType.drawingSize)
                var currentDrawingIndex = 0
                val lastLottoPatternOccurrenceMap = mutableMapOf<LottoPattern, Int>()

                sortedLottoNumbers.forEach { lottoNumber ->
                    // Fill the array with the numbers corresponding to the individual drawing
                    currentDrawing[lottoNumber.position] = lottoNumber.number

                    // Add the odd even pattern on the last number from the current issue
                    if (lottoNumber.position == lottoType.drawingSize - 1) {
                        currentDrawingIndex += 1

                        // Already got the lotto numbers of a single drawing
                        val lowHighPattern = LottoPattern(
                            pattern = convertLottoNumbersToLowHighPattern(currentDrawing.copyOf())
                        )

                        // Save the pattern in the map
                        patternsCache.merge(lowHighPattern, 1, Int::plus)

                        // Reset the tmp array for the next lotto drawing
                        currentDrawing.clear()

                        // Frequencies

                        if (lastLottoPatternOccurrenceMap.containsKey(lowHighPattern).not()) {
                            lastLottoPatternOccurrenceMap[lowHighPattern] = currentDrawingIndex
                            return@forEach
                        }

                        lastLottoPatternOccurrenceMap[lowHighPattern]?.let { lastDrawingIndex ->
                            val newFrequency = currentDrawingIndex - lastDrawingIndex

                            lastLottoPatternOccurrenceMap[lowHighPattern] = currentDrawingIndex

                            if (frequenciesCache.containsKey(lowHighPattern).not()) {
                                frequenciesCache[lowHighPattern] = mutableListOf(LottoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val doesNewFrequencyExist: Boolean = frequenciesCache[lowHighPattern]?.any { it.frequency == newFrequency }
                                ?: false
                            if (doesNewFrequencyExist.not()) {
                                frequenciesCache[lowHighPattern]?.add(LottoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val index: Int = frequenciesCache[lowHighPattern]?.indexOfFirst { it.frequency == newFrequency } ?: -1
                            if (index == -1) {
                                frequenciesCache[lowHighPattern]?.add(LottoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val lottoFrequency: LottoFrequency? = frequenciesCache[lowHighPattern]?.get(index)
                            if (lottoFrequency == null) {
                                frequenciesCache[lowHighPattern]?.add(LottoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            frequenciesCache[lowHighPattern]?.set(
                                index,
                                lottoFrequency.copy(count = lottoFrequency.count + 1)
                            )
                        }
                    }
                }
            }

        validateLottoLowHighPatternOccurrences()
        validateLottoLowHighPatternFrequencies()
    }

    private fun convertLottoNumbersToLowHighPattern(
        numbers: IntArray
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = if (numbers[i] <= lottoType.lowHighMidPoint) 0 else 1
        }

        return numbers
    }

    private fun validateLottoLowHighPatternOccurrences() {
        // Size of the lotto numbers should be the same as the total sum of the patterns
        val lowHighPatternSize = patternsCache.values.sum()
        val lottoNumberSize = lottoNumbers.numbers.count { it.position == 0 }
        if (lowHighPatternSize != lottoNumberSize)
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

    private fun validateLottoLowHighPatternFrequencies() {
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
}