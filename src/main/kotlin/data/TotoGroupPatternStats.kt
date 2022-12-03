package data

import extensions.greaterOrEqual
import extensions.sortByValueDescending
import model.*
import util.PatternUtils.convertTotoNumbersToGroupPattern
import util.PatternUtils.didPatternOccurMoreThanAverage

class TotoGroupPatternStats(
    private val totoType: TotoType,
    private val totoNumbers: TotoDrawnNumbers,
    private val groupStrategy: TotoGroupStrategy,
    private val totoPredict: TotoGroupPatternPredict,
    private val fromYear: Int? = null
) {

    val patterns: Map<TotoNumbers, Int>
        get() = patternsCache

    private val patternsCache = mutableMapOf<TotoNumbers, Int>()

    val frequencies: Map<TotoNumbers, List<TotoFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<TotoNumbers, MutableList<TotoFrequency>>()

    private val groupStrategyMethod = groupStrategies[groupStrategy] as? (Int) -> Int

    init {
        if (groupStrategyMethod == null)
            throw IllegalArgumentException("Group strategy method is null!")
    }

    fun calculateTotoGroupPatternStats() {
        val drawings = if (fromYear == null) totoNumbers.allDrawings else totoNumbers.drawingsSubset
        val lastTotoPatternOccurrenceMap = mutableMapOf<TotoNumbers, Int>()

        drawings.forEachIndexed { index, totoNumbers ->
            val groupPattern = TotoNumbers(
                convertTotoNumbersToGroupPattern(totoNumbers.numbers.copyOf(), groupStrategyMethod)
            )

            totoPredict.handleNextGroupPattern(
                groupPattern.numbers,
                index,
                didPatternOccurMoreThanAverage(patternsCache, groupPattern)
            )

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

        validateTotoGroupPatternOccurrences()
        validateTotoGroupPatternFrequencies()

        sortTotoGroupPatternOccurrences()
        sortTotoGroupPatternFrequencies()
    }

    private fun validateTotoGroupPatternOccurrences() {
        // Size of the toto numbers should be the same as the total sum of the patterns
        val groupPatternSize = patternsCache.values.sum()
        val totoNumberSize = totoNumbers.numbers.count {
            it.position == 0 && it.year.greaterOrEqual(fromYear, true)
        }
        if (groupPatternSize != totoNumberSize)
            throw IllegalArgumentException("Pattern size is incorrect!")

        // No group patter should have an occurrence of 0
        if (patternsCache.values.any { it == 0 })
            throw IllegalArgumentException("Invalid pattern occurrence value!")

        // All patterns should have the proper values
        if (groupStrategy != TotoGroupStrategy.DIVIDE_BY_10)
            throw IllegalArgumentException("Invalid Toto Group Strategy!")

        val anyInvalidPatterns = patternsCache.keys.any { pattern ->
            pattern.numbers.any { item ->
                item != 0 && item != 1 && item != 2 && item != 3 && item != 4
            }
        }
        if (anyInvalidPatterns)
            throw IllegalArgumentException("Invalid group pattern!")
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