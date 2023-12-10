import crawler.WebCrawler
import data.*
import model.TotoType

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("=== MAIN START ===")

        WebCrawler().apply {
            crawl(TotoType.T_5X35)
            crawl(TotoType.T_6X42)
            crawl(TotoType.T_6X49)
        }

        val drawings = Drawings(TotoType.T_6X49)

        val numberTable = NumberTable(drawings.totoType, drawings.drawings)
        val numberFrequencies = NumberFrequencies(drawings.totoType, drawings.drawings)

        val groupPatterns = GroupPatterns(drawings.drawings)
        val groupPatternFrequencies = GroupPatternFrequencies(groupPatterns.patterns.keys, drawings.drawings)

        val lowHighPatterns = LowHighPatterns(drawings.drawings)
        val lowHighPatternFrequencies = LowHighPatternFrequencies(lowHighPatterns.patterns.keys, drawings.drawings)

        val oddEvenPatterns = OddEvenPatterns(drawings.drawings)
        val oddEvenPPatternFrequencies = OddEvenPatternFrequencies(oddEvenPatterns.patterns.keys, drawings.drawings)

        val numberToPatternCorrelations = NumberToPatternCorrelations(drawings.drawings)
        val groupPatternToPatternCorrelations = GroupPatternToPatternCorrelations(drawings.drawings)
        val lowHighPatternToPatternCorrelations = LowHighPatternToPatternCorrelations(drawings.drawings)
        val oddEvenPatternToPatternCorrelations = OddEvenPatternToPatternCorrelations(drawings.drawings)
        val sameDrawingNumberCombinations = SameDrawingNumberCombinations(combinationSize = 3, drawings.drawings)
        val subsequentDrawingNumberCombinations = SubsequentDrawingNumberCombinations(sequenceSize = 3, drawings.drawings)

        val predictNumberSequences = PredictNumberSequences(drawings.drawings, subsequentDrawingNumberCombinations, takeSize = 2)

        /*
        val groupSequenceSize = 2
        val groupSubsequentDrawingsPatternSequences = SubsequentDrawingsPatternSequences(
            drawings.drawings, PatternType.GROUP, groupSequenceSize
        )
        val predictGroupPatternSequences = PredictPatternSequences(
            drawings.totoType,
            drawings.drawings,
            PatternType.GROUP,
            groupSubsequentDrawingsPatternSequences.patternSequences,
            groupSequenceSize,
            takeRatio = .77f
        )

        val lowHighSequenceSize = 4
        val lowHighSubsequentDrawingsPatternSequences = SubsequentDrawingsPatternSequences(
            drawings.drawings, PatternType.LOW_HIGH, lowHighSequenceSize
        )
        val predictLowHighPatternSequences = PredictPatternSequences(
            drawings.totoType,
            drawings.drawings,
            PatternType.LOW_HIGH,
            lowHighSubsequentDrawingsPatternSequences.patternSequences,
            lowHighSequenceSize,
            takeRatio = .77f
        )

        val oddEvenSequenceSize = 2
        val oddEvenSubsequentDrawingsPatternSequences = SubsequentDrawingsPatternSequences(
            drawings.drawings, PatternType.ODD_EVEN, oddEvenSequenceSize
        )
        val predictOddEvenPatternSequences = PredictPatternSequences(
            drawings.totoType,
            drawings.drawings,
            PatternType.ODD_EVEN,
            oddEvenSubsequentDrawingsPatternSequences.patternSequences,
            oddEvenSequenceSize,
            takeRatio = .67f
        )
        */

        val predictDrawingBasedOnNumberSequences = PredictDrawingBasedOnNumberSequences(
            drawings.totoType,
            drawings,
            predictNumberSequences
        )

        println("=== MAIN END ===")
    }
}