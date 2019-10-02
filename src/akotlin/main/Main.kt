package akotlin.main

import akotlin.algorithm.Gen
import akotlin.algorithm.Markov
import akotlin.algorithm.Patterns
import akotlin.utils.drawingsList
import akotlin.utils.loadDrawingsForYears

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            updateYearDrawings()

//            loadAllDrawings()
//            printDuplicateDrawings("All")

            loadDrawingsForYears("2017", "2018", "2019")
            printDuplicateDrawings("2017_2018_2019")

//            loadDrawingsForYears("2019")
//            printDuplicateDrawings("2019")

            Patterns.generatePatterns()
            Patterns.sortPatterns()
            Markov.train()
            Markov.sortChains()
            Gen.gen()

            println()
        }

        private fun printDuplicateDrawings(years: String) {
            println("Duplicate drawings for years - $years ::: ${drawingsList.count() - drawingsList.toSet().size}")
        }

    }

}