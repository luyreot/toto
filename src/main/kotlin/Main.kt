
import crawler.WebCrawler
import model.TotoType
import systems.deeplearning.analyzeNetwork
import systems.deeplearning.generateCombinations
import systems.deeplearning.predictNumbers
import systems.occurrence.doAlgo
import systems.patterns.Drawings
import systems.patterns.predictViaNumberDistributionPerPosition
import util.*
import visualizer.NumberViewer
import javax.swing.SwingUtilities

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        Logger.p("=== MAIN START ===")

        if (webCrawl) {
            WebCrawler().apply {
                crawl(TotoType.T_5X35)
                crawl(TotoType.T_6X42)
                crawl(TotoType.T_6X49)
            }
        }

        val predictionsSize: Int = when (totoType) {
            TotoType.T_6X49 -> 4
            TotoType.T_6X42 -> 4
            TotoType.T_5X35 -> 4
        }

        println("Toto Type - ${totoType.name}")

        if (numberCorrelations) {
            val allDrawings = Drawings(totoType, 0)
//            allDataClasses(totoType)
            predictViaNumberDistributionPerPosition(totoType, allDrawings, predictionsSize)

            return
        }

        if (deepLearning) {
//            trainNetwork(totoType)
            analyzeNetwork(totoType)

            val predictedNumbers = predictNumbers(totoType)

            generateCombinations(totoType.size, predictedNumbers, predictionsSize)

            if (showUiPicker) {
                SwingUtilities.invokeLater {
                    NumberViewer(totoType, predictedNumbers, totoType.size, 1)
                }
            }

            return
        }

        if (newAnalysis) {
            doAlgo()

            return
        }

        Logger.p("=== MAIN END ===")
    }
}