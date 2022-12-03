package data

import extensions.sortByValueDescending
import model.Frequency
import model.TotoType

/**
 * Holds information about:
 * - how often a number has been drawn
 * - the spacing between issues when a particular number has occurred, via the [Frequency] data class
 */
class NumberStats(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val fromYear: Int? = null
) {

    val occurrences: Map<Int, Int>
        get() = occurrencesCache
    private val occurrencesCache = mutableMapOf<Int, Int>()

    val frequencies: Map<Int, List<Frequency>>
        get() = frequenciesCache
    private val frequenciesCache = mutableMapOf<Int, MutableList<Frequency>>()

    val averageFrequencies: Map<Int, Float>
        get() = averageFrequenciesCache
    private val averageFrequenciesCache = mutableMapOf<Int, Float>()

    /**
     * Calculate number occurrences.
     * Calculate number frequencies.
     */
    fun calculateStats() {
        val drawings = if (fromYear == null) drawings.drawings else drawings.drawingsSubset

        // Track the number and the drawing index at which it has occurred last
        val lastNumberOccurrenceMap = mutableMapOf<Int, Int>()

        drawings.forEachIndexed { index, numbers ->
            numbers.numbers.forEach { number ->
                // Increment the value of how often a drawing number has occurred by 1
                occurrencesCache.merge(number, 1, Int::plus)

                // Track the first occurrence of a particular number.
                // Store it, cannot calculate its frequency yet.
                if (lastNumberOccurrenceMap.containsKey(number).not()) {
                    lastNumberOccurrenceMap[number] = index
                    return@forEach
                }

                // Track the subsequent occurrence of a number.
                // Calculate the frequency and update the index at which the number last occurred.
                lastNumberOccurrenceMap[number]?.let { lastDrawingIndex ->
                    val newFrequency = index - lastDrawingIndex

                    lastNumberOccurrenceMap[number] = index

                    // Toto number does not have any frequencies yet
                    if (frequenciesCache.containsKey(number).not()) {
                        frequenciesCache[number] = mutableListOf(Frequency(frequency = newFrequency))
                        return@forEach
                    }

                    // Toto number has already some frequencies
                    val doesNewFrequencyExist: Boolean = frequenciesCache[number]?.any { it.frequency == newFrequency } ?: false

                    // Add new frequency to number
                    if (doesNewFrequencyExist.not()) {
                        frequenciesCache[number]?.add(Frequency(frequency = newFrequency))
                        return@forEach
                    }

                    // Get index of the existing frequency
                    val frequencyIndex: Int = frequenciesCache[number]?.indexOfFirst { it.frequency == newFrequency } ?: -1
                    // Defensive coding in case the frequency does not exist
                    if (frequencyIndex == -1) {
                        frequenciesCache[number]?.add(Frequency(frequency = newFrequency))
                        return@forEach
                    }

                    // Defensive coding in case the frequency does not exist
                    val frequency: Frequency? = frequenciesCache[number]?.get(frequencyIndex)
                    if (frequency == null) {
                        frequenciesCache[number]?.add(Frequency(frequency = newFrequency))
                        return@forEach
                    }

                    // Increment the count of the exiting frequency
                    frequenciesCache[number]?.set(
                        frequencyIndex,
                        frequency.copy(count = frequency.count + 1)
                    )
                }
            }
        }

        validateNumberOccurrences()
        validateNumberFrequencies()

        sortNumberOccurrences()
        sortNumberFrequencies()

        averageFrequencies()
    }

    private fun validateNumberOccurrences() {
        if (occurrencesCache.size != totoType.totalNumbers)
            throw IllegalArgumentException("Drawing is not ${totoType.name}!")

        if (occurrencesCache.values.any { it == 0 })
            throw IllegalArgumentException("There is a number that has never occurred!")
    }

    private fun validateNumberFrequencies() {
        if (frequenciesCache.size != totoType.totalNumbers)
            throw IllegalArgumentException("Drawing is not ${totoType.name}!")

        if (occurrencesCache.size != frequenciesCache.size)
            throw IllegalArgumentException("Occurrences and frequencies sizes do not match!")

        // The number of occurrences should be the same as the total sum of the frequencies plus 1
        occurrencesCache.forEach { (number, occurrences) ->
            val totalFrequencyCount: Int = frequenciesCache[number]?.sumOf { it.count }
                ?: throw IllegalArgumentException("Frequencies for $number do not exist!")

            if (totalFrequencyCount + 1 != occurrences)
                throw IllegalArgumentException("Occurrences and frequencies for $number do not match!")
        }
    }

    /**
     * Sort by how ofter a number has appeared.
     */
    private fun sortNumberOccurrences() {
        occurrencesCache.sortByValueDescending()
    }

    /**
     * Sort by the same sort order that is used for the [occurrencesCache].
     * See [sortNumberOccurrences].
     */
    private fun sortNumberFrequencies() {
        val sortedOccurrences = occurrencesCache.keys.toList()
        val sortedFrequencies = mutableMapOf<Int, MutableList<Frequency>>()

        sortedOccurrences.forEach { number ->
            frequenciesCache[number]?.let { frequencies ->
                sortedFrequencies[number] = frequencies
            }
        }

        frequenciesCache.apply {
            clear()
            putAll(sortedFrequencies)
        }
    }

    private fun averageFrequencies() {
        frequenciesCache.forEach { (number, frequencies) ->
            val totalCount: Int = frequencies.fold(0) { acc, frequency -> acc + frequency.count }
            val totalSum: Int = frequencies.fold(0) { acc, frequency -> acc + frequency.frequency * frequency.count }
            averageFrequenciesCache[number] = totalSum.div(totalCount.toFloat())
        }
    }
}