package model.pattern

import data.Drawings

/**
 * Base pattern for tracking how often a pattern occurs.
 */
abstract class PatternBase(
        var occurrence: Int = 1,
        var probability: Double = 0.0
) : Comparable<PatternBase> {
    fun occurred() = occurrence++

    /**
     * Calculates the probability of this pattern.
     * Divide the occurrence by the total number of drawings from [Drawings.drawings].
     */
    open fun calcProbability(total: Int = Drawings.drawings.count()) {
        probability = occurrence.toDouble().div(total)
    }

    /**
     * Currently sorts in ascending order.
     */
    override fun compareTo(other: PatternBase): Int = probability.compareTo(other.probability)

}