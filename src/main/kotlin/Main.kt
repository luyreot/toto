import data.TotoStats
import model.TotoType
import util.ThreadUtils.launchThread
import util.TotoUtils.fetchNewDrawings

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("=== MAIN START ===")

//        fetchNewDrawings()

        TotoStats(TotoType.D_6X49).apply {
            loadTotoNumbers()

            listOf(
                launchThread {
                    calculateTotoNumberStats()
                    calculateTotoDrawingScoreStats()
                },
                launchThread { calculateTotoOddEvenPatternStats() },
                launchThread { calculateTotoLowHighPatternStats() },
                launchThread { calculateTotoGroupPatternStats() },
                launchThread { calculateTotoGroupPatternDeltaStats() }
            ).forEach { thread ->
                thread.join()
            }

//            testOddEvenPredictionAlgo()
//            testLowHighPredictionAlgo()
//            testGroupPredictionAlgo()
//            testGroupPredictionDeltaAlgo()

            predictNextDrawing()

            println()
        }

        println("=== MAIN END ===")
    }
}