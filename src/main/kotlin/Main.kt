import crawler.WebCrawler
import data.*
import model.PatternType
import model.TotoType

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("=== MAIN START ===")

//        WebCrawler().apply {
//            crawl(TotoType.T_5X35)
//            crawl(TotoType.T_6X42)
//            crawl(TotoType.T_6X49)
//        }

        val drawings = Drawings(TotoType.T_6X49)

        val numbers = Numbers(drawings.totoType, drawings.drawings)
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

        val combinationSize = 3
        val numberCombinations = NumberCombinations(combinationSize, drawings.drawings)

        val numberSequenceTakeRatio = .37f
        val numberSequenceSize = 3
        val numberSequences = NumberSequences(numberSequenceSize, drawings.drawings)
        val predictNumberSequences = PredictNumberSequences(drawings.totoType, drawings.drawings, numberSequences, numberSequenceTakeRatio)

        val groupSequenceTakeRatio = .77f
        val groupSequenceSize = 2
        val groupPatternSequences = PatternSequences(drawings.drawings, PatternType.GROUP, groupSequenceSize)
        val predictGroupPatternSequences = PredictPatternSequences(
            drawings.totoType,
            drawings.drawings,
            PatternType.GROUP,
            groupPatternSequences.patternSequences,
            groupSequenceSize,
            groupSequenceTakeRatio
        )

        val lowHighSequenceTakeRatio = .77f
        val lowHighSequenceSize = 4
        val lowHighPatternSequences = PatternSequences(drawings.drawings, PatternType.LOW_HIGH, lowHighSequenceSize)
        val predictLowHighPatternSequences = PredictPatternSequences(
            drawings.totoType,
            drawings.drawings,
            PatternType.LOW_HIGH,
            lowHighPatternSequences.patternSequences,
            lowHighSequenceSize,
            lowHighSequenceTakeRatio
        )

        val oddEvenSequenceTakeRatio = .67f
        val oddEvenSequenceSize = 2
        val oddEvenPatternSequences = PatternSequences(drawings.drawings, PatternType.ODD_EVEN, oddEvenSequenceSize)
        val predictOddEvenPatternSequences = PredictPatternSequences(
            drawings.totoType,
            drawings.drawings,
            PatternType.ODD_EVEN,
            oddEvenPatternSequences.patternSequences,
            oddEvenSequenceSize,
            oddEvenSequenceTakeRatio
        )

        val predictDrawing = PredictDrawing(
            drawings,
            numbers,
            groupPatterns,
            lowHighPatterns,
            oddEvenPatterns,
            predictNumberSequences,
            numberCombinations,
            numberToPatternCorrelations,
            groupPatternToPatternCorrelations
        )

        if (true) {
            testing(
                drawings = drawings,
                backtestSampleSize = 100,
                numberSequenceSize = numberSequenceSize,
                numberSequenceTakeRatio = numberSequenceTakeRatio,
                groupSequenceSize = groupSequenceSize,
                groupSequenceTakeRatio = groupSequenceTakeRatio,
                lowHighSequenceSize = lowHighSequenceSize,
                lowHighSequenceTakeRatio = lowHighSequenceTakeRatio,
                oddEvenSequenceSize = oddEvenSequenceSize,
                oddEvenSequenceTakeRatio = oddEvenSequenceTakeRatio
            )
        }

        println("=== MAIN END ===")
    }

    private fun testing(
        drawings: Drawings,
        backtestSampleSize: Int,
        numberSequenceSize: Int,
        numberSequenceTakeRatio: Float,
        groupSequenceSize: Int,
        groupSequenceTakeRatio: Float,
        lowHighSequenceSize: Int,
        lowHighSequenceTakeRatio: Float,
        oddEvenSequenceSize: Int,
        oddEvenSequenceTakeRatio: Float
    ) {
        val results = mutableMapOf<Int, Int>()

        for (i in backtestSampleSize downTo 1) {
            val subDrawings = drawings.drawings.subList(0, drawings.drawings.size - i)

            val numberSequences = NumberSequences(numberSequenceSize, subDrawings)

            // Testing number predictions
            val predictNumberSequences = PredictNumberSequences(
                drawings.totoType,
                subDrawings,
                numberSequences,
                numberSequenceTakeRatio
            )
            predictNumberSequences.printPredictionResults(
                drawings.drawings[subDrawings.size],
                predictNumberSequences.predictionNumbers,
                drawings.totoType.totalNumbers
            ).let { result ->
                results[result] = (results[result] ?: 0).plus(1)
            }

            // Testing group pattern predictions
            /*
            val groupPatternSequencesTest = PatternSequences(
                drawings.drawings.size, drawings.drawings, PatternType.GROUP, groupSequenceSize, i
            )
            PredictPatternSequences(
                drawings.totoType,
                drawings.drawings,
                PatternType.GROUP,
                groupPatternSequencesTest.patternSequences,
                groupSequenceSize,
                groupSequenceTakeRatio,
                i
            )
            */

            // Testing low/high pattern predictions
            /*
            val lowHighPatternSequencesTest = PatternSequences(
                drawings.drawings.size, drawings.drawings, PatternType.LOW_HIGH, lowHighSequenceSize, i
            )
            PredictPatternSequences(
                drawings.totoType,
                drawings.drawings,
                PatternType.LOW_HIGH,
                lowHighPatternSequencesTest.patternSequences,
                lowHighSequenceSize,
                lowHighSequenceTakeRatio,
                i
            )
            */

            // Testing odd/even pattern predictions
            /*
            val oddEvenPatternSequencesTest = PatternSequences(
                drawings.drawings.size, drawings.drawings, PatternType.ODD_EVEN, oddEvenSequenceSize, i
            )
            PredictPatternSequences(
                drawings.totoType,
                drawings.drawings,
                PatternType.ODD_EVEN,
                oddEvenPatternSequencesTest.patternSequences,
                oddEvenSequenceSize,
                oddEvenSequenceTakeRatio,
                i
            )
            */
        }

        println("Total Results (${results.values.sum()}):")
        results.forEach { (result, count) ->
            println("$result - $count times")
        }
    }
}