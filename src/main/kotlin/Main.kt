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
            ).forEach {
                it.join()
            }

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