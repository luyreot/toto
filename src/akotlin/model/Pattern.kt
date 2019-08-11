package akotlin.model

/**
 * Super class for storing information for various types of patterns.
 */
abstract class Pattern {

    // How many times a certain pattern has occurred
    abstract var timesOccurred: Int
    // Calculated by dividing the [timesOccurred] of this pattern by the total sum of all patterns
    abstract var probability: Double

    fun incrementTimesOccurred() = timesOccurred++

    fun calculateProbability(total: Int) {
        probability = timesOccurred.div(total).toDouble()
    }

}