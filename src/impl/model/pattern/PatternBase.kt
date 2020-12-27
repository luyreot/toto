package impl.model.pattern

import impl.data.Drawing

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
     * Divide the occurrence by the total number of drawings from [Drawing.drawings].
     */
    open fun calcProbability(total: Int = Drawing.drawings.count()) {
        probability = occurrence.toDouble().div(total)
    }

    /**
     * Currently sorts in ascending order.
     */
    override fun compareTo(other: PatternBase): Int {
        return probability.compareTo(other.probability)
    }
}