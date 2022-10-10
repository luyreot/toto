package data

import model.TotoGroupStrategy.DIVIDE_BY_10
import model.TotoType

class TotoStats(
    totoType: TotoType
) {

    val totoNumbers = TotoNumbers(totoType)
    val totoPredict = TotoPredict(totoType)
    val totoNumberStats = TotoNumberStats(totoType, totoNumbers, totoPredict)
    val totoOddEvenPatternStats = TotoOddEvenPatternStats(totoType, totoNumbers, totoPredict)
    val totoLowHighPatternStats = TotoLowHighPatternStats(totoType, totoNumbers, totoPredict)
    val totoGroupPatternStats = TotoGroupPatternStats(totoType, totoNumbers, DIVIDE_BY_10, totoPredict)

    fun loadTotoNumbers(
        vararg years: Int
    ) {
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