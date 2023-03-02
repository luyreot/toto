import data.Stats
import model.TotoType
import util.GlobalConfig
import util.ThreadUtils.launchThread
import util.TotoUtils

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("=== MAIN START ===")

        GlobalConfig.apply {
            fetchNewDrawings = false
            checkPredictionScore = false
            loadPreviousRandomPicks = false
            calculateDerivedPredictions = false
            savePredictionsToFile = false

            GlobalConfig.PredictionScoreTester.apply {
                startYear = 2023
                startIssue = 18
            }

            if (fetchNewDrawings) {
                TotoUtils.fetchNewDrawings()
            }

            do {
                if (performAlgoForType[0]) {
                    println("======== 5x35 ========")
                    performAlgo(Stats(TotoType.T_5X35))
                }

                if (performAlgoForType[1]) {
                    println("======== 6x42 ========")
                    performAlgo(Stats(TotoType.T_6X42))
                }

                if (performAlgoForType[2]) {
                    println("======== 6x49 ========")
                    performAlgo(Stats(TotoType.T_6X49))
                }

                GlobalConfig.PredictionScoreTester.counter++
            } while (checkPredictionScore)
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