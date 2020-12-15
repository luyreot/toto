package main

import algorithm.Gen
import algorithm.Markov
import algorithm.Patterns
import utils.drawingsList
import utils.loadAllDrawings
import utils.loadDrawingsForYears
import utils.updateYearDrawings

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            updateYearDrawings()

//            loadAllDrawings()
//            printDuplicateDrawings("All")

//            loadDrawingsForYears("2017", "2018", "2019, "2020"")
//            printDuplicateDrawings("2017_2018_2019_2020")

//            loadDrawingsForYears("2020")
//            printDuplicateDrawings("2020")

//            Patterns.generatePatterns()
//            Patterns.sortPatterns()
//            Markov.train()
//            Markov.sortChains()
//            Gen.gen()

            println()
        }

        private fun printDuplicateDrawings(years: String) {
            println("Duplicate drawings for years - $years ::: ${drawingsList.count() - drawingsList.toSet().size}")
        }

    }

}