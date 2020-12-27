package impl.model.pattern

import impl.data.Drawing

/**
 * Holds a number, a frequency, which represents the number
 * between two subsequent occurrences of a [PatternNumeric] from [Drawing.drawings].
 */
data class PatternFrequency(val frequency: Int) : PatternBase()