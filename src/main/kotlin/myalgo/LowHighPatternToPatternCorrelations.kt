package myalgo

import myalgo.model.Drawing
import myalgo.model.UniquePattern

/**
 * Track how often correlations exist between:
 * - a low/high pattern and a group pattern
 * - a low/high pattern and an odd/even pattern
 */
class LowHighPatternToPatternCorrelations(
    private val drawings: List<Drawing>
) {

    val lowHighToGroupPatterns: Map<UniquePattern, Map<UniquePattern, Int>>
        get() = _lowHighToGroupPatterns
    private val _lowHighToGroupPatterns = mutableMapOf<UniquePattern, MutableMap<UniquePattern, Int>>()

    val lowHighToOddEvenPatterns: Map<UniquePattern, Map<UniquePattern, Int>>
        get() = _lowHighToOddEvenPatterns
    private val _lowHighToOddEvenPatterns = mutableMapOf<UniquePattern, MutableMap<UniquePattern, Int>>()

    init {
        setCorrelations()
    }

    private fun setCorrelations() {
        drawings.forEach { drawing ->
            setLowHighToGroupPatternCorrelation(UniquePattern(drawing.lowHighPattern), drawing.groupPattern)
            setLowHighToOddEvenPatternCorrelation(UniquePattern(drawing.lowHighPattern), drawing.oddEvenPattern)
        }
    }

    private fun setLowHighToGroupPatternCorrelation(lowHighPattern: UniquePattern, groupPattern: IntArray) {
        if (_lowHighToGroupPatterns.containsKey(lowHighPattern).not()) {
            _lowHighToGroupPatterns[lowHighPattern] = mutableMapOf()
        }
        _lowHighToGroupPatterns[lowHighPattern]?.merge(UniquePattern(groupPattern), 1, Int::plus)
    }

    private fun setLowHighToOddEvenPatternCorrelation(lowHighPattern: UniquePattern, oddEvenPattern: IntArray) {
        if (_lowHighToOddEvenPatterns.containsKey(lowHighPattern).not()) {
            _lowHighToOddEvenPatterns[lowHighPattern] = mutableMapOf()
        }
        _lowHighToOddEvenPatterns[lowHighPattern]?.merge(UniquePattern(oddEvenPattern), 1, Int::plus)
    }
}