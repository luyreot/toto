package logicNew.data

import kotlinx.coroutines.coroutineScope
import logicNew.model.LottoNumber
import logicNew.model.LottoType

/**
 * Holds information on how often a number has been drawn.
 */
class LottoNumberOccurrences(
    private val lottoType: LottoType
) {

    val occurrences: Map<Int, Int>
        get() = occurrencesCache

    private val occurrencesCache = mutableMapOf<Int, Int>()

    init {
        when (lottoType) {
            LottoType.D_6X49 -> for (i in 1..49) occurrencesCache[i] = 0
            LottoType.D_6X42 -> for (i in 1..42) occurrencesCache[i] = 0
            LottoType.D_5X35 -> for (i in 1..35) occurrencesCache[i] = 0
        }
    }

    suspend fun calculateLottoNumberOccurrences(
        lottoNumbers: List<LottoNumber>
    ) = coroutineScope {
        lottoNumbers.forEach { number ->
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
    }
}