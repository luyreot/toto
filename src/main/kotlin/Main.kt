import crawler.WebCrawler649
import crawler.WebCrawler649Backup
import kotlinx.coroutines.*
import logicNew.data.TotoStats
import logicNew.model.TotoType

@DelicateCoroutinesApi
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("=== START ===")

        val fetchNewDrawings = false
        val fetchNewDrawingsFromBackupSite = false
        if (fetchNewDrawings) {
            if (fetchNewDrawingsFromBackupSite) {
                WebCrawler649Backup.updateDrawings()
            } else {
                WebCrawler649.updateDrawings()
            }
        }

        val totoStats = TotoStats(TotoType.D_6X49)
        totoStats.loadTotoNumbers(2021, 2022)

        GlobalScope.launch {
            listOf(
                async { totoStats.calculateTotoNumberStats() },
                async { totoStats.calculateTotoOddEvenPatternStats() },
                async { totoStats.calculateTotoLowHighPatternStats() },
                async { totoStats.calculateTotoGroupPatternStats() }
            ).awaitAll()
        }
        Thread.sleep(100000000000L)

        println("=== END ===")
    }
}