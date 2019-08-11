package akotlin.main

import akotlin.crawler.WebCrawler
import akotlin.service.DataService
import akotlin.utils.*

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            updateYearDrawings()
            loadAllDrawings()
//            loadDrawingsForYears("2018", "2019")

            println()
        }

        private fun updateYearDrawings() = WebCrawler(getTxtFileContents(getCurrentYearTxtFilePath())).crawl()

        private fun loadAllDrawings() = listFileNamesInInPath(PATH_TXT_FOLDER).forEach { file ->
            DataService.drawingsMap[file.name] =
                    getDrawingsFromFileContents(
                            file.name,
                            getTxtFileContents(file)
                    )
        }

        private fun loadDrawingsForYears(vararg years: String) = years.forEach { year ->
            DataService.drawingsMap[year] =
                    getDrawingsFromFileContents(
                            year,
                            getTxtFileContents(
                                    getYearTxtFilePath(year)
                            )
                    )

        }

    }

}