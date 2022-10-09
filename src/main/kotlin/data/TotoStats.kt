package data

import model.TotoType
import model.divideBy10

class TotoStats(
    totoType: TotoType
) {

    val totoNumbers = TotoNumbers(totoType)
    val totoNumberStats = TotoNumberStats(totoType, totoNumbers)
    val totoOddEvenPatternStats = TotoOddEvenPatternStats(totoType, totoNumbers)
    val totoLowHighPatternStats = TotoLowHighPatternStats(totoType, totoNumbers)
    val totoGroupPatternStats = TotoGroupPatternStats(totoType, totoNumbers, ::divideBy10)

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