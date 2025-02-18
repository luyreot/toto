package systems.numbercorrelations

import systems.numbercorrelations.model.Drawing
import systems.numbercorrelations.model.UniquePattern

/**
 * Track how often correlations exist between:
 * - an odd/even pattern and a group pattern
 * - an odd/even pattern and a low/high pattern
 */
class OddEvenPatternToPatternCorrelations(
    private val drawings: List<Drawing>
) {

    val oddEvenToGroupPatterns: Map<UniquePattern, Map<UniquePattern, Int>>
        get() = _oddEvenToGroupPatterns
    private val _oddEvenToGroupPatterns = mutableMapOf<UniquePattern, MutableMap<UniquePattern, Int>>()

    val oddEvenToLowHighPatterns: Map<UniquePattern, Map<UniquePattern, Int>>
        get() = _oddEvenToLowHighPatterns
    private val _oddEvenToLowHighPatterns = mutableMapOf<UniquePattern, MutableMap<UniquePattern, Int>>()

    init {
        setCorrelations()
    }

    private fun setCorrelations() {
        drawings.forEach { drawing ->
            setOddEvenToGroupPatternCorrelation(UniquePattern(drawing.oddEvenPattern), drawing.groupPattern)
            setOddEvenToLowHighPatternCorrelation(UniquePattern(drawing.oddEvenPattern), drawing.lowHighPattern)
        }
    }

    private fun setOddEvenToGroupPatternCorrelation(oddEvenPattern: UniquePattern, groupPattern: IntArray) {
        if (_oddEvenToGroupPatterns.containsKey(oddEvenPattern).not()) {
            _oddEvenToGroupPatterns[oddEvenPattern] = mutableMapOf()
        }
        _oddEvenToGroupPatterns[oddEvenPattern]?.merge(UniquePattern(groupPattern), 1, Int::plus)
    }

    private fun setOddEvenToLowHighPatternCorrelation(oddEvenPattern: UniquePattern, lowHighPattern: IntArray) {
        if (_oddEvenToLowHighPatterns.containsKey(oddEvenPattern).not()) {
            _oddEvenToLowHighPatterns[oddEvenPattern] = mutableMapOf()
        }
        _oddEvenToLowHighPatterns[oddEvenPattern]?.merge(UniquePattern(lowHighPattern), 1, Int::plus)
    }
}