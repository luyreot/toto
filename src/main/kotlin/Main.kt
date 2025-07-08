import crawler.WebCrawler
import model.TotoType
import systems.deeplearning.analyzeNetwork
import systems.deeplearning.generateCombinations
import systems.deeplearning.predictNumbers
import systems.gapanalysis.analysePredict
import systems.numbercorrelations.Drawings
import systems.numbercorrelations.predictViaNumberDistributionPerPosition
import util.*
import visualizer.NumberViewer
import javax.swing.SwingUtilities

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        Logg.p("=== MAIN START ===")

        if (webCrawl) {
            WebCrawler().apply {
                crawl(TotoType.T_5X35)
                crawl(TotoType.T_6X42)
                crawl(TotoType.T_6X49)
            }
            return
        }

        val totoType =
            TotoType.T_5X35
//            TotoType.T_6X49
//            TotoType.T_6X42

        val predictionsSize: Int = when (totoType) {
            TotoType.T_6X49 -> 4
            TotoType.T_6X42 -> TODO()
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

            generateCombinations(totoType, predictedNumbers, predictionsSize)

            SwingUtilities.invokeLater {
                NumberViewer(totoType, predictedNumbers, totoType.size, 1)
            }

            return
        }

        if (gapAnalysis) {
            analysePredict(totoType, 2020, 20000)
//            backtest(totoType, 2020, 2024)

            return
        }

        Logg.p("=== MAIN END ===")
    }
}