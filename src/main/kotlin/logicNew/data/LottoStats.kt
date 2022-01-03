package logicNew.data

import logicNew.model.LottoType

class LottoStats(
    lottoType: LottoType
) {

    val lottoNumbers = LottoNumbers(lottoType)
    val lottoNumberOccurrences = LottoNumberOccurrences(lottoType, lottoNumbers)
    val lottoNumberFrequencies = LottoNumberFrequencies(lottoType, lottoNumbers, lottoNumberOccurrences)
    val lottoOddEvenPatternOccurrences = LottoOddEvenPatternOccurrences(lottoType, lottoNumbers)
    val lottoLowHighPatternOccurrences = LottoLowHighPatternOccurrences(lottoType, lottoNumbers)

    fun loadLottoNumbers(
        vararg years: Int
    ) {
        lottoNumbers.loadLottoNumbers(*years)
    }

    suspend fun calculateLottoNumberOccurrences() {
        lottoNumberOccurrences.calculateLottoNumberOccurrences()
    }

    suspend fun calculateLottoNumberFrequencies() {
        lottoNumberFrequencies.calculateLottoNumberFrequencies()
    }

    suspend fun calculateLottoOddEvenPatternOccurrences() {
        lottoOddEvenPatternOccurrences.calculateLottoOddEvenPatternOccurrences()
    }

    suspend fun calculateLottoLowHighPatternOccurrences() {
        lottoLowHighPatternOccurrences.calculateLottoLowHighPatternOccurrences()
    }
}