package data

import extensions.sortByValueDescending
import model.*
import util.PatternUtils.convertOddEvenPattern
import util.PatternUtils.convertToGroupPattern
import util.PatternUtils.convertToLowHighPattern

/**
 * Track stats for group, high/low and odd/even patterns.
 * Track high/low and odd/even pattern for each group pattern.
 */
class CombinedPatternStats(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val groupStrategy: GroupStrategy,
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

    fun calculateStats() {
        val drawings = if (fromYear == null) drawings.drawings else drawings.drawingsSubset

        drawings.forEach { totoNumbers ->
            val groupPattern = Numbers(
                convertToGroupPattern(totoNumbers.numbers.copyOf(), groupStrategyMethod)
            )

            val lowHighPattern = Numbers(
                convertToLowHighPattern(totoNumbers.numbers.copyOf(), totoType.lowHighMidPoint)
            )

            val oddEvenPattern = Numbers(
                convertOddEvenPattern(totoNumbers.numbers.copyOf())
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