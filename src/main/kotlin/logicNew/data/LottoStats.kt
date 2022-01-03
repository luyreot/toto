package logicNew.data

import logicNew.model.LottoType

class LottoStats(
    lottoType: LottoType
) {

    val lottoNumbers = LottoNumbers(lottoType)
    val lottoNumberOccurrences = LottoNumberOccurrences(lottoType)

    fun loadLottoNumbers(
        vararg years: Int
    ) {
        lottoNumbers.loadLottoNumbers(*years)
    }

    suspend fun calculateLottoNumberOccurrences() {
        lottoNumberOccurrences.calculateLottoNumberOccurrences(lottoNumbers.numbers)
    }

    suspend fun calculateNumberFrequencies() {
        numberFrequencies.calculateNumberFrequencies(drawnNumbers.numbers)
    }
}