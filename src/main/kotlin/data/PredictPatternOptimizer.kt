package data

import model.Numbers
import model.TotoType

class PredictPatternOptimizer(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val fromYear: Int? = null,
    private val numberStats: NumberStats,
    private val oddEvenPatternStats: OddEvenPatternStats,
    private val predictOddEvenPattern: PredictOddEvenPattern,
    private val lowHighPatternStats: LowHighPatternStats,
    private val predictLowHighPattern: PredictLowHighPattern,
    private val groupPatternStats: GroupPatternStats,
    private val predictGroupPattern: PredictGroupPattern,
    private val groupPatternDeltaStats: GroupPatternDeltaStats,
    private val combinedPatternStats: CombinedPatternStats,
    private val drawingScoreStats: DrawingScoreStats
) {

    lateinit var nextGroupPattern: IntArray
    lateinit var nextLowHighPattern: IntArray
    lateinit var nextOddEvenPattern: IntArray

    init {
        // TODO: Remove
        nextGroupPattern = predictGroupPattern.nextPattern
        nextLowHighPattern = predictLowHighPattern.nextPattern
        nextOddEvenPattern = predictOddEvenPattern.nextPattern
    }

    fun optimizePredictedPatterns() {
        // TODO: Remove
        return
        optimizeGroupPattern()
    }

    private fun optimizeGroupPattern() {
        val averagePatternOccurrence = groupPatternStats.patterns.values.sum() / groupPatternStats.patterns.size
        val aboveAveragePatterns = groupPatternStats.patterns.filter { it.value > averagePatternOccurrence }.toMutableMap()

        if (aboveAveragePatterns.isEmpty()) throw IllegalArgumentException("There are no group patterns!")

        // Add the predicted pattern at the top of the list if it is in the above average patterns map
        val pattern = Numbers(predictGroupPattern.nextPattern)
        val patternsToVerify = mutableListOf<Numbers>()
        if (aboveAveragePatterns.containsKey(pattern)) {
            aboveAveragePatterns.remove(pattern)
            patternsToVerify.add(pattern)
        }
        patternsToVerify.addAll(aboveAveragePatterns.keys)



        println()
    }

    private fun test(pattern: Int): Boolean {


        return false
    }
}