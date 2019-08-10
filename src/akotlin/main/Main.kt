package akotlin.main

import akotlin.service.DataService
import akotlin.utils.*
import akotlin.crawler.WebCrawler

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            updateYearDrawings()
            loadDrawingsForYears("2018", "2019")

            println()
        }

        private fun updateYearDrawings() =
                WebCrawler(getTxtFileContents(getCurrentYearTxtFilePath())).crawl()

        private fun loadDrawingsForYears(vararg years: String) = years.forEach { year ->
            DataService.drawingsMap[year] =
                    convertStringListToDrawingsList(
                            year,
                            getTxtFileContents(
                                    getYearTxtFilePath(year)
                            )
                    )

        }

    }

}