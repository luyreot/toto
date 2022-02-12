package logicNew.data

import logicNew.model.TotoType

class TotoStats(
    totoType: TotoType
) {

    val totoNumbers = TotoNumbers(totoType)
    val totoNumberStats = TotoNumberStats(totoType, totoNumbers)
    val totoOddEvenPatternStats = TotoOddEvenPatternStats(totoType, totoNumbers)
    val totoLowHighPatternStats = TotoLowHighPatternStats(totoType, totoNumbers)
    val totoGroupPatternStats = TotoGroupPatternStats(totoType, totoNumbers)

    fun loadTotoNumbers(
        vararg years: Int
    ) {
        totoNumbers.loadLottoNumbers(*years)
    }

    suspend fun calculateTotoNumberStats() {
        totoNumberStats.calculateStats()
    }

    suspend fun calculateTotoOddEvenPatternStats() {
        totoOddEvenPatternStats.calculateTotoOddEvenPatternStats()
    }

    suspend fun calculateTotoLowHighPatternStats() {
        totoLowHighPatternStats.calculateTotoLowHighPatternStats()
    }

    suspend fun calculateTotoGroupPatternStats() {
        totoGroupPatternStats.calculateTotoGroupPatternStats()
    }
}