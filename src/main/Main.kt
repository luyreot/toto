package main

import algorithm.Generate
import algorithm.Markov
import algorithm.Patterns
import service.drawingsList
import utils.loadAllDrawings
import utils.updateYearDrawings

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            updateYearDrawings()
//            doOtherStuff()
        }

        private fun doOtherStuff() {
            loadAllDrawings()
//            printDuplicateDrawings("All")

//            loadDrawingsForYears("2017", "2018", "2019")
//            printDuplicateDrawings("2017_2018_2019")

//            loadDrawingsForYears("2019")
//            printDuplicateDrawings("2019")

            Patterns.generate()
            Patterns.calculateProbabilities()
            Patterns.sort()
            Markov.train()
            Markov.sortChains()
            Generate.getAllPossibleColorPatterns()
            Generate.findMissingColorPatterns()
//            Predict.predict()

            println()
        }

        private fun printDuplicateDrawings(years: String) {
            println("Duplicate drawings for years - $years ::: ${drawingsList.count() - drawingsList.toSet().size}")
        }

    }

}