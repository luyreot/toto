package data

import model.Drawing
import model.UniquePattern

/**
 * Track how often each low/high pattern has been drawn.
 */
class LowHighPatterns(
    private val drawings: List<Drawing>
) {

    val patterns: Map<UniquePattern, Int>
        get() = _patterns
    private val _patterns = mutableMapOf<UniquePattern, Int>()

    init {
        setLowHighPatternOccurrences()
    }

    private fun setLowHighPatternOccurrences() {
        drawings
            .map { drawing -> UniquePattern(drawing.lowHighPattern) }
            .toSet()
            .forEach { pattern ->
                _patterns[pattern] = drawings.count { drawing ->
                    drawing.lowHighPattern.contentEquals(pattern.array)
                }
            }
    }
}