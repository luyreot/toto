package logicNew.data

import logicNew.model.LottoType

class LottoStats(
    lottoType: LottoType
) {

    val lottoNumbers = LottoNumbers(lottoType)
    val lottoNumberStats = LottoNumberStats(lottoType, lottoNumbers)
    val lottoOddEvenPatternStats = LottoOddEvenPatternStats(lottoType, lottoNumbers)
    val lottoLowHighPatternStats = LottoLowHighPatternStats(lottoType, lottoNumbers)

    fun loadLottoNumbers(
        vararg years: Int
    ) {
        lottoNumbers.loadLottoNumbers(*years)
    }

    suspend fun calculateLottoNumberStats() {
        lottoNumberStats.calculateStats()
    }

    suspend fun calculateLottoOddEvenPatternStats() {
        lottoOddEvenPatternStats.calculateLottoOddEvenPatternStats()
    }

    suspend fun calculateLottoLowHighPatternStats() {
        lottoLowHighPatternStats.calculateLottoLowHighPatternStats()
    }
}