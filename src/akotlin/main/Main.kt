package akotlin.main

import akotlin.algorithm.Markov
import akotlin.algorithm.Patterns
import akotlin.utils.loadAllDrawings
import akotlin.utils.loadDrawingsForYears

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            updateYearDrawings()

            loadAllDrawings()
//            loadDrawingsForYears("2017", "2018", "2019")
//            loadDrawingsForYears("2019")

            Patterns.generatePatterns()
            Patterns.sortPatterns()
            Markov.train()
            Markov.sortChains()

            println()
        }

    }

}