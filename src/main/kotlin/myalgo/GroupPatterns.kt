package myalgo

import myalgo.model.Drawing
import myalgo.model.UniquePattern

/**
 * Track how often each group pattern has been drawn.
 */
class GroupPatterns(
    private val drawings: List<Drawing>
) {

    val patterns: Map<UniquePattern, Int>
        get() = _patterns
    private val _patterns = mutableMapOf<UniquePattern, Int>()

    init {
        setGroupPatternOccurrences()
    }

    private fun setGroupPatternOccurrences() {
        drawings
            .map { drawing -> UniquePattern(drawing.groupPattern) }
            .toSet()
            .forEach { pattern ->
                _patterns[pattern] = drawings.count { drawing ->
                    drawing.groupPattern.contentEquals(pattern.array)
                }
            }
    }
}