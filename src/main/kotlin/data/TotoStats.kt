package data

import model.TotoGroupStrategy.DIVIDE_BY_10
import model.TotoType

class TotoStats(
    private val totoType: TotoType
) {

    val totoNumbers = TotoNumbers(totoType)

    val totoNumberStats = TotoNumberStats(totoType, totoNumbers)

    val totoPredictOddEvenPattern = TotoOddEvenPatternPredict(totoType)
    val totoOddEvenPatternStats = TotoOddEvenPatternStats(totoType, totoNumbers, totoPredictOddEvenPattern)

    val totoLowHighPatternPredict = TotoLowHighPatternPredict(totoType)
    val totoLowHighPatternStats = TotoLowHighPatternStats(totoType, totoNumbers, totoLowHighPatternPredict)

    val totoGroupPatternPredict = TotoGroupPatternPredict(totoType)
    val totoGroupPatternStats = TotoGroupPatternStats(totoType, totoNumbers, DIVIDE_BY_10, totoGroupPatternPredict)

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

    fun testOddEventLowHighPredictionAlgo() {
        /*
        var up = 0f
        var down = 0f
        var highestCorrectlyPredictedPatternPart = 0
        var highestCorrectlyPredictedPatternFull = 0
        for (u in 0..9) {
            for (d in 0..9) {
                val predict = TotoLowHighPatternPredict(totoType, up, down)
                TotoLowHighPatternStats(totoType, totoNumbers, predict).apply {
                    calculateTotoLowHighPatternStats()

                    println("UP - $up, DOWN - $down")
                    println("correctlyPredictedPatternPart - ${predict.correctlyPredictedPatternPart}")
                    println("correctlyPredictedPatternFull - ${predict.correctlyPredictedPatternFull}")
                    println("nextLowHighPattern - ${predict.nextLowHighPattern.map { it }}")
                    println("--------")
                }

                if (predict.correctlyPredictedPatternPart > highestCorrectlyPredictedPatternPart) {
                    highestCorrectlyPredictedPatternPart = predict.correctlyPredictedPatternPart
                }
                if (predict.correctlyPredictedPatternFull > highestCorrectlyPredictedPatternFull) {
                    highestCorrectlyPredictedPatternFull = predict.correctlyPredictedPatternFull
                }

                down += 0.1f
            }
            up += 0.1f
            down = 0f
        }

        println("highestCorrectlyPredictedPatternPart - $highestCorrectlyPredictedPatternPart")
        println("highestCorrectlyPredictedPatternFull - $highestCorrectlyPredictedPatternFull")
        */
    }

    fun testGroupPredictionAlgo() {
        /*
        var up = 0f
        var down = 0f
        var highestCorrectlyPredictedPatternPart = 0
        var highestCorrectlyPredictedPatternFull = 0
        for (u in 0..40) {
            for (d in 0..40) {
                val predict = TotoGroupPatternPredict(totoType, up, down)
                TotoGroupPatternStats(totoType, totoNumbers, DIVIDE_BY_10, predict).apply {
                    calculateTotoGroupPatternStats()

                    println("UP - $up, DOWN - $down")
                    println("correctlyPredictedPatternPart - ${predict.correctlyPredictedPatternPart}")
                    println("correctlyPredictedPatternFull - ${predict.correctlyPredictedPatternFull}")
                    println("nextGroupPattern - ${predict.nextGroupPattern.map { it }}")
                    println("--------")
                }

                if (predict.correctlyPredictedPatternPart > highestCorrectlyPredictedPatternPart) {
                    highestCorrectlyPredictedPatternPart = predict.correctlyPredictedPatternPart
                }
                if (predict.correctlyPredictedPatternFull > highestCorrectlyPredictedPatternFull) {
                    highestCorrectlyPredictedPatternFull = predict.correctlyPredictedPatternFull
                }

                down += 0.1f
            }
            up += 0.1f
            down = 0f
        }

        println("highestCorrectlyPredictedPatternPart - $highestCorrectlyPredictedPatternPart")
        println("highestCorrectlyPredictedPatternFull - $highestCorrectlyPredictedPatternFull")
        */
    }
}