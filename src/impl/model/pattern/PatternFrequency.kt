package impl.model.pattern

import impl.data.Drawings

/**
 * Holds a number, a frequency, which represents the number
 * between two subsequent occurrences of a [PatternNumeric] from [Drawings.drawings].
 */
data class PatternFrequency(val frequency: Int) : PatternBase()