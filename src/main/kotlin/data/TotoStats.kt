package data

import model.TotoGroupStrategy.DIVIDE_BY_10
import model.TotoType

class TotoStats(
    totoType: TotoType
) {

    val totoNumbers = TotoNumbers(totoType)

    val totoNumberStats = TotoNumberStats(totoType, totoNumbers)

    val totoPredictOddEvenPattern = TotoOddEvenPatternPredict(totoType)
    val totoOddEvenPatternStats = TotoOddEvenPatternStats(totoType, totoNumbers, totoPredictOddEvenPattern)

    val totoLowHighPatternStats = TotoLowHighPatternStats(totoType, totoNumbers)

    val totoGroupPatternStats = TotoGroupPatternStats(totoType, totoNumbers, DIVIDE_BY_10)

    fun loadTotoNumbers(vararg years: Int) {
        totoNumbers.loadTotoNumbers(*years)
    }

    fun calculateTotoNumberStats() {
        totoNumberStats.calculateStats()
    }

    fun calculateTotoOddEvenPatternStats() {
        totoOddEvenPatternStats.calculateTotoOddEvenPatternStats()
    }

    fun calculateTotoLowHighPatternStats() {
        totoLowHighPatternStats.calculateTotoLowHighPatternStats()
    }

    fun calculateTotoGroupPatternStats() {
        totoGroupPatternStats.calculateTotoGroupPatternStats()
    }
}