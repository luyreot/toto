import crawler.WebCrawler649
import crawler.WebCrawler649Backup
import logicNew.data.DrawingsStats
import logicNew.model.drawing.DrawingType

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

            val stats = DrawingsStats(DrawingType.D_6x49)
            stats.loadNumbers(2020)


            println("=== END ===")
        }
    }
}