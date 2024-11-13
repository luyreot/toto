import algo.PredictViaNumberDistributionPerPosition
import crawler.WebCrawler
import data.*
import model.TotoType
import test.BacktestCooccurrenceMatrix
import test.BacktestNumberDistributionPerPosition
import util.Constants
import util.Logg
import util.webCrawl

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        Logg.p("=== MAIN START ===")

        // Setup Global Configs
        if (webCrawl()) return

        val totoType = TotoType.T_6X49
        val allDrawings = Drawings(totoType, 0)

//        allDataClasses(totoType)
        predictViaNumberDistributionPerPosition(totoType, allDrawings)
//        backtestPredictViaNumberDistributionPerPosition(totoType, allDrawings)
//        backtestCooccurrenceMatrix(totoType, allDrawings)

        Logg.p("=== MAIN END ===")
    }

    private fun webCrawl(): Boolean {
        if (!webCrawl) return false

        WebCrawler().apply {
            crawl(TotoType.T_5X35)
            crawl(TotoType.T_6X42)
            crawl(TotoType.T_6X49)
        }

        return true
    }

    private fun allDataClasses(totoType: TotoType) {
        val allDrawings = Drawings(totoType, 0)
        val yearFilter = Constants.PAGE_YEAR.toInt() - 10
        val filteredDrawings = allDrawings.drawings.filter { it.year >= yearFilter }

        val numberTable = NumberTable(totoType, filteredDrawings)
        val numberIntervals = NumberIntervals(totoType, filteredDrawings)
        val numberHotCold = NumberHotCold(totoType, filteredDrawings, numberTable.numbers)
        val numberDistributionPerPosition = NumberDistributionPerPosition(totoType, filteredDrawings)

        val groupPatterns = GroupPatterns(filteredDrawings)
        val groupPatternIntervals = GroupPatternIntervals(groupPatterns.patterns.keys, filteredDrawings)
        val lowHighPatterns = LowHighPatterns(filteredDrawings)
        val lowHighPatternIntervals = LowHighPatternIntervals(lowHighPatterns.patterns.keys, filteredDrawings)
        val oddEvenPatterns = OddEvenPatterns(filteredDrawings)
        val oddEvenPatternIntervals = OddEvenPatternIntervals(oddEvenPatterns.patterns.keys, filteredDrawings)

        val numberToPatternCorrelations = NumberToPatternCorrelations(filteredDrawings)
        val groupPatternToPatternCorrelations = GroupPatternToPatternCorrelations(filteredDrawings)
        val lowHighPatternToPatternCorrelations = LowHighPatternToPatternCorrelations(filteredDrawings)
        val oddEvenPatternToPatternCorrelations = OddEvenPatternToPatternCorrelations(filteredDrawings)

        val sameDrawingCombinations = SameDrawingCombinations(filteredDrawings, size = 2)
        val subsequentDrawingCombinations = SubsequentDrawingCombinations(filteredDrawings, size = 3)

        println()
    }

    private fun predictViaNumberDistributionPerPosition(totoType: TotoType, allDrawings: Drawings) {
        PredictViaNumberDistributionPerPosition(totoType, allDrawings).apply {
            val yearFilter = when (totoType) {
                TotoType.T_6X49 -> Constants.PAGE_YEAR.toInt() - 20
                TotoType.T_6X42 -> Constants.PAGE_YEAR.toInt() - 20 // Not tested
                TotoType.T_5X35 -> Constants.PAGE_YEAR.toInt() - 20
            }
            val filteredDrawings = allDrawings.drawings.filter { it.year >= yearFilter }
            val numbersToUse = getNumbersToUse(filteredDrawings)
            val drawingsToGenerate = when (totoType) {
                TotoType.T_6X49 -> 4
                TotoType.T_6X42 -> 4
                TotoType.T_5X35 -> 8
            }
            val results = generatePredictions(
                numbersToUse,
                drawingsToGenerate
            )
            results.forEach { Logg.printIntArray(it.numbers) }
        }
    }

    private fun backtestPredictViaNumberDistributionPerPosition(
        totoType: TotoType,
        allDrawings: Drawings
    ) {
        BacktestNumberDistributionPerPosition(
            totoType,
            allDrawings,
            PredictViaNumberDistributionPerPosition(totoType, allDrawings)
        ).backtest(trainingDataYearFilter = 20, backtestSampleSizeYears = 1)
    }

    private fun backtestCooccurrenceMatrix(
        totoType: TotoType,
        allDrawings: Drawings
    ) {
        val yearFilter = when (totoType) {
            TotoType.T_6X49 -> Constants.PAGE_YEAR.toInt() - 20
            TotoType.T_6X42 -> Constants.PAGE_YEAR.toInt() - 20 // Not tested
            TotoType.T_5X35 -> Constants.PAGE_YEAR.toInt() - 20
        }
        BacktestCooccurrenceMatrix(
            totoType,
            allDrawings,
            yearFilter
        ).test()
    }
}