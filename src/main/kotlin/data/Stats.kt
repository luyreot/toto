package data

import model.GroupStrategy.DIVIDE_BY_10
import model.TotoType

class Stats(
    private val totoType: TotoType,
    private val fromYear: Int? = null
) {

    val drawings = Drawings(totoType, fromYear)

    val numberStats = NumberStats(totoType, drawings, fromYear)

    val groupNumberStats = GroupNumberStats(totoType, drawings, fromYear)

    val drawingScoreStats = DrawingScoreStats(totoType, drawings, numberStats, fromYear)

    val predictOddEvenPattern = PredictOddEvenPattern(totoType)
    val oddEvenPatternStats = OddEvenPatternStats(totoType, drawings, predictOddEvenPattern, fromYear)

    val preditctLowHighPattern = PredictLowHighPattern(totoType)
    val lowHighPatternStats = LowHighPatternStats(totoType, drawings, preditctLowHighPattern, fromYear)

    val predictGroupPattern = PredictGroupPattern(totoType)
    val groupPatternStats = GroupPatternStats(totoType, drawings, DIVIDE_BY_10, predictGroupPattern, fromYear)

    val combinedPatternStats = CombinedPatternStats(totoType, drawings, DIVIDE_BY_10, fromYear)

    val groupPatternDeltaStats = GroupPatternDeltaStats(totoType, drawings, fromYear)

    val predictPatternOptimizer = PredictPatternOptimizer(
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
        DIVIDE_BY_10,
        combinedPatternStats,
        drawingScoreStats
    )

    val predictNextDrawing = PredictNextDrawing(
        totoType,
        drawings,
        fromYear,
        numberStats,
        DIVIDE_BY_10,
        drawingScoreStats,
        predictPatternOptimizer,
        groupNumberStats
    )

    fun loadNumbers() {
        drawings.loadNumbers()
        drawings.setUpDrawingsForTesting()
        drawings.extractDrawings()
        drawings.checkForDuplicateDrawings()
    }

    fun calculateNumberStats() {
        numberStats.calculateStats()
    }

    fun calculateNumberGroupStats() {
        groupNumberStats.calculateStats()
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

    fun optimizePredictedPatterns() {
        predictPatternOptimizer.optimizePredictedPatterns()
    }

    fun predictNextDrawing() {
        predictNextDrawing.predictNextDrawing()
    }

    // region Testing

    fun testOddEvenPredictionAlgo() {
        /*
        val periodIncrement = 8 // 2 drawings per week x 4 weeks = 1 month
        var predictedThreesCount = 0
        var predictedFoursCount = 0
        var predictedFivesCount = 0
        var predictedSixesCount = 0

        for (i in 1..111) {
            val period = periodIncrement * i
            val predict = PredictOddEvenPattern(totoType, period)
            OddEvenPatternStats(totoType, drawings, predict, fromYear).apply {
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
        */
    }

    fun testLowHighPredictionAlgo() {
        /*
        val periodIncrement = 8 // 2 drawings per week x 4 weeks = 1 month
        var predictedThreesCount = 0
        var predictedFoursCount = 0
        var predictedFivesCount = 0
        var predictedSixesCount = 0

        for (i in 1..111) {
            val period = periodIncrement * i
            val predict = PredictLowHighPattern(totoType, period)
            LowHighPatternStats(totoType, drawings, predict, fromYear).apply {
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
        */
    }

    fun testGroupPredictionAlgo() {
        /*
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
        */
    }

    // endregion Testing
}