import crawler.WebCrawler
import model.TotoType
import systems.deeplearning.nnTrainIndividualNumber
import systems.gapanalysis.analysePredict
import systems.numbercorrelations.Drawings
import systems.numbercorrelations.predictViaNumberDistributionPerPosition
import util.*

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

        val totoType = TotoType.T_5X35

        if (numberCorrelations) {
            val allDrawings = Drawings(totoType, 0)
//            allDataClasses(totoType)
            predictViaNumberDistributionPerPosition(totoType, allDrawings)

            return
        }

        if (deepLearning) {
//            nnTrainDrawFullNumberSet(totoType)
//            nnTestDrawFullNumberSet(totoType)
            nnTrainIndividualNumber(totoType, 1)

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