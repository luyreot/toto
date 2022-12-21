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

    lateinit var nextGroupPattern: IntArray
    lateinit var nextLowHighPattern: IntArray
    lateinit var nextOddEvenPattern: IntArray

    private val groupStrategyMethod = groupStrategies[groupStrategy] as? (Int) -> Int

    init {
        if (groupStrategyMethod == null)
            throw IllegalArgumentException("Group strategy method is null!")
    }

    fun optimizePredictedPatterns() {
        optimizeGroupPattern()
    }

    private fun optimizeGroupPattern() {
        val averageGroupPatternOccurrence = groupPatternStats.patterns.values.sum() / groupPatternStats.patterns.size
        val aboveAverageGroupPatterns = groupPatternStats.patterns.filter { it.value > averageGroupPatternOccurrence }.toMutableMap()

        if (aboveAverageGroupPatterns.isEmpty()) throw IllegalArgumentException("There are no group patterns!")

        // Add the predicted pattern at the top of the list if it is in the above average patterns map
        val predictedGroupPattern = Numbers(predictGroupPattern.nextPattern)
        val groupPatternsToVerify = mutableListOf<Numbers>()
        if (aboveAverageGroupPatterns.containsKey(predictedGroupPattern)) {
            aboveAverageGroupPatterns.remove(predictedGroupPattern)
            groupPatternsToVerify.add(predictedGroupPattern)
        }
        groupPatternsToVerify.addAll(aboveAverageGroupPatterns.keys)

        groupPatternsToVerify.forEach { groupPattern ->
            val averageGroupPatternFrequencyCount: Int = groupPatternStats.frequencies[groupPattern]
                ?.map { it }
                ?.sumOf { it.count }
                ?.div(
                    groupPatternStats.frequencies[groupPattern]?.size
                        ?: throw IllegalArgumentException("Something is wrong with group pattern - $groupPattern")
                )
                ?: throw IllegalArgumentException("Something is wrong with group pattern - $groupPattern")

            // Get difference between the upcoming and last drawing when the pattern has occurred
            val lastFrequency = getLastFrequencyForPattern(
                pattern = groupPattern,
                convertFun = ::convertToGroup
            )
            // Find the first frequency that matches that difference
            val cachedFrequency: Frequency? = groupPatternStats.frequencies[groupPattern]?.find { it.frequency == lastFrequency }

            if (cachedFrequency == null) {
                return@forEach
            }

            if (cachedFrequency.count <= averageGroupPatternFrequencyCount) {
                return@forEach
            }

            val lowHighPattern = getCombinedCrossPattern(
                crossedPatterns = combinedPatternStats.patterns.find { it.groupPattern == groupPattern }?.lowHighs,
                predictedPattern = Numbers(predictLowHighPattern.nextPattern)
            )

            if (lowHighPattern == null) {
                return@forEach
            }

            val oddEvenPattern = getCombinedCrossPattern(
                crossedPatterns = combinedPatternStats.patterns.find { it.groupPattern == groupPattern }?.oddEvens,
                predictedPattern = Numbers(predictOddEvenPattern.nextPattern)
            )

            if (oddEvenPattern == null) {
                return@forEach
            }

            nextGroupPattern = groupPattern.numbers
            nextLowHighPattern = lowHighPattern
            nextOddEvenPattern = oddEvenPattern

            return
        }

        throw IllegalArgumentException("Could not find a suitable group pattern!")
    }

    private fun getCombinedCrossPattern(
        crossedPatterns: MutableMap<Numbers, Int>?,
        predictedPattern: Numbers
    ): IntArray? {
        // TODO Get l/h and o/e pattern based on the frequency
        //val averageScore = crossedPatterns?.values?.sum()?.div(crossedPatterns.size) ?: return null

        return crossedPatterns?.maxByOrNull { it.value }?.key?.numbers
    }

    private fun getLastFrequencyForPattern(
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
    ): Map<Numbers, Int>? = combinedPatternStats.patterns.find { it.groupPattern == groupPattern }?.lowHighs

    private fun getCombinedOddEvenPatterns(
        groupPattern: Numbers
    ): Map<Numbers, Int>? = combinedPatternStats.patterns.find { it.groupPattern == groupPattern }?.oddEvens
}