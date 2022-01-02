import crawler.WebCrawler649
import crawler.WebCrawler649Backup
import data.Chains
import data.Drawings
import data.Patterns
import util.Helper

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val fetchNewDrawings = false
            val fetchNewDrawingsFromBackupSite = false
            if (fetchNewDrawings) {
                if (fetchNewDrawingsFromBackupSite) {
                    WebCrawler649Backup.updateDrawings()
                } else {
                    WebCrawler649.updateDrawings()
                }
            }

            val triggerOldLogic = false
            if (triggerOldLogic) {
                Drawings.loadDrawings()
                Helper.printDuplicateDrawingsCount()
                Patterns
                Chains
            }

            println()
        }

    }

}