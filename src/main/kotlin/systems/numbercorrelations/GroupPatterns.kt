package systems.numbercorrelations

import systems.numbercorrelations.model.Drawing
import util.UniqueIntArray

/**
 * Track how often each group pattern has been drawn.
 */
class GroupPatterns(
    private val drawings: List<Drawing>
) {

    val patterns: Map<UniqueIntArray, Int>
        get() = _patterns
    private val _patterns = mutableMapOf<UniqueIntArray, Int>()

    init {
        setGroupPatternOccurrences()
    }

    private fun setGroupPatternOccurrences() {
        drawings
            .map { drawing -> UniqueIntArray(drawing.groupPattern) }
            .toSet()
            .forEach { pattern ->
                _patterns[pattern] = drawings.count { drawing ->
                    drawing.groupPattern.contentEquals(pattern.array)
                }
            }
    }
}