package impl.model.pattern

import impl.data.Data

/**
 * Holds a number, a frequency, which represents the number
 * between two subsequent occurrences of a [PatternNumeric] from [Data.drawings].
 */
data class PatternFrequency(val frequency: Int) : PatternBase()