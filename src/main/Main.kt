package main

import impl.data.Drawing
import impl.util.Helper
import old.algorithm.Markov
import old.algorithm.Patterns
import old.algorithm.Predict

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
//            WebCrawler.updateDrawings()

            Drawing.loadDrawings()
            Helper.printDuplicateDrawingsCount()

            println()
        }

        private fun doOtherStuff() {
            Patterns.generate()
            Patterns.calculateProbabilities()
            Patterns.sort()
            Markov.trainChains()
            Markov.sortChains()
            Predict.predict()
        }

    }

}