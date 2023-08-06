package data

import model.Drawing
import model.UniquePattern

/**
 * Track the intervals at which each low/high pattern has occurred.
 */
class LowHighPatternFrequencies(
    private val patterns: Set<UniquePattern>,
    private val drawings: List<Drawing>
) {

    val frequencies: Map<UniquePattern, Map<Int, Int>>
        get() = _frequencies
    private val _frequencies = mutableMapOf<UniquePattern, MutableMap<Int, Int>>()

    init {
        setLowHighPatternFrequencies()
    }

    private fun setLowHighPatternFrequencies() {
        patterns.forEach { pattern ->
            val indexes = drawings.mapIndexed { index, drawing ->
                if (drawing.lowHighPattern.contentEquals(pattern.array)) index else null
            }.filterNotNull()

            _frequencies[pattern] = mutableMapOf()
            for (i in 1 until indexes.size) {
                val frequency = indexes[i] - indexes[i - 1]
                _frequencies[pattern]?.merge(frequency, 1, Int::plus)
            }
        }
    }
}