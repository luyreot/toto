import crawler.WebCrawler
import deeplearning.testNeuralNetwork
import model.TotoType
import myalgo.Drawings
import myalgo.predictViaNumberDistributionPerPosition
import util.Logg
import util.deepLearning
import util.webCrawl

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

        val totoType = TotoType.T_6X49
        val allDrawings = Drawings(totoType, 0)

        if (deepLearning) {
            testNeuralNetwork()
            return
        }

//        allDataClasses(totoType)
        predictViaNumberDistributionPerPosition(totoType, allDrawings)
//        backtestPredictViaNumberDistributionPerPosition(totoType, allDrawings)
//        backtestCooccurrenceMatrix(totoType, allDrawings)

        Logg.p("=== MAIN END ===")
    }
}