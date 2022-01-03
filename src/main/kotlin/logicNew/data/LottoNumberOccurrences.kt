package logicNew.data

import kotlinx.coroutines.coroutineScope
import logicNew.model.LottoType

/**
 * Holds information on how often a number has been drawn.
 */
class LottoNumberOccurrences(
    private val lottoType: LottoType,
    private val lottoNumbers: LottoNumbers
) {

    val occurrences: Map<Int, Int>
        get() = occurrencesCache

    private val occurrencesCache = mutableMapOf<Int, Int>()

    suspend fun calculateLottoNumberOccurrences() = coroutineScope {
        lottoNumbers.numbers.forEach { number ->
            // Increment the value of how often a drawing number has occurred by 1
            occurrencesCache.merge(number.number, 1, Int::plus)
        }

        validateLottoNumberOccurrences()
    }

    private fun validateLottoNumberOccurrences() {
        when (lottoType) {
            LottoType.D_6X49 -> if (occurrencesCache.size != 49)
                throw IllegalArgumentException("Drawing is not ${lottoType.name}!")

            LottoType.D_6X42 -> if (occurrencesCache.size != 42)
                throw IllegalArgumentException("Drawing is not ${lottoType.name}!")

            LottoType.D_5X35 -> if (occurrencesCache.size != 35)
                throw IllegalArgumentException("Drawing is not ${lottoType.name}!")
        }

        if (occurrencesCache.values.any { it == 0 })
            throw IllegalArgumentException("Invalid number occurrence value!")
    }
}