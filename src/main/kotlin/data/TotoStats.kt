package data

import model.TotoGroupStrategy.DIVIDE_BY_10
import model.TotoType

class TotoStats(
    private val totoType: TotoType,
    private val fromYear: Int? = null
) {

    val totoNumbers = TotoDrawnNumbers(totoType, fromYear)

    val totoNumberStats = TotoNumberStats(totoType, totoNumbers, fromYear)

    val totoDrawingScoreStats = TotoDrawingScoreStats(totoType, totoNumbers, totoNumberStats, fromYear)

    val totoOddEvenPatternPredict = TotoOddEvenPatternPredict(totoType)
    val totoOddEvenPatternStats = TotoOddEvenPatternStats(totoType, totoNumbers, totoOddEvenPatternPredict, fromYear)

    val totoLowHighPatternPredict = TotoLowHighPatternPredict(totoType)
    val totoLowHighPatternStats = TotoLowHighPatternStats(totoType, totoNumbers, totoLowHighPatternPredict, fromYear)

    val totoGroupPatternPredict = TotoGroupPatternPredict(totoType)
    val totoGroupPatternStats = TotoGroupPatternStats(totoType, totoNumbers, DIVIDE_BY_10, totoGroupPatternPredict, fromYear)

    val totoGroupPatternDeltaPredict = TotoGroupPatternDeltaPredict(totoType)
    val totoGroupPatternDeltaStats = TotoGroupPatternDeltaStats(totoType, totoNumbers, totoGroupPatternDeltaPredict, fromYear)

    val totoNextDrawing = TotoNextDrawing(
        totoType,
        totoNumbers,
        fromYear,
        totoNumberStats,
        totoOddEvenPatternStats,
        totoOddEvenPatternPredict,
        totoLowHighPatternStats,
        totoLowHighPatternPredict,
        totoGroupPatternStats,
        totoGroupPatternPredict,
        totoGroupPatternDeltaStats,
        totoGroupPatternDeltaPredict,
        DIVIDE_BY_10,
        totoDrawingScoreStats
    )

    fun loadTotoNumbers(vararg years: Int) {
        totoNumbers.loadTotoNumbers(*years)
        totoNumbers.extractDrawings()
    }

    fun calculateTotoNumberStats() {
        totoNumberStats.calculateStats()
    }

    fun calculateTotoDrawingScoreStats() {
        totoDrawingScoreStats.calculateTotoDrawingScoreStats()
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

    fun calculateTotoGroupPatternDeltaStats() {
        totoGroupPatternDeltaStats.calculateTotoGroupPatternDeltaStats()
    }

    fun predictNextDrawing() {
        totoNextDrawing.populateArrays()
        totoNextDrawing.predictNextDrawing()
    }

    // region Testing

    fun testOddEvenPredictionAlgo() {
        /*
        var up = 0.1f
        var down = 0.1f
        var divisorUp = 2
        var divisorDown = 2
        var highestCorrectlyPredictedPatternPart = 0
        var highestCorrectlyPredictedPatternFull = 0
        var sUp = 0f
        var sDown = 0f
        var sDivisorUp = 0
        var sDivisorDown = 0
        for (u in 0..9) {
            for (d in 0..9) {
                divisorUp = 2
                divisorDown = 2

                for (ud in 0..10) {
                    for (dd in 0..10) {
                        val predict = TotoOddEvenPatternPredict(totoType, up, down, divisorUp, divisorDown)
                        TotoOddEvenPatternStats(totoType, totoNumbers, predict).apply {
                            calculateTotoOddEvenPatternStats()

                            println("UP - $up, DOWN - $down")
                            println("Div UP - $divisorUp, Div DOWN - $divisorDown")
                            println("correctlyPredictedPatternPart - ${predict.correctlyPredictedPatternPart}")
                            println("correctlyPredictedPatternFull - ${predict.correctlyPredictedPatternFull}")
                            println("nextOddEvenPattern - ${predict.nextOddEvenPattern.map { it }}")
                            println("--------")
                        }

                        if (predict.correctlyPredictedPatternPart > highestCorrectlyPredictedPatternPart) {
                            highestCorrectlyPredictedPatternPart = predict.correctlyPredictedPatternPart
                        }
                        if (predict.correctlyPredictedPatternFull > highestCorrectlyPredictedPatternFull) {
                            highestCorrectlyPredictedPatternFull = predict.correctlyPredictedPatternFull
                            sUp = up
                            sDown = down
                            sDivisorUp = divisorUp
                            sDivisorDown = divisorDown
                        }
                        divisorDown += 1
                    }
                    divisorUp += 1
                    divisorDown = 2
                }
                down += 0.1f
            }
            up += 0.1f
            down = 0.1f
        }

        println("highestCorrectlyPredictedPatternPart - $highestCorrectlyPredictedPatternPart")
        println("highestCorrectlyPredictedPatternFull - $highestCorrectlyPredictedPatternFull")
        */
    }

    fun testLowHighPredictionAlgo() {
        /*
        var up = 0.1f
        var down = 0.1f
        var divisorUp = 2
        var divisorDown = 2
        var highestCorrectlyPredictedPatternPart = 0
        var highestCorrectlyPredictedPatternFull = 0
        for (u in 0..9) {
            for (d in 0..9) {
                divisorUp = 2
                divisorDown = 2

                for (ud in 0..10) {
                    for (dd in 0..10) {
                        val predict = TotoLowHighPatternPredict(totoType, up, down, divisorUp, divisorDown)
                        TotoLowHighPatternStats(totoType, totoNumbers, predict).apply {
                            calculateTotoLowHighPatternStats()

                            println("UP - $up, DOWN - $down")
                            println("Div UP - $divisorUp, Div DOWN - $divisorDown")
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
                        divisorDown += 1
                    }
                    divisorUp += 1
                    divisorDown = 2
                }
                down += 0.1f
            }
            up += 0.1f
            down = 0.1f
        }

        println("highestCorrectlyPredictedPatternPart - $highestCorrectlyPredictedPatternPart")
        println("highestCorrectlyPredictedPatternFull - $highestCorrectlyPredictedPatternFull")
        */
    }

    fun testGroupPredictionAlgo() {
        /*
        var up = 0.1f
        var down = 0.1f
        var divisorUp = 2
        var divisorDown = 2
        var highestCorrectlyPredictedPatternPart = 0
        var highestCorrectlyPredictedPatternFull = 0
        var sUp = 0f
        var sDown = 0f
        var sDivisorUp = 0
        var sDivisorDown = 0
        for (u in 0..40) {
            for (d in 0..40) {
                divisorUp = 2
                divisorDown = 2

                for (ud in 0..10) {
                    for (dd in 0..10) {
                        val predict = TotoGroupPatternPredict(totoType, up, down, divisorUp, divisorDown)
                        TotoGroupPatternStats(totoType, totoNumbers, DIVIDE_BY_10, predict).apply {
                            calculateTotoGroupPatternStats()

                            println("UP - $up, DOWN - $down")
                            println("Div UP - $divisorUp, Div DOWN - $divisorDown")
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
                            sUp = up
                            sDown = down
                            sDivisorUp = divisorUp
                            sDivisorDown = divisorDown
                        }
                        divisorDown += 1
                    }
                    divisorUp += 1
                    divisorDown = 2
                }
                down += 0.1f
            }
            up += 0.1f
            down = 0.1f
        }

        println("highestCorrectlyPredictedPatternPart - $highestCorrectlyPredictedPatternPart")
        println("highestCorrectlyPredictedPatternFull - $highestCorrectlyPredictedPatternFull")
        */
    }

    fun testGroupPredictionDeltaAlgo() {
        /*
        val bestPatterns = mutableListOf<BestPatternPredictStats>()
        var up = 0f
        var down = 0f
        var highestCorrectlyPredictedPatternPart = 0
        var highestCorrectlyPredictedPatternFull = 0
        for (u in 0..490) {
            for (d in 0..490) {
                val predict = TotoGroupPatternDeltaPredict(totoType, up, down)
                TotoGroupPatternDeltaStats(totoType, totoNumbers, predict).apply {
                    calculateTotoGroupPatternDeltaStats()

                    println("UP - $up, DOWN - $down")
                    println("correctlyPredictedPatternPart - ${predict.correctlyPredictedPatternPart}")
                    println("correctlyPredictedPatternFull - ${predict.correctlyPredictedPatternFull}")
                    println("nextGroupPattern - ${predict.nextGroupPattern.map { it }}")
                    println("--------")
                }

                if (predict.correctlyPredictedPatternPart > highestCorrectlyPredictedPatternPart) {
                    highestCorrectlyPredictedPatternPart = predict.correctlyPredictedPatternPart

                    bestPatterns.clear()
                    bestPatterns.add(BestPattern(up, down, highestCorrectlyPredictedPatternPart, highestCorrectlyPredictedPatternFull))
                } else if (predict.correctlyPredictedPatternPart == highestCorrectlyPredictedPatternPart) {
                    bestPatterns.add(BestPattern(up, down, highestCorrectlyPredictedPatternPart, highestCorrectlyPredictedPatternFull))
                }

                if (predict.correctlyPredictedPatternFull > highestCorrectlyPredictedPatternFull) {
                    highestCorrectlyPredictedPatternFull = predict.correctlyPredictedPatternFull

                    bestPatterns.clear()
                    bestPatterns.add(BestPattern(up, down, highestCorrectlyPredictedPatternPart, highestCorrectlyPredictedPatternFull))
                } else if (predict.correctlyPredictedPatternFull == highestCorrectlyPredictedPatternFull) {
                    bestPatterns.add(BestPattern(up, down, highestCorrectlyPredictedPatternPart, highestCorrectlyPredictedPatternFull))
                }

                down += 0.1f
            }
            up += 0.1f
            down = 0f
        }

        println("highestCorrectlyPredictedPatternPart - $highestCorrectlyPredictedPatternPart")
        println("highestCorrectlyPredictedPatternFull - $highestCorrectlyPredictedPatternFull")
        println()
//        */
    }

    // endregion Testing
}