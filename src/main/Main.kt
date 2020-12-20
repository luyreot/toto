package main

import algorithm.Markov
import algorithm.Patterns
import algorithm.Predict
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
//            loadDrawingsForYears("2017", "2018", "2019")
//            loadDrawingsForYears("2019")
            printDuplicateDrawingsCount()

            Patterns.generate()
            Patterns.calculateProbabilities()
            Patterns.sort()
            Markov.trainChains()
            Markov.sortChains()
            Predict.predict()

            println()
        }

        private fun printDuplicateDrawingsCount(years: String = "*") {
            println("Duplicate drawings for years $years – ${drawingsList.count() - drawingsList.toSet().size}")
            // currently 6 duplicate drawings
        }

    }

}