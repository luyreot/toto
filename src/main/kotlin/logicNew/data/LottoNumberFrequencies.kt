package logicNew.data

import kotlinx.coroutines.coroutineScope
import logicNew.model.LottoNumber
import logicNew.model.LottoNumberFrequency
import logicNew.model.LottoType

/**
 * Holds information on how often a number has been drawn.
 */
class LottoNumberFrequencies(
    private val lottoType: LottoType,
    private val lottoNumberOccurrences: LottoNumberOccurrences
) {

    val frequencies: Map<Int, List<LottoNumberFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<Int, MutableList<LottoNumberFrequency>>()

    /**
     * Map that holds the last occurred lotto number at a particular year and issue represented by an index.
     * A check is made for the same number occurrence.
     * The difference is calculated between the current and last one.
     * This becomes the frequency at which the lotto number has been drawn.
     */
    suspend fun calculateLottoNumberFrequencies(
        lottoNumbers: List<LottoNumber>
    ) = coroutineScope {
        lottoNumbers.sortedWith(compareBy<LottoNumber> { it.year }.thenBy { it.issue }.thenBy { it.position })
            .let { sortedLottoNumbers ->

                val lastOccurredLottoNumberMap = mutableMapOf<Int, Int>()
                var lottoDrawingIndex = 0
                var lottoDrawingIssue = 0

                sortedLottoNumbers.forEach { lottoNumber ->
                    if (lottoNumber.issue != lottoDrawingIssue) {
                        lottoDrawingIndex += 1
                        lottoDrawingIssue = lottoNumber.issue
                    }

                    val number = lottoNumber.number

                    // Number has not occurred yet
                    if (lastOccurredLottoNumberMap.containsKey(number).not()) {
                        lastOccurredLottoNumberMap[number] = lottoDrawingIndex
                        return@forEach
                    }

                    // Number occurs again
                    lastOccurredLottoNumberMap[number]?.let { lastFrequency ->
                        val newFrequency = lottoDrawingIndex - lastFrequency

                        if (frequenciesCache.containsKey(number)) {
                            // Lotto number has already some frequencies
                            val doesNewFrequencyExist = frequenciesCache[number]?.any { it.frequency == newFrequency }
                            if (doesNewFrequencyExist == true) {
                                val index: Int = frequenciesCache[number]?.indexOfFirst { it.frequency == newFrequency } ?: -1

                                if (index == -1) {
                                    frequenciesCache[number]?.add(LottoNumberFrequency(frequency = newFrequency))
                                } else {
                                    val lottoNumberFrequency: LottoNumberFrequency? = frequenciesCache[number]?.get(index)
                                    if (lottoNumberFrequency == null) {
                                        frequenciesCache[number]?.add(LottoNumberFrequency(frequency = newFrequency))
                                    } else {
                                        frequenciesCache[number]?.set(
                                            index,
                                            lottoNumberFrequency.copy(count = lottoNumberFrequency.count + 1)
                                        )
                                    }
                                }
                            } else {
                                frequenciesCache[number]?.add(LottoNumberFrequency(frequency = newFrequency))
                            }
                        } else {
                            // Lotto number does not have any frequencies yet
                            frequenciesCache[number] = mutableListOf(LottoNumberFrequency(frequency = newFrequency))
                        }

                        lastOccurredLottoNumberMap[number] = lottoDrawingIndex
                    }
                }

            }

        validateLottoNumberFrequencies()
    }

    private fun validateLottoNumberFrequencies() {
        when (lottoType) {
            LottoType.D_6X49 -> if (frequenciesCache.size != 49)
                throw IllegalArgumentException("Drawing is not ${lottoType.name}!")

            LottoType.D_6X42 -> if (frequenciesCache.size != 42)
                throw IllegalArgumentException("Drawing is not ${lottoType.name}!")

            LottoType.D_5X35 -> if (frequenciesCache.size != 35)
                throw IllegalArgumentException("Drawing is not ${lottoType.name}!")
        }

        if (lottoNumberOccurrences.occurrences.size != frequenciesCache.size)
            throw IllegalArgumentException("Occurrences and frequencies sizes do not match!")

        // The number of occurrences should be the same as the total sum of the frequencies plus 1
        lottoNumberOccurrences.occurrences.forEach { (lottoNumber, occurrences) ->
            val totalFrequencyCount: Int = frequenciesCache[lottoNumber]?.sumOf { it.count }
                ?: throw IllegalArgumentException("Frequencies for $lottoNumber do not exist!")

            if (totalFrequencyCount + 1 != occurrences)
                throw IllegalArgumentException("Occurrences and frequencies for $lottoNumber do not match!")
        }
    }
}