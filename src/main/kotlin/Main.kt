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
            startFromYear = 2022
            startFromIssue = 1
        }

        do {
            // 1999
            Stats(TotoType.T_6X49).apply {
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
                    launchThread { calculateGroupPatternDeltaStats() },
                    launchThread { calculateCombinedPatternStats() }
                ).forEach { thread ->
                    thread.join()
                }

//                testOddEvenPredictionAlgo()
//                testLowHighPredictionAlgo()
//                testGroupPredictionAlgo()

                optimizePredictedPatterns()
                predictNextDrawing()

                println()
            }

            PredictionTester.apply {
                if (isTestingPredictions) {
                    startFromIssue = startFromIssue!! + 1
                }
            }
        } while (PredictionTester.isTestingPredictions && PredictionTester.startFromIssue!! < 104)

        println("=== MAIN END ===")
    }
}