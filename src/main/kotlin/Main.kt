import crawler.WebCrawler
import crawler.WebCrawlerBackup
import data.Chains
import data.Drawings
import data.Patterns
import util.Helper

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            WebCrawler.updateDrawings()
//            WebCrawlerBackup.updateDrawings()

            /*
            Drawings.loadDrawings()
            Helper.printDuplicateDrawingsCount()
            Patterns
            Chains
            */

            println()
        }

    }

}