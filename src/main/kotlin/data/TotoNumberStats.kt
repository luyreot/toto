package data

import extensions.sortByValueDescending
import kotlinx.coroutines.coroutineScope
import model.TotoFrequency
import model.TotoNumber
import model.TotoType

/**
 * Holds information about:
 * - how often a number has been drawn
 * - the spacing between issues when a particular number has occurred, via the [TotoFrequency] data class
 */
class TotoNumberStats(
    private val totoType: TotoType,
    private val totoNumbers: TotoNumbers
) {

    val occurrences: Map<Int, Int>
        get() = occurrencesCache

    private val occurrencesCache = mutableMapOf<Int, Int>()

    val frequencies: Map<Int, List<TotoFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<Int, MutableList<TotoFrequency>>()

    /**
     * Calculate number occurrences.
     * Calculate number frequencies.
     */
    suspend fun calculateStats() = coroutineScope {
        totoNumbers.numbers
            .sortedWith(compareBy<TotoNumber> { it.year }.thenBy { it.issue }.thenBy { it.position })
            .let { sortedTotoNumbers ->

                // Var to track the index of the drawing. Increment each time when a new drawing issue is occurring.
                // Needed to calculate the frequencies between the same number from different drawings.
                // totoNumber.issue cannot be used since two different years can have the same issue number, ie. 1
                var currentDrawingIndex = 0
                // Var to detect when a new drawing issue is occurring.
                var currentDrawingIssue = -1

                // Track the number and the drawing index at which it has occurred last
                val lastTotoNumberOccurrenceMap = mutableMapOf<Int, Int>()

                sortedTotoNumbers.forEach { totoNumber ->
                    val number = totoNumber.number

                    // Increment the value of how often a drawing number has occurred by 1
                    occurrencesCache.merge(number, 1, Int::plus)

                    // The issue value changes indicate a new set of numbers are coming up.
                    // Each set represent a complete drawing.
                    // Increment the drawing index and store the current issue number for next iteration.
                    if (totoNumber.issue != currentDrawingIssue) {
                        currentDrawingIndex += 1
                        currentDrawingIssue = totoNumber.issue
                    }

                    // Track the first occurrence of a particular number.
                    // Store it, cannot calculate its frequency yet.
                    if (lastTotoNumberOccurrenceMap.containsKey(number).not()) {
                        lastTotoNumberOccurrenceMap[number] = currentDrawingIndex
                        return@forEach
                    }

                    // Track the subsequent occurrence of a number.
                    // Calculate the frequency and update the index at which the number last occurred.
                    lastTotoNumberOccurrenceMap[number]?.let { lastDrawingIndex ->
                        val newFrequency = currentDrawingIndex - lastDrawingIndex

                        lastTotoNumberOccurrenceMap[number] = currentDrawingIndex

                        // Toto number does not have any frequencies yet
                        if (frequenciesCache.containsKey(number).not()) {
                            frequenciesCache[number] = mutableListOf(TotoFrequency(frequency = newFrequency))
                            return@forEach
                        }

                        // Toto number has already some frequencies
                        val doesNewFrequencyExist: Boolean = frequenciesCache[number]?.any { it.frequency == newFrequency } ?: false

                        // Add new frequency to number
                        if (doesNewFrequencyExist.not()) {
                            frequenciesCache[number]?.add(TotoFrequency(frequency = newFrequency))
                            return@forEach
                        }

                        // Get index of the existing frequency
                        val index: Int = frequenciesCache[number]?.indexOfFirst { it.frequency == newFrequency } ?: -1
                        // Defensive coding in case the frequency does not exist
                        if (index == -1) {
                            frequenciesCache[number]?.add(TotoFrequency(frequency = newFrequency))
                            return@forEach
                        }

                        // Defensive coding in case the frequency does not exist
                        val totoFrequency: TotoFrequency? = frequenciesCache[number]?.get(index)
                        if (totoFrequency == null) {
                            frequenciesCache[number]?.add(TotoFrequency(frequency = newFrequency))
                            return@forEach
                        }

                        // Increment the count of the exiting frequency
                        frequenciesCache[number]?.set(
                            index,
                            totoFrequency.copy(count = totoFrequency.count + 1)
                        )
                    }
                }
            }

        validateTotoNumberOccurrences()
        validateTotoNumberFrequencies()

        sortTotoNumberOccurrences()
        sortTotoNumberFrequencies()
    }

    private fun validateTotoNumberOccurrences() {
        if (occurrencesCache.size != totoType.numberCount)
            throw IllegalArgumentException("Drawing is not ${totoType.name}!")

        if (occurrencesCache.values.any { it == 0 })
            throw IllegalArgumentException("Invalid number occurrence value!")
    }

    private fun validateTotoNumberFrequencies() {
        if (frequenciesCache.size != totoType.numberCount)
            throw IllegalArgumentException("Drawing is not ${totoType.name}!")

        if (occurrencesCache.size != frequenciesCache.size)
            throw IllegalArgumentException("Occurrences and frequencies sizes do not match!")

        // The number of occurrences should be the same as the total sum of the frequencies plus 1
        occurrencesCache.forEach { (totoNumber, occurrences) ->
            val totalFrequencyCount: Int = frequenciesCache[totoNumber]?.sumOf { it.count }
                ?: throw IllegalArgumentException("Frequencies for $totoNumber do not exist!")

            if (totalFrequencyCount + 1 != occurrences)
                throw IllegalArgumentException("Occurrences and frequencies for $totoNumber do not match!")
        }
    }

    /**
     * Sort by how ofter a number has appeared.
     */
    private fun sortTotoNumberOccurrences() {
        occurrencesCache.sortByValueDescending()
    }

    /**
     * Sort by the same sort order that is used for the [occurrencesCache].
     * See [sortTotoNumberOccurrences].
     */
    private fun sortTotoNumberFrequencies() {
        val sortedOccurrences = occurrencesCache.keys.toList()
        val sortedFrequencies = mutableMapOf<Int, MutableList<TotoFrequency>>()

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
}