import crawler.WebCrawler649
import crawler.WebCrawler649Backup
import kotlinx.coroutines.*
import logicNew.data.LottoStats
import logicNew.model.LottoType

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

        val lottoStats = LottoStats(LottoType.D_6X49)
        lottoStats.loadLottoNumbers(2021, 2022)

        GlobalScope.launch {
            listOf(
                async { lottoStats.calculateLottoNumberStats() },
                async { lottoStats.calculateLottoOddEvenPatternStats() },
                async { lottoStats.calculateLottoLowHighPatternStats() }
            ).awaitAll()
        }
        Thread.sleep(1000L)

        println("=== END ===")
    }
}