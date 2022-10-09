package data

import model.TotoType
import model.divideBy10

class TotoStats(
    totoType: TotoType
) {

    val totoNumbers = TotoNumbers(totoType)
    val totoPredict = TotoPredict(totoType)
    val totoNumberStats = TotoNumberStats(totoType, totoNumbers, totoPredict)
    val totoOddEvenPatternStats = TotoOddEvenPatternStats(totoType, totoNumbers, totoPredict)
    val totoLowHighPatternStats = TotoLowHighPatternStats(totoType, totoNumbers, totoPredict)
    val totoGroupPatternStats = TotoGroupPatternStats(totoType, totoNumbers, ::divideBy10, totoPredict)

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