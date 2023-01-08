import data.Stats
import model.TotoType
import util.PredictionTester
import util.ThreadUtils.launchThread
import util.TotoUtils

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("=== MAIN START ===")

        TotoUtils.apply {
//            fetchNewDrawings()
        }

        PredictionTester.apply {
            isTestingPredictions = false
            startYear = 2022
            startIssue = 103

            do {
                println("======== 5x35 ========")
                performAlgo(Stats(TotoType.T_5X35))

                println("======== 6x42 ========")
                performAlgo(Stats(TotoType.T_6X42))

                println("======== 6x49 ========")
                performAlgo(Stats(TotoType.T_6X49))

                issueCounter++
            } while (isTestingPredictions)
        }

        println("=== MAIN END ===")
    }

    private fun performAlgo(stats: Stats) {
        stats.apply {
            loadNumbers()

            listOf(
                launchThread {
                    calculateNumberStats()
                    calculateDrawingScore()
                },
                launchThread { calculateNumberGroupStats() },
                launchThread { calculateOddEvenPatternStats() },
                launchThread { calculateLowHighPatternStats() },
                launchThread { calculateGroupPatternStats() },
                launchThread { calculateCombinedPatternStats() }
            ).forEach { thread ->
                thread.join()
            }

//            testGroupPredictionAlgo()

            optimizePredictedPatterns()
            predictNextDrawing()

            println()
        }
    }
}