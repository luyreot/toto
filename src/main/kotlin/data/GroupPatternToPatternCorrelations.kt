package data

import model.Drawing
import model.UniquePattern

/**
 * Track how often correlations exist between:
 * - a group pattern and a low/high pattern
 * - a group pattern and an odd/even pattern
 */
class GroupPatternToPatternCorrelations(
    private val drawings: List<Drawing>
) {

    val groupToLowHighPatterns: Map<UniquePattern, Map<UniquePattern, Int>>
        get() = _groupToLowHighPatterns
    private val _groupToLowHighPatterns = mutableMapOf<UniquePattern, MutableMap<UniquePattern, Int>>()

    val groupToOddEvenPatterns: Map<UniquePattern, Map<UniquePattern, Int>>
        get() = _groupToOddEvenPatterns
    private val _groupToOddEvenPatterns = mutableMapOf<UniquePattern, MutableMap<UniquePattern, Int>>()

    init {
        setCorrelations()
    }

    private fun setCorrelations() {
        drawings.forEach { drawing ->
            setGroupToLowHighPatternCorrelation(UniquePattern(drawing.groupPattern), drawing.lowHighPattern)
            setGroupToOddEvenPatternCorrelation(UniquePattern(drawing.groupPattern), drawing.oddEvenPattern)
        }
    }

    private fun setGroupToLowHighPatternCorrelation(groupPattern: UniquePattern, lowHighPattern: IntArray) {
        if (_groupToLowHighPatterns.containsKey(groupPattern).not()) {
            _groupToLowHighPatterns[groupPattern] = mutableMapOf()
        }
        _groupToLowHighPatterns[groupPattern]?.merge(UniquePattern(lowHighPattern), 1, Int::plus)
    }

    private fun setGroupToOddEvenPatternCorrelation(groupPattern: UniquePattern, oddEvenPattern: IntArray) {
        if (_groupToOddEvenPatterns.containsKey(groupPattern).not()) {
            _groupToOddEvenPatterns[groupPattern] = mutableMapOf()
        }
        _groupToOddEvenPatterns[groupPattern]?.merge(UniquePattern(oddEvenPattern), 1, Int::plus)
    }
}