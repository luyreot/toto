package logicNew.data

import kotlinx.coroutines.coroutineScope
import logicNew.model.LottoType
import logicNew.model.LottoNumber

/**
 * Holds information on how often a number has been drawn.
 */
class LottoNumberOccurrences(
    private val lottoType: LottoType
) {

    val numbers: Map<Int, Int>
        get() = numbersCache

    private val numbersCache = mutableMapOf<Int, Int>()

    init {
        when (lottoType) {
            LottoType.D_6X49 -> for (i in 1..49) numbersCache[i] = 0
            LottoType.D_6X42 -> for (i in 1..42) numbersCache[i] = 0
            LottoType.D_5X35 -> for (i in 1..35) numbersCache[i] = 0
        }
    }

    suspend fun calculateLottoNumberOccurrences(
        lottoNumbers: List<LottoNumber>
    ) = coroutineScope {
        lottoNumbers.forEach { number ->
            // Increment the value of how often a drawing number has occurred by 1
            numbersCache.merge(number.number, 1, Int::plus)
        }

        validateLottoNumberOccurrences()
    }

    private fun validateLottoNumberOccurrences() {
        when (lottoType) {
            LottoType.D_6X49 -> if (numbersCache.size != 49)
                throw IllegalArgumentException("Drawing is not ${lottoType.name}!")

            LottoType.D_6X42 -> if (numbersCache.size != 42)
                throw IllegalArgumentException("Drawing is not ${lottoType.name}!")

            LottoType.D_5X35 -> if (numbersCache.size != 35)
                throw IllegalArgumentException("Drawing is not ${lottoType.name}!")
        }
    }
}