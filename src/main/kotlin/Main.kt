import crawler.WebCrawler649
import crawler.WebCrawler649Backup
import data.TotoStats
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import model.TotoType

@DelicateCoroutinesApi
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("=== START ===")

        val totoStats = TotoStats(TotoType.D_6X49)

        runBlocking {
            //fetchNewDrawings()

            totoStats.loadTotoNumbers(2021, 2022)

            listOf(
                async { totoStats.calculateTotoNumberStats() },
                async { totoStats.calculateTotoOddEvenPatternStats() },
                async { totoStats.calculateTotoLowHighPatternStats() },
                async { totoStats.calculateTotoGroupPatternStats() }
            ).awaitAll()
        }

        println("=== END ===")
    }

    private suspend fun fetchNewDrawings(fetchFromBackupSite: Boolean = false) {
        if (fetchFromBackupSite) {
            WebCrawler649Backup.updateDrawings()
        } else {
            WebCrawler649.updateDrawings()
        }
    }
}