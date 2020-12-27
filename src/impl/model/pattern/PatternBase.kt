package impl.model.pattern

import impl.data.Data

/**
 * Base pattern for tracking how often a pattern occurs.
 */
abstract class PatternBase(
        var occurrence: Int = 1,
        var probability: Double = 0.0
) {
    fun occurred() = occurrence++

    /**
     * Calculates the probability of this pattern.
     * Divide the occurrence by the total number of drawings from [Data.drawings].
     */
    open fun calcProbability(total: Int = Data.drawings.count()) {
        probability = occurrence.toDouble().div(total)
    }
}