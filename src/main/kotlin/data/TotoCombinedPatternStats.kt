package data

import extensions.sortByValueDescending
import model.*
import util.PatternUtils.convertTotoNumbersToGroupPattern
import util.PatternUtils.convertTotoNumbersToLowHighPattern
import util.PatternUtils.convertTotoNumbersToOddEvenPattern

/**
 * Track stats for group, high/low and odd/even patterns.
 * Track high/low and odd/even pattern for each group pattern.
 */
class TotoCombinedPatternStats(
    private val totoType: TotoType,
    private val totoNumbers: TotoDrawnNumbers,
    private val groupStrategy: TotoGroupStrategy,
    private val fromYear: Int? = null
) {

    val patterns: Set<CombinedPattern>
        get() = patternsCache

    private val patternsCache = mutableSetOf<CombinedPattern>()

    private val groupStrategyMethod = groupStrategies[groupStrategy] as? (Int) -> Int

    init {
        if (groupStrategyMethod == null)
            throw IllegalArgumentException("Group strategy method is null!")
    }

    fun calculateCombinedPatternStats() {
        val drawings = if (fromYear == null) totoNumbers.allDrawings else totoNumbers.drawingsSubset

        drawings.forEach { totoNumbers ->
            val groupPattern = TotoNumbers(
                convertTotoNumbersToGroupPattern(totoNumbers.numbers.copyOf(), groupStrategyMethod)
            )

            val lowHighPattern = TotoNumbers(
                convertTotoNumbersToLowHighPattern(totoNumbers.numbers.copyOf(), totoType.lowHighMidPoint)
            )

            val oddEvenPattern = TotoNumbers(
                convertTotoNumbersToOddEvenPattern(totoNumbers.numbers.copyOf())
            )

            val combinedPattern = CombinedPattern(groupPattern, 1).apply {
                lowHighs[lowHighPattern] = 1
                oddEvens[oddEvenPattern] = 1
            }

            // Add combined pattern
            if (patternsCache.contains(combinedPattern).not()) {
                patternsCache.add(combinedPattern)
                return@forEach
            }

            // Merge existing combined pattern
            patternsCache.find { it == combinedPattern }?.let { existingPattern ->
                existingPattern.apply {
                    // Increment group pattern occurrence count
                    count += 1
                    // Merge low high pattern
                    lowHighs.merge(lowHighPattern, 1, Int::plus)
                    // Merge odd even pattern
                    oddEvens.merge(oddEvenPattern, 1, Int::plus)
                }
            }
        }

        sortResults()
    }

    private fun sortResults() {
        // Sort group patterns
        val sortedDescending = patternsCache.sortedBy { it.count }.reversed().toMutableSet()
        patternsCache.clear()
        patternsCache.addAll(sortedDescending)

        // Sort low/high and odd/even patterns
        patternsCache.forEach { pattern ->
            pattern.apply {
                lowHighs.sortByValueDescending()
                oddEvens.sortByValueDescending()
            }
        }
    }
}