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
        optimizeGroupPattern(
            patternStats = groupPatternStats,
            predictedPattern = predictGroupPattern.nextPattern,
            type = "group",
            convertFun = ::convertToGroup,
            setNextPattern = { nextPattern -> nextGroupPattern = nextPattern }
        )
        optimizeGroupPattern(
            patternStats = oddEvenPatternStats,
            predictedPattern = predictOddEvenPattern.nextPattern,
            type = "odd/even",
            convertFun = ::convertToOddEven,
            setNextPattern = { nextPattern -> nextOddEvenPattern = nextPattern }
        )
        optimizeGroupPattern(
            patternStats = lowHighPatternStats,
            predictedPattern = predictLowHighPattern.nextPattern,
            type = "low/high",
            convertFun = ::convertToLowHigh,
            setNextPattern = { nextPattern -> nextLowHighPattern = nextPattern }
        )
    }

    private fun optimizeGroupPattern(
        patternStats: PatternStats<Numbers>,
        predictedPattern: IntArray,
        type: String,
        convertFun: (IntArray) -> IntArray,
        setNextPattern: (IntArray) -> Unit
    ) {
        val averagePatternOccurrence = patternStats.patterns.values.sum() / patternStats.patterns.size
        val aboveAveragePatterns = patternStats.patterns.filter { it.value > averagePatternOccurrence }.toMutableMap()

        if (aboveAveragePatterns.isEmpty()) throw IllegalArgumentException("There are no group patterns!")

        // Add the predicted pattern at the top of the list if it is in the above average patterns map
        val predictedGroupPattern = Numbers(predictedPattern)
        val patternsToVerify = mutableListOf<Numbers>()
        if (aboveAveragePatterns.containsKey(predictedGroupPattern)) {
            aboveAveragePatterns.remove(predictedGroupPattern)
            patternsToVerify.add(predictedGroupPattern)
        }
        patternsToVerify.addAll(aboveAveragePatterns.keys)

        patternsToVerify.forEach { pattern ->
            val averageGroupPatternFrequencyCount: Int = patternStats.frequencies[pattern]
                ?.map { it }
                ?.sumOf { it.count }
                ?.div(
                    patternStats.frequencies[pattern]?.size
                        ?: throw IllegalArgumentException("Something is wrong with $type pattern - $pattern")
                )
                ?: throw IllegalArgumentException("Something is wrong with $type pattern - $pattern")

            // Get difference between the upcoming and last drawing when the pattern has ocurred
            val lastFrequency = getLastFrequencyForPattern(
                type = type,
                pattern = pattern,
                convertFun = convertFun
            )
            // Find the first frequency that matches that difference
            val cachedFrequencies: Frequency? = patternStats.frequencies[pattern]?.find { it.frequency == lastFrequency }

            if (cachedFrequencies == null) {
                return@forEach
            }

            if (cachedFrequencies.count <= averageGroupPatternFrequencyCount) {
                return@forEach
            }

            setNextPattern.invoke(pattern.numbers)
            return
        }

        throw IllegalArgumentException("Could not find a suitable $type pattern!")
    }

    private fun getLastFrequencyForPattern(
        type: String,
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

        throw IllegalArgumentException("Something went wrong! Could not find any previous drawing with the following $type pattern - $pattern")
    }

    private fun convertToGroup(pattern: IntArray): IntArray = convertToGroupPattern(pattern, groupStrategyMethod)

    private fun convertToOddEven(pattern: IntArray): IntArray = convertOddEvenPattern(pattern)

    private fun convertToLowHigh(pattern: IntArray): IntArray = convertToLowHighPattern(pattern, totoType.lowHighMidPoint)

}