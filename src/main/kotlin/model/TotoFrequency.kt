package model

/**
 * Hold information about a particular [frequency] value (the spacing between issues when a particular value has occurred)
 * and the number of times it has occurred represented by the [count].
 *
 * Can be used to track frequencies of:
 * - a toto number
 * - a toto pattern
 */
data class TotoFrequency(
    val frequency: Int,
    val count: Int = 1
)