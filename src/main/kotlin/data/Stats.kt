package data

import model.GroupStrategy.DELTA_SUBTRACT
import model.GroupStrategy.DIVIDE_BY_10
import model.TotoType

class Stats(
    private val totoType: TotoType,
    private val fromYear: Int? = null
) {

    val drawings = Drawings(totoType, fromYear)

    val numberStats = NumberStats(totoType, drawings, fromYear)

    val drawingScoreStats = DrawingScoreStats(totoType, drawings, numberStats, fromYear)

    val predictOddEvenPattern = PredictOddEvenPattern(totoType)
    val oddEvenPatternStats = OddEvenPatternStats(totoType, drawings, predictOddEvenPattern, fromYear)

    val preditctLowHighPattern = PredictLowHighPattern(totoType)
    val lowHighPatternStats = LowHighPatternStats(totoType, drawings, preditctLowHighPattern, fromYear)

    val predictGroupPattern = PredictGroupPattern(totoType)
    val groupPatternStats = GroupPatternStats(totoType, drawings, DIVIDE_BY_10, predictGroupPattern, fromYear)

    val combinedPatternStats = CombinedPatternStats(totoType, drawings, DIVIDE_BY_10, fromYear)

    val groupPatternDeltaStats = GroupPatternDeltaStats(totoType, drawings, fromYear)

    val nextDrawing = NextDrawing(
        totoType,
        drawings,
        fromYear,
        numberStats,
        oddEvenPatternStats,
        predictOddEvenPattern,
        lowHighPatternStats,
        preditctLowHighPattern,
        groupPatternStats,
        predictGroupPattern,
        groupPatternDeltaStats,
        DIVIDE_BY_10,
        DELTA_SUBTRACT,
        drawingScoreStats
    )

    fun loadNumbers() {
        drawings.loadNumbers()
        drawings.extractDrawings()
        drawings.checkForDuplicateDrawings()
    }

    fun calculateNumberStats() {
        numberStats.calculateStats()
    }

    fun calculateDrawingScore() {
        drawingScoreStats.calculateStats()
    }

    fun calculateOddEvenPatternStats() {
        oddEvenPatternStats.calculateStats()
    }

    fun calculateLowHighPatternStats() {
        lowHighPatternStats.calculateStats()
    }

    fun calculateGroupPatternStats() {
        groupPatternStats.calculateStats()
    }

    fun calculateCombinedPatternStats() {
        combinedPatternStats.calculateStats()
    }

    fun calculateGroupPatternDeltaStats() {
        groupPatternDeltaStats.calculateStats()
    }

    fun predictNextDrawing() {
        nextDrawing.populatePatternArrays()
        nextDrawing.predictNextDrawing()
    }

    // region Testing

    fun testOddEvenPredictionAlgo() {
        /*
        var up = 0.1f
        var down = 0.1f
        var divisorUp = 2
        var divisorDown = 2
        var correctlyPredictedIndexCount = 0
        var correctlyPredictedCount = 0
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
                        val predict = PredictOddEvenPattern(totoType, up, down, divisorUp, divisorDown)
                        OddEvenPatternStats(totoType, drawings, predict).apply {
                            calculateStats()

                            println("UP - $up, DOWN - $down")
                            println("Div UP - $divisorUp, Div DOWN - $divisorDown")
                            println("correctlyPredictedIndexCount - ${predict.correctlyPredictedIndexCount}")
                            println("correctlyPredictedCount - ${predict.correctlyPredictedCount}")
                            println("nextOddEvenPattern - ${predict.nextPattern.map { it }}")
                            println("--------")
                        }

                        if (predict.correctlyPredictedIndexCount > correctlyPredictedIndexCount) {
                            correctlyPredictedIndexCount = predict.correctlyPredictedIndexCount
                        }
                        if (predict.correctlyPredictedCount > correctlyPredictedCount) {
                            correctlyPredictedCount = predict.correctlyPredictedCount
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

        println("correctlyPredictedIndexCount - $correctlyPredictedIndexCount")
        println("correctlyPredictedCount - $correctlyPredictedCount")
        */
    }

    fun testLowHighPredictionAlgo() {
        /*
        var up = 0.1f
        var down = 0.1f
        var divisorUp = 2
        var divisorDown = 2
        var correctlyPredictedIndexCount = 0
        var correctlyPredictedCount = 0
        for (u in 0..9) {
            for (d in 0..9) {
                divisorUp = 2
                divisorDown = 2

                for (ud in 0..10) {
                    for (dd in 0..10) {
                        val predict = PredictLowHighPattern(totoType, up, down, divisorUp, divisorDown)
                        LowHighPatternStats(totoType, drawings, predict).apply {
                            calculateStats()

                            println("UP - $up, DOWN - $down")
                            println("Div UP - $divisorUp, Div DOWN - $divisorDown")
                            println("correctlyPredictedIndexCount - ${predict.correctlyPredictedIndexCount}")
                            println("correctlyPredictedCount - ${predict.correctlyPredictedCount}")
                            println("nextLowHighPattern - ${predict.nextPattern.map { it }}")
                            println("--------")
                        }

                        if (predict.correctlyPredictedIndexCount > correctlyPredictedIndexCount) {
                            correctlyPredictedIndexCount = predict.correctlyPredictedIndexCount
                        }
                        if (predict.correctlyPredictedCount > correctlyPredictedCount) {
                            correctlyPredictedCount = predict.correctlyPredictedCount
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

        println("correctlyPredictedIndexCount - $correctlyPredictedIndexCount")
        println("correctlyPredictedCount - $correctlyPredictedCount")
        */
    }

    fun testGroupPredictionAlgo() {
        val periodIncrement = 8 // 2 drawings per week x 4 weeks = 1 month
        var predictedThreesCount = 0
        var predictedFoursCount = 0
        var predictedFivesCount = 0
        var predictedSixesCount = 0

        for (i in 1..24) {
            val period = periodIncrement * i
            val predict = PredictGroupPattern(totoType, period)
            GroupPatternStats(totoType, drawings, DIVIDE_BY_10, predict, fromYear).apply {
                calculateStats()

                println("Period - $period")
                println("PredictedThreesCount - ${predict.predictedThreesCount}")
                println("PredictedFoursCount - ${predict.predictedFoursCount}")
                println("PredictedFivesCount - ${predict.predictedFivesCount}")
                println("PredictedSixesCount - ${predict.predictedSixesCount}")
                println("--- --- --- --- --- --- ---")

                if (predictedThreesCount < predict.predictedThreesCount) {
                    predictedThreesCount = predict.predictedThreesCount
                }
                if (predictedFoursCount < predict.predictedFoursCount) {
                    predictedFoursCount = predict.predictedFoursCount
                }
                if (predictedFivesCount < predict.predictedFivesCount) {
                    predictedFivesCount = predict.predictedFivesCount
                }
                if (predictedSixesCount < predict.predictedSixesCount) {
                    predictedSixesCount = predict.predictedSixesCount
                }
            }
        }

        println("Top Results")
        println("PredictedThreesCount - $predictedThreesCount")
        println("PredictedFoursCount - $predictedFoursCount")
        println("PredictedFivesCount - $predictedFivesCount")
        println("PredictedSixesCount - $predictedSixesCount")
    }

    // endregion Testing
}