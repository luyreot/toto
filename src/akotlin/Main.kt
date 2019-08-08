package akotlin

import akotlin.utils.getCurrentYearTxtFilePath
import akotlin.utils.getTxtFileContents
import akotlin.webcrawler.TotoWebCrawler

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            updateYearDrawings()
        }

        fun updateYearDrawings() {
            TotoWebCrawler(getTxtFileContents(getCurrentYearTxtFilePath())).crawl()
        }
    }

}