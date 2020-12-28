package main

import impl.data.Drawings
import impl.data.Patterns
import impl.util.Helper
import old.algorithm.Markov

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
//            WebCrawler.updateDrawings()

            Drawings.loadDrawings()
            Helper.printDuplicateDrawingsCount()
            Patterns

            println()
        }

        private fun doOtherStuff() {
            Markov.trainChains()
            Markov.sortChains()
            Predict.predict()
        }

    }

}