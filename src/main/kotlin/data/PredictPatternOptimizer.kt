package data

import model.*
import util.PatternUtils.convertOddEvenPattern
import util.PatternUtils.convertToGroupPattern
import util.PatternUtils.convertToLowHighPattern

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
    private val groupStrategy: GroupStrategy,
    private val combinedPatternStats: CombinedPatternStats,
    private val drawingScoreStats: DrawingScoreStats
) {

    val upcommingPatterns = mutableSetOf<CombinedPattern>()

    private val groupStrategyMethod = groupStrategies[groupStrategy] as? (Int) -> Int

    init {
        if (groupStrategyMethod == null)
            throw IllegalArgumentException("Group strategy method is null!")
    }

    fun optimizePredictedPatterns() {
        val aboveAverageGroupPatterns = (groupPatternStats.patterns.values.sum() / groupPatternStats.patterns.size)
            .let { averageGroupPatternOccurrence ->
                groupPatternStats.patterns.filter { pattern -> pattern.value > averageGroupPatternOccurrence }.toMutableMap()
            }

        if (aboveAverageGroupPatterns.isEmpty())
            throw IllegalArgumentException("There are no group patterns!")

        // Add the predicted pattern at the top of the list if it is in the above average patterns map
        val predictedGroupPattern = Numbers(predictGroupPattern.nextPattern)
        val groupPatternsToVerify = mutableListOf<Numbers>()
        if (aboveAverageGroupPatterns.containsKey(predictedGroupPattern)) {
            aboveAverageGroupPatterns.remove(predictedGroupPattern)
            groupPatternsToVerify.add(predictedGroupPattern)
        }
        groupPatternsToVerify.addAll(aboveAverageGroupPatterns.keys)

        groupPatternsToVerify.forEach { groupPattern ->
            if (canPatternOccurNext(groupPatternStats, groupPattern, ::convertToGroup, "group").not()) {
                return@forEach
            }

            val lowHighPatterns = getCombinedLowHighPatterns(groupPattern)
            if (lowHighPatterns?.isEmpty() == true) {
                return@forEach
            }
            val lowHighPatternsToAdd = mutableListOf<Numbers>()
            lowHighPatterns?.forEach { lowHighPattern ->
                if (canPatternOccurNext(lowHighPatternStats, lowHighPattern.key, ::convertToLowHigh, "low/high")) {
                    lowHighPatternsToAdd.add(lowHighPattern.key)
                }
            }
            if (lowHighPatternsToAdd.isEmpty()) {
                return@forEach
            }

            val oddEvenPatterns = getCombinedOddEvenPatterns(groupPattern)
            if (oddEvenPatterns?.isEmpty() == true) {
                return@forEach
            }
            val oddEvenPatternsToAdd = mutableListOf<Numbers>()
            oddEvenPatterns?.forEach { oddEvenPattern ->
                if (canPatternOccurNext(oddEvenPatternStats, oddEvenPattern.key, ::convertToOddEven, "odd/even")) {
                    oddEvenPatternsToAdd.add(oddEvenPattern.key)
                }
            }
            if (oddEvenPatternsToAdd.isEmpty()) {
                return@forEach
            }

            val upcomingPattern = CombinedPattern(Numbers(groupPattern.numbers), 0)
            lowHighPatternsToAdd.forEach {
                upcomingPattern.lowHighs[it] = 0
            }
            oddEvenPatternsToAdd.forEach {
                upcomingPattern.oddEvens[it] = 0
            }
            upcommingPatterns.add(upcomingPattern)
        }

        if (upcommingPatterns.isEmpty()) {
            throw IllegalArgumentException("Could not find a suitable group pattern!")
        }
    }

    private fun canPatternOccurNext(
        patternStats: PatternStats<Numbers>,
        pattern: Numbers,
        convertFun: (IntArray) -> IntArray,
        patternName: String
    ): Boolean {
        // Get difference between the upcoming and last drawing when the pattern has occurred
        val upcomingFrequency: Int = getUpcomingPatternFrequency(
            pattern = pattern,
            convertFun = convertFun
        )

        // Get upcoming frequency count
        val upcomingFrequencyCount: Int = patternStats.frequencies[pattern]
            ?.find { frequency -> frequency.frequency == upcomingFrequency }?.count
            ?: 1

        // Skip pattern if its upcoming frequency count is below average
        val averageFrequencyCount: Int = patternStats.frequencies[pattern]
            ?.sumOf { frequency -> frequency.count }
            ?.div(
                patternStats.frequencies[pattern]?.size
                    ?: throw IllegalArgumentException("Cannot determine frequency size for $patternName pattern - $pattern")
            )
            ?: throw IllegalArgumentException("Cannot determine average frequency count for $patternName pattern - $pattern")

        return upcomingFrequencyCount > averageFrequencyCount
    }

    private fun getUpcomingPatternFrequency(
        pattern: Numbers,
        convertFun: (IntArray) -> IntArray
    ): Int {
        val drawings = (if (fromYear == null) drawings.drawings else drawings.drawingsSubset).reversed()

        drawings.forEachIndexed { index, drawing ->
            val drawingPattern = Numbers(convertFun.invoke(drawing.numbers.copyOf()))
            if (pattern == drawingPattern) {
                return index + 1
            }
        }

        throw IllegalArgumentException("Something went wrong! Could not find any previous drawing with the following pattern - $pattern")
    }

    private fun convertToGroup(pattern: IntArray): IntArray = convertToGroupPattern(pattern, groupStrategyMethod)

    private fun convertToOddEven(pattern: IntArray): IntArray = convertOddEvenPattern(pattern)

    private fun convertToLowHigh(pattern: IntArray): IntArray = convertToLowHighPattern(pattern, totoType.lowHighMidPoint)

    private fun getCombinedLowHighPatterns(
        groupPattern: Numbers
    ): MutableMap<Numbers, Int>? = combinedPatternStats.patterns.find { it.groupPattern == groupPattern }?.lowHighs

    private fun getCombinedOddEvenPatterns(
        groupPattern: Numbers
    ): MutableMap<Numbers, Int>? = combinedPatternStats.patterns.find { it.groupPattern == groupPattern }?.oddEvens
}