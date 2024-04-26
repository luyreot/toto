import crawler.WebCrawler
import data.*
import model.TotoType
import util.Logg
import util.webCrawl

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        Logg.p("=== MAIN START ===")

        // Setup Global Configs
        webCrawl = false

        if (webCrawl()) return

        // Configs for generating toto numbers
        val yearFilter = 2018
        val totoType = TotoType.T_5X35

        performAlgorithm(Drawings(totoType, yearFilter))

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


    private fun performAlgorithm(drawings: Drawings) {
        val numberTable = NumberTable(drawings.totoType, drawings.drawings)
        val numberIntervals = NumberIntervals(drawings.totoType, drawings.drawings)
        val numberHotCold = NumberHotCold(drawings.totoType, drawings.drawings, numberTable.numbers)
        val numberDistributionPerPosition = NumberDistributionPerPosition(drawings.totoType, drawings.drawings)

        //
        val groupPatterns = GroupPatterns(drawings.drawings)
        val groupPatternIntervals = GroupPatternIntervals(groupPatterns.patterns.keys, drawings.drawings)
        val lowHighPatterns = LowHighPatterns(drawings.drawings)
        val lowHighPatternIntervals = LowHighPatternIntervals(lowHighPatterns.patterns.keys, drawings.drawings)
        val oddEvenPatterns = OddEvenPatterns(drawings.drawings)
        val oddEvenPatternIntervals = OddEvenPatternIntervals(oddEvenPatterns.patterns.keys, drawings.drawings)

        val numberToPatternCorrelations = NumberToPatternCorrelations(drawings.drawings)
        val groupPatternToPatternCorrelations = GroupPatternToPatternCorrelations(drawings.drawings)
        val lowHighPatternToPatternCorrelations = LowHighPatternToPatternCorrelations(drawings.drawings)
        val oddEvenPatternToPatternCorrelations = OddEvenPatternToPatternCorrelations(drawings.drawings)
        //

        val sameDrawingCombinations = SameDrawingCombinations(drawings.drawings, size = 2)
        val subsequentDrawingCombinations = SubsequentDrawingCombinations(drawings.drawings, size = 3)


    }
}