package logicNew.data

import logicNew.model.LottoType

class LottoStats(
    lottoType: LottoType
) {

    val lottoNumbers = LottoNumbers(lottoType)
    val lottoNumberOccurrences = LottoNumberOccurrences(lottoType)
    val lottoNumberFrequencies = LottoNumberFrequencies(lottoType, lottoNumberOccurrences)

    fun loadLottoNumbers(
        vararg years: Int
    ) {
        lottoNumbers.loadLottoNumbers(*years)
    }

    suspend fun calculateLottoNumberOccurrences() {
        lottoNumberOccurrences.calculateLottoNumberOccurrences(lottoNumbers.numbers)
    }

    suspend fun calculateLottoNumberFrequencies() {
        lottoNumberFrequencies.calculateLottoNumberFrequencies(lottoNumbers.numbers)
    }
}