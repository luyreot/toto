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

        //fetchNewDrawings()

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

    private fun fetchNewDrawings(fetchFromBackupSite: Boolean = false) {
        if (fetchFromBackupSite) {
            WebCrawler649Backup.updateDrawings()
        } else {
            WebCrawler649.updateDrawings()
        }
    }
}