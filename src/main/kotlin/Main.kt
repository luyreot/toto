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
        println("=== START ===")

        //fetchNewDrawings()

        TotoStats(TotoType.D_6X49).apply {
            loadTotoNumbers(2019, 2020, 2021, 2022)

            listOf(
                launchThread { calculateTotoNumberStats() },
                launchThread { calculateTotoOddEvenPatternStats() },
                launchThread { calculateTotoLowHighPatternStats() },
                launchThread { calculateTotoGroupPatternStats() }
            ).forEach { thread ->
                thread.join()
            }

            testOddEventLowHighPredictionAlgo()
            testGroupPredictionAlgo()

            println(doesDrawingExists(intArrayOf(8, 10, 21, 27, 36, 44)))
            println(doesDrawingExists(intArrayOf(8, 10, 23, 27, 34, 42)))
            println(doesDrawingExists(intArrayOf(8, 12, 21, 27, 36, 46)))
            println(doesDrawingExists(intArrayOf(10, 12, 23, 27, 34, 46)))

            println()
        }

        println("=== END ===")
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