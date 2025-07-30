package systems.patterns

import systems.patterns.model.Drawing
import util.UniqueIntArray

/**
 * Track how often correlations exist between:
 * - an odd/even pattern and a group pattern
 * - an odd/even pattern and a low/high pattern
 */
class OddEvenPatternToPatternCorrelations(
    private val drawings: List<Drawing>
) {

    val oddEvenToGroupPatterns: Map<UniqueIntArray, Map<UniqueIntArray, Int>>
        get() = _oddEvenToGroupPatterns
    private val _oddEvenToGroupPatterns = mutableMapOf<UniqueIntArray, MutableMap<UniqueIntArray, Int>>()

    val oddEvenToLowHighPatterns: Map<UniqueIntArray, Map<UniqueIntArray, Int>>
        get() = _oddEvenToLowHighPatterns
    private val _oddEvenToLowHighPatterns = mutableMapOf<UniqueIntArray, MutableMap<UniqueIntArray, Int>>()

    init {
        setCorrelations()
    }

    private fun setCorrelations() {
        drawings.forEach { drawing ->
            setOddEvenToGroupPatternCorrelation(UniqueIntArray(drawing.oddEvenPattern), drawing.groupPattern)
            setOddEvenToLowHighPatternCorrelation(UniqueIntArray(drawing.oddEvenPattern), drawing.lowHighPattern)
        }
    }

    private fun setOddEvenToGroupPatternCorrelation(oddEvenPattern: UniqueIntArray, groupPattern: IntArray) {
        if (_oddEvenToGroupPatterns.containsKey(oddEvenPattern).not()) {
            _oddEvenToGroupPatterns[oddEvenPattern] = mutableMapOf()
        }
        _oddEvenToGroupPatterns[oddEvenPattern]?.merge(UniqueIntArray(groupPattern), 1, Int::plus)
    }

    private fun setOddEvenToLowHighPatternCorrelation(oddEvenPattern: UniqueIntArray, lowHighPattern: IntArray) {
        if (_oddEvenToLowHighPatterns.containsKey(oddEvenPattern).not()) {
            _oddEvenToLowHighPatterns[oddEvenPattern] = mutableMapOf()
        }
        _oddEvenToLowHighPatterns[oddEvenPattern]?.merge(UniqueIntArray(lowHighPattern), 1, Int::plus)
    }
}