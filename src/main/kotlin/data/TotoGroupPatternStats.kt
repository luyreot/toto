package data

import extensions.clear
import extensions.sortByValueDescending
import model.*

class TotoGroupPatternStats(
    private val totoType: TotoType,
    private val totoNumbers: TotoNumbers,
    private val groupStrategy: TotoGroupStrategy,
    private val totoPredict: TotoGroupPatternPredict,
    private val fromYear: Int? = null
) {

    val patterns: Map<TotoPattern, Int>
        get() = patternsCache

    private val patternsCache = mutableMapOf<TotoPattern, Int>()

    val frequencies: Map<TotoPattern, List<TotoFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<TotoPattern, MutableList<TotoFrequency>>()

    fun calculateTotoGroupPatternStats() {
        totoNumbers.numbers
            .sortedWith(compareBy<TotoNumber> { it.year }.thenBy { it.issue }.thenBy { it.position })
            .let { sortedTotoNumbers ->
                val currentDrawing = IntArray(totoType.drawingSize)
                var currentDrawingIndex = 0
                val lastTotoPatternOccurrenceMap = mutableMapOf<TotoPattern, Int>()

                sortedTotoNumbers.forEach { totoNumber ->
                    if (fromYear != null && totoNumber.year < fromYear) {
                        return@forEach
                    }

                    currentDrawing[totoNumber.position] = totoNumber.number

                    if (totoNumber.position == totoType.drawingSize - 1) {
                        currentDrawingIndex += 1

                        val groupPattern = TotoPattern(
                            pattern = convertTotoNumbersToGroupPattern(currentDrawing.copyOf())
                        )

                        patternsCache.merge(groupPattern, 1, Int::plus)

                        totoPredict.handleNextGroupPattern(groupPattern.pattern, currentDrawingIndex)

                        currentDrawing.clear()

                        // Frequencies

                        if (lastTotoPatternOccurrenceMap.containsKey(groupPattern).not()) {
                            lastTotoPatternOccurrenceMap[groupPattern] = currentDrawingIndex
                            return@forEach
                        }

                        lastTotoPatternOccurrenceMap[groupPattern]?.let { lastDrawingIndex ->
                            val newFrequency = currentDrawingIndex - lastDrawingIndex

                            lastTotoPatternOccurrenceMap[groupPattern] = currentDrawingIndex

                            if (frequenciesCache.containsKey(groupPattern).not()) {
                                frequenciesCache[groupPattern] = mutableListOf(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val doesNewFrequencyExist: Boolean =
                                frequenciesCache[groupPattern]?.any { it.frequency == newFrequency }
                                    ?: false
                            if (doesNewFrequencyExist.not()) {
                                frequenciesCache[groupPattern]?.add(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val index: Int =
                                frequenciesCache[groupPattern]?.indexOfFirst { it.frequency == newFrequency } ?: -1
                            if (index == -1) {
                                frequenciesCache[groupPattern]?.add(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val totoFrequency: TotoFrequency? = frequenciesCache[groupPattern]?.get(index)
                            if (totoFrequency == null) {
                                frequenciesCache[groupPattern]?.add(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            frequenciesCache[groupPattern]?.set(
                                index,
                                totoFrequency.copy(count = totoFrequency.count + 1)
                            )
                        }
                    }
                }
            }

        totoPredict.normalizePrediction()

        validateTotoGroupPatternOccurrences()
        validateTotoGroupPatternFrequencies()

        sortTotoGroupPatternOccurrences()
        sortTotoGroupPatternFrequencies()
    }

    private fun convertTotoNumbersToGroupPattern(
        numbers: IntArray
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = groupStrategy.method.invoke(numbers[i])
        }

        return numbers
    }

    private fun validateTotoGroupPatternOccurrences() {
        // Size of the toto numbers should be the same as the total sum of the patterns
        val groupPatternSize = patternsCache.values.sum()
        val totoNumberSize = totoNumbers.numbers.count { it.position == 0 }
        if (groupPatternSize != totoNumberSize)
            throw IllegalArgumentException("Pattern size is incorrect!")

        // No group patter should have an occurrence of 0
        if (patternsCache.values.any { it == 0 })
            throw IllegalArgumentException("Invalid pattern occurrence value!")

        // All patterns should have the proper values
        if (groupStrategy != TotoGroupStrategy.DIVIDE_BY_10)
            throw IllegalArgumentException("Invalid Toto Group Strategy!")

        val anyInvalidPatterns = patternsCache.keys.any { pattern ->
            pattern.pattern.any { item ->
                item != 0 && item != 1 && item != 2 && item != 3 && item != 4
            }
        }
        if (anyInvalidPatterns) throw IllegalArgumentException("Invalid group pattern!")
    }

    private fun validateTotoGroupPatternFrequencies() {
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
    private fun sortTotoGroupPatternOccurrences() {
        patternsCache.sortByValueDescending()
    }

    /**
     * Sort by the same sort order that is used for the [patternsCache].
     * See [sortTotoGroupPatternOccurrences].
     */
    private fun sortTotoGroupPatternFrequencies() {
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