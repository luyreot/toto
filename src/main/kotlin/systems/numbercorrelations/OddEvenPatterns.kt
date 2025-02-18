package systems.numbercorrelations

import systems.numbercorrelations.model.Drawing
import systems.numbercorrelations.model.UniquePattern

/**
 * Track how often each odd/even pattern has been drawn.
 */
class OddEvenPatterns(
    private val drawings: List<Drawing>
) {

    val patterns: Map<UniquePattern, Int>
        get() = _patterns
    private val _patterns = mutableMapOf<UniquePattern, Int>()

    init {
        setOddEvenPatternOccurrences()
    }

    private fun setOddEvenPatternOccurrences() {
        drawings
            .map { drawing -> UniquePattern(drawing.oddEvenPattern) }
            .toSet()
            .forEach { pattern ->
                _patterns[pattern] = drawings.count { drawing ->
                    drawing.oddEvenPattern.contentEquals(pattern.array)
                }
            }
    }
}