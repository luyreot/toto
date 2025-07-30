package systems.patterns

import systems.patterns.model.Drawing
import util.UniqueIntArray

/**
 * Track how often each low/high pattern has been drawn.
 */
class LowHighPatterns(
    private val drawings: List<Drawing>
) {

    val patterns: Map<UniqueIntArray, Int>
        get() = _patterns
    private val _patterns = mutableMapOf<UniqueIntArray, Int>()

    init {
        setLowHighPatternOccurrences()
    }

    private fun setLowHighPatternOccurrences() {
        drawings
            .map { drawing -> UniqueIntArray(drawing.lowHighPattern) }
            .toSet()
            .forEach { pattern ->
                _patterns[pattern] = drawings.count { drawing ->
                    drawing.lowHighPattern.contentEquals(pattern.array)
                }
            }
    }
}