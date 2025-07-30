package systems.patterns

import systems.patterns.model.Drawing
import util.UniqueIntArray

/**
 * Track how often each odd/even pattern has been drawn.
 */
class OddEvenPatterns(
    private val drawings: List<Drawing>
) {

    val patterns: Map<UniqueIntArray, Int>
        get() = _patterns
    private val _patterns = mutableMapOf<UniqueIntArray, Int>()

    init {
        setOddEvenPatternOccurrences()
    }

    private fun setOddEvenPatternOccurrences() {
        drawings
            .map { drawing -> UniqueIntArray(drawing.oddEvenPattern) }
            .toSet()
            .forEach { pattern ->
                _patterns[pattern] = drawings.count { drawing ->
                    drawing.oddEvenPattern.contentEquals(pattern.array)
                }
            }
    }
}