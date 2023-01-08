package data

import extensions.sortByValueDescending
import model.CombinedPattern
import model.Numbers
import model.TotoType
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
    private val fromYear: Int? = null
) {

    val patterns: Set<CombinedPattern>
        get() = patternsCache
    private val patternsCache = mutableSetOf<CombinedPattern>()

    fun calculateStats() {
        val drawings = if (fromYear == null) drawings.drawings else drawings.drawingsSubset

        drawings.forEach { totoNumbers ->
            val groupPattern = Numbers(
                convertToGroupPattern(totoNumbers.numbers.copyOf(), totoType.groupStrategy)
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

        validateResults()
        sortResults()
    }

    private fun validateResults() {
        if (patternsCache.isEmpty())
            throw IllegalArgumentException("There are no patterns!")

        patternsCache.forEach { pattern ->
            if (pattern.lowHighs.isEmpty())
                throw IllegalArgumentException("There are no low/high patterns!")

            if (pattern.oddEvens.isEmpty())
                throw IllegalArgumentException("There are no odd/even patterns!")
        }
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