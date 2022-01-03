import crawler.WebCrawler649
import crawler.WebCrawler649Backup
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import logicNew.data.DrawingsStats
import logicNew.model.DrawingType

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

            val stats = DrawingsStats(DrawingType.D_6X49)
            stats.loadNumbers(2021, 2022)

            runBlocking {
                async { stats.calculateNumberOccurrences() }

            }

            println("=== END ===")
        }
    }
}