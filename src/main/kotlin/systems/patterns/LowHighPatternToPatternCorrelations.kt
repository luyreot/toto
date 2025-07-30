package systems.patterns

import systems.patterns.model.Drawing
import util.UniqueIntArray

/**
 * Track how often correlations exist between:
 * - a low/high pattern and a group pattern
 * - a low/high pattern and an odd/even pattern
 */
class LowHighPatternToPatternCorrelations(
    private val drawings: List<Drawing>
) {

    val lowHighToGroupPatterns: Map<UniqueIntArray, Map<UniqueIntArray, Int>>
        get() = _lowHighToGroupPatterns
    private val _lowHighToGroupPatterns = mutableMapOf<UniqueIntArray, MutableMap<UniqueIntArray, Int>>()

    val lowHighToOddEvenPatterns: Map<UniqueIntArray, Map<UniqueIntArray, Int>>
        get() = _lowHighToOddEvenPatterns
    private val _lowHighToOddEvenPatterns = mutableMapOf<UniqueIntArray, MutableMap<UniqueIntArray, Int>>()

    init {
        setCorrelations()
    }

    private fun setCorrelations() {
        drawings.forEach { drawing ->
            setLowHighToGroupPatternCorrelation(UniqueIntArray(drawing.lowHighPattern), drawing.groupPattern)
            setLowHighToOddEvenPatternCorrelation(UniqueIntArray(drawing.lowHighPattern), drawing.oddEvenPattern)
        }
    }

    private fun setLowHighToGroupPatternCorrelation(lowHighPattern: UniqueIntArray, groupPattern: IntArray) {
        if (_lowHighToGroupPatterns.containsKey(lowHighPattern).not()) {
            _lowHighToGroupPatterns[lowHighPattern] = mutableMapOf()
        }
        _lowHighToGroupPatterns[lowHighPattern]?.merge(UniqueIntArray(groupPattern), 1, Int::plus)
    }

    private fun setLowHighToOddEvenPatternCorrelation(lowHighPattern: UniqueIntArray, oddEvenPattern: IntArray) {
        if (_lowHighToOddEvenPatterns.containsKey(lowHighPattern).not()) {
            _lowHighToOddEvenPatterns[lowHighPattern] = mutableMapOf()
        }
        _lowHighToOddEvenPatterns[lowHighPattern]?.merge(UniqueIntArray(oddEvenPattern), 1, Int::plus)
    }
}