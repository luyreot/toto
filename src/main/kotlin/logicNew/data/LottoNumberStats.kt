package logicNew.data

import kotlinx.coroutines.coroutineScope
import logicNew.model.LottoFrequency
import logicNew.model.LottoNumber
import logicNew.model.LottoType

/**
 * Holds information on:
 * - how often a number has been drawn
 * - the spacing between issues when a particular number has occurred, via the [LottoFrequency] data class
 */
class LottoNumberStats(
    private val lottoType: LottoType,
    private val lottoNumbers: LottoNumbers
) {

    val occurrences: Map<Int, Int>
        get() = occurrencesCache

    private val occurrencesCache = mutableMapOf<Int, Int>()

    val frequencies: Map<Int, List<LottoFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<Int, MutableList<LottoFrequency>>()

    /**
     * Calculate number occurrences.
     * Calculate number frequencies.
     */
    suspend fun calculate() = coroutineScope {
        lottoNumbers.numbers.sortedWith(compareBy<LottoNumber> { it.year }.thenBy { it.issue }.thenBy { it.position })
            .let { sortedLottoNumbers ->

                // The index of a particular drawing. Independent of the drawing's issue value.
                // Drawings from multiple years can be loaded. Since each year can have the same number of issues,
                // each drawing must be tracked individually.
                // Most important when tracking numbers when the year changes.
                var currentDrawingIndex = 0
                var currentDrawingIssue = -1

                // Track the number and the drawing index at which it has occurred last
                val lastLottoNumberOccurrenceMap = mutableMapOf<Int, Int>()

                sortedLottoNumbers.forEach { lottoNumber ->
                    val number = lottoNumber.number

                    // Increment the value of how often a drawing number has occurred by 1
                    occurrencesCache.merge(number, 1, Int::plus)

                    // The issue value changes indicate a new set of numbers are coming up.
                    // Each set represent a complete drawing.
                    // Increment the drawing index and store the current issue number for next iteration.
                    if (lottoNumber.issue != currentDrawingIssue) {
                        currentDrawingIndex += 1
                        currentDrawingIssue = lottoNumber.issue
                    }

                    // Track the first occurrence of a particular number.
                    // Store it, cannot calculate its frequency yet.
                    if (lastLottoNumberOccurrenceMap.containsKey(number).not()) {
                        lastLottoNumberOccurrenceMap[number] = currentDrawingIndex
                        return@forEach
                    }

                    // Track the subsequent occurrence of a number.
                    // Calculate the frequency and update the index at which the number last occurred.
                    lastLottoNumberOccurrenceMap[number]?.let { lastDrawingIndex ->
                        val newFrequency = currentDrawingIndex - lastDrawingIndex

                        lastLottoNumberOccurrenceMap[number] = currentDrawingIndex

                        // Lotto number does not have any frequencies yet
                        if (frequenciesCache.containsKey(number).not()) {
                            frequenciesCache[number] = mutableListOf(LottoFrequency(frequency = newFrequency))
                            return@forEach
                        }

                        // Lotto number has already some frequencies
                        val doesNewFrequencyExist = frequenciesCache[number]?.any { it.frequency == newFrequency }
                        // New frequency does not exist
                        if (doesNewFrequencyExist?.not() == true) {
                            frequenciesCache[number]?.add(LottoFrequency(frequency = newFrequency))
                            return@forEach
                        }

                        // New frequency does not exist
                        val index: Int = frequenciesCache[number]?.indexOfFirst { it.frequency == newFrequency } ?: -1
                        if (index == -1) {
                            frequenciesCache[number]?.add(LottoFrequency(frequency = newFrequency))
                            return@forEach
                        }

                        // New frequency does not exist
                        val lottoFrequency: LottoFrequency? = frequenciesCache[number]?.get(index)
                        if (lottoFrequency == null) {
                            frequenciesCache[number]?.add(LottoFrequency(frequency = newFrequency))
                            return@forEach
                        }

                        // New frequency does exist
                        frequenciesCache[number]?.set(
                            index,
                            lottoFrequency.copy(count = lottoFrequency.count + 1)
                        )
                    }
                }
            }

        validateLottoNumberOccurrences()
        validateLottoNumberFrequencies()
    }

    private fun validateLottoNumberOccurrences() {
        if (occurrencesCache.size != lottoType.numberCount)
            throw IllegalArgumentException("Drawing is not ${lottoType.name}!")

        if (occurrencesCache.values.any { it == 0 })
            throw IllegalArgumentException("Invalid number occurrence value!")
    }

    private fun validateLottoNumberFrequencies() {
        if (frequenciesCache.size != lottoType.numberCount)
            throw IllegalArgumentException("Drawing is not ${lottoType.name}!")

        if (occurrencesCache.size != frequenciesCache.size)
            throw IllegalArgumentException("Occurrences and frequencies sizes do not match!")

        // The number of occurrences should be the same as the total sum of the frequencies plus 1
        occurrencesCache.forEach { (lottoNumber, occurrences) ->
            val totalFrequencyCount: Int = frequenciesCache[lottoNumber]?.sumOf { it.count }
                ?: throw IllegalArgumentException("Frequencies for $lottoNumber do not exist!")

            if (totalFrequencyCount + 1 != occurrences)
                throw IllegalArgumentException("Occurrences and frequencies for $lottoNumber do not match!")
        }
    }
}