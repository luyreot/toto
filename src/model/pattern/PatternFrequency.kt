package model.pattern

import data.Drawings

/**
 * Holds a number, a frequency, which represents the number
 * between two subsequent occurrences of a [PatternNumeric] from [Drawings.drawings].
 */
data class PatternFrequency(val frequency: Int) : PatternBase()