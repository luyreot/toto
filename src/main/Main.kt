package main

import impl.data.Chains
import impl.data.Drawings
import impl.data.Patterns
import impl.util.Helper

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
//            WebCrawler.updateDrawings()

            Drawings.loadDrawings()
            Helper.printDuplicateDrawingsCount()
            Patterns
            Chains

            println()
        }

    }

}