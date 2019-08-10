package akotlin

import akotlin.utils.*
import akotlin.webcrawler.TotoWebCrawler

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            updateYearDrawings()
            loadDrawingsForYears("2018", "2019")

            println()
        }

        private fun updateYearDrawings() =
                TotoWebCrawler(getTxtFileContents(getCurrentYearTxtFilePath())).crawl()

        private fun loadDrawingsForYears(vararg years: String) = years.forEach { year ->
            TotoService.drawingsMap[year] =
                    convertStringListToDrawingsList(
                            year,
                            getTxtFileContents(
                                    getYearTxtFilePath(year)
                            )
                    )

        }

    }

}