package main

import impl.crawler.WebCrawler
import old.algorithm.Markov
import old.algorithm.Patterns
import old.algorithm.Predict
import old.service.drawingsList
import old.utils.loadAllDrawings

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            WebCrawler.updateDrawings()




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