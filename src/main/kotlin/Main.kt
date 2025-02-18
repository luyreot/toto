import crawler.WebCrawler
import model.TotoType
import systems.deeplearning.trainNeuralNetworkBatch
import systems.numbercorrelations.Drawings
import systems.numbercorrelations.predictViaNumberDistributionPerPosition
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

        if (deepLearning) {
//            trainNeuralNetworkForIndividualNumbers(totoType)
            trainNeuralNetworkBatch(totoType)
//            predictNeuralNetwork(totoType)
            return
        }

        val allDrawings = Drawings(totoType, 0)

//        allDataClasses(totoType)
        predictViaNumberDistributionPerPosition(totoType, allDrawings)

        Logg.p("=== MAIN END ===")
    }
}