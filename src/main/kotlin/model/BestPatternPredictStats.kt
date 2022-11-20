package model

/**
 * Class for tracking best pattern prediction stats.
 */
data class BestPatternPredictStats(
    val up: Float,
    val down: Float,
    val highestPart: Int,
    val highestFull: Int
)