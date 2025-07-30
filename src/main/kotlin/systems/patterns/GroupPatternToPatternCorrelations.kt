package systems.patterns

import systems.patterns.model.Drawing
import util.UniqueIntArray

/**
 * Track how often correlations exist between:
 * - a group pattern and a low/high pattern
 * - a group pattern and an odd/even pattern
 */
class GroupPatternToPatternCorrelations(
    private val drawings: List<Drawing>
) {

    val groupToLowHighPatterns: Map<UniqueIntArray, Map<UniqueIntArray, Int>>
        get() = _groupToLowHighPatterns
    private val _groupToLowHighPatterns = mutableMapOf<UniqueIntArray, MutableMap<UniqueIntArray, Int>>()

    val groupToOddEvenPatterns: Map<UniqueIntArray, Map<UniqueIntArray, Int>>
        get() = _groupToOddEvenPatterns
    private val _groupToOddEvenPatterns = mutableMapOf<UniqueIntArray, MutableMap<UniqueIntArray, Int>>()

    init {
        setCorrelations()
    }

    private fun setCorrelations() {
        drawings.forEach { drawing ->
            setGroupToLowHighPatternCorrelation(UniqueIntArray(drawing.groupPattern), drawing.lowHighPattern)
            setGroupToOddEvenPatternCorrelation(UniqueIntArray(drawing.groupPattern), drawing.oddEvenPattern)
        }
    }

    private fun setGroupToLowHighPatternCorrelation(groupPattern: UniqueIntArray, lowHighPattern: IntArray) {
        if (_groupToLowHighPatterns.containsKey(groupPattern).not()) {
            _groupToLowHighPatterns[groupPattern] = mutableMapOf()
        }
        _groupToLowHighPatterns[groupPattern]?.merge(UniqueIntArray(lowHighPattern), 1, Int::plus)
    }

    private fun setGroupToOddEvenPatternCorrelation(groupPattern: UniqueIntArray, oddEvenPattern: IntArray) {
        if (_groupToOddEvenPatterns.containsKey(groupPattern).not()) {
            _groupToOddEvenPatterns[groupPattern] = mutableMapOf()
        }
        _groupToOddEvenPatterns[groupPattern]?.merge(UniqueIntArray(oddEvenPattern), 1, Int::plus)
    }
}