package logicNew.data

import logicNew.model.LottoType

class LottoStats(
    lottoType: LottoType
) {

    val lottoNumbers = LottoNumbers(lottoType)
    val lottoNumberStats = LottoNumberStats(lottoType, lottoNumbers)
    val lottoOddEvenPatternOccurrences = LottoOddEvenPatternOccurrences(lottoType, lottoNumbers)
    val lottoLowHighPatternOccurrences = LottoLowHighPatternOccurrences(lottoType, lottoNumbers)

    fun loadLottoNumbers(
        vararg years: Int
    ) {
        lottoNumbers.loadLottoNumbers(*years)
    }

    suspend fun calculateLottoNumberStats() {
        lottoNumberStats.calculate()
    }

    suspend fun calculateLottoOddEvenPatternOccurrences() {
        lottoOddEvenPatternOccurrences.calculateLottoOddEvenPatternOccurrences()
    }

    suspend fun calculateLottoLowHighPatternOccurrences() {
        lottoLowHighPatternOccurrences.calculateLottoLowHighPatternOccurrences()
    }
}