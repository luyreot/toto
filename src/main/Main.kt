package main

import impl.crawler.WebCrawler
import impl.data.Data
import old.algorithm.Markov
import old.algorithm.Patterns
import old.algorithm.Predict
import old.service.drawingsList

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            Data.loadDrawings("2017")

            println()
        }

        private fun doOtherStuff() {

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