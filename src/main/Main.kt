package main

import impl.data.Drawing
import impl.data.Pattern
import impl.util.Helper
import old.algorithm.Markov
import old.algorithm.Predict

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
//            WebCrawler.updateDrawings()

            Drawing.loadDrawings()
            Helper.printDuplicateDrawingsCount()
            Pattern


            println()
        }

        private fun doOtherStuff() {
            Markov.trainChains()
            Markov.sortChains()
            Predict.predict()
        }

    }

}