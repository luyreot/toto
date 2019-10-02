package model

/**
 * Super class for storing information for various types of patterns.
 */
abstract class Pattern : Comparable<Pattern> {

    // How many times a certain pattern has occurred
    var timesOccurred: Int = 1
    // Calculated by dividing the [timesOccurred] of this pattern by the total sum of all patterns
    var probability: Double = 0.0

    fun incrementTimesOccurred() = timesOccurred++

    fun calculateProbability(total: Int) {
        probability = timesOccurred.toDouble().div(total)
    }

    /**
     * Will sort in ascending order.
     */
    override fun compareTo(other: Pattern): Int = probability.compareTo(other.probability)

}