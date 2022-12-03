import data.Stats
import model.TotoType
import util.ThreadUtils.launchThread
import util.TotoUtils.fetchNewDrawings

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("=== MAIN START ===")

//        fetchNewDrawings()

        Stats(TotoType.T_6X49).apply {
            loadNumbers()

            listOf(
                launchThread {
                    calculateNumberStats()
                    calculateDrawingScore()
                },
                launchThread { calculateOddEvenPatternStats() },
                launchThread { calculateLowHighPatternStats() },
                launchThread { calculateGroupPatternStats() },
                launchThread { calculateGroupPatternDeltaStats() },

                launchThread { calculateCombinedPatternStats() }
            ).forEach { thread ->
                thread.join()
            }

//            testOddEvenPredictionAlgo()
//            testLowHighPredictionAlgo()
//            testGroupPredictionAlgo()

            predictNextDrawing()

            println()
        }

        println("=== MAIN END ===")
    }
}