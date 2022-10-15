import crawler.WebCrawler649
import crawler.WebCrawler649Backup
import data.TotoStats
import kotlinx.coroutines.DelicateCoroutinesApi
import model.TotoType
import kotlin.concurrent.thread

@DelicateCoroutinesApi
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("=== MAIN START ===")

//        fetchNewDrawings()

        TotoStats(TotoType.D_6X49).apply {
            loadTotoNumbers()

            listOf(
                launchThread { calculateTotoNumberStats() },
                launchThread { calculateTotoOddEvenPatternStats() },
                launchThread { calculateTotoLowHighPatternStats() },
                launchThread { calculateTotoGroupPatternStats() },
                launchThread { calculateTotoGroupPatternDeltaStats() }
            ).forEach { thread ->
                thread.join()
            }

//            testOddEventLowHighPredictionAlgo()
//            testGroupPredictionAlgo()
//            testGroupPredictionDeltaAlgo()

            predictNextDrawing()

            println()
        }

        println("=== MAIN END ===")
    }

    private fun fetchNewDrawings(fetchFromBackupSite: Boolean = false) {
        if (fetchFromBackupSite) {
            WebCrawler649Backup.updateDrawings()
        } else {
            WebCrawler649.updateDrawings()
        }
    }

    private fun launchThread(method: () -> Unit) = thread(true) {
        method.invoke()
    }
}