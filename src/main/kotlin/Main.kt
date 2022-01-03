import crawler.WebCrawler649
import crawler.WebCrawler649Backup
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import logicNew.data.LottoStats
import logicNew.model.LottoType

class Main {

    companion object {

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

            runBlocking {
                async { lottoStats.calculateLottoNumberOccurrences() }
            }

            println("=== END ===")
        }
    }
}