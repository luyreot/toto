package myalgo

import myalgo.model.Drawing
import myalgo.model.UniquePattern

/**
 * Track how often correlations exist between:
 * - a number and a group pattern
 * - a number and a low/high pattern
 * - a number and an odd/even pattern
 */
class NumberToPatternCorrelations(
    private val drawings: List<Drawing>
) {

    val numberToGroupPatterns: Map<Int, Map<UniquePattern, Int>>
        get() = _numberToGroupPatterns
    private val _numberToGroupPatterns = mutableMapOf<Int, MutableMap<UniquePattern, Int>>()

    val numberToLowHighPatterns: Map<Int, Map<UniquePattern, Int>>
        get() = _numberToLowHighPatterns
    private val _numberToLowHighPatterns = mutableMapOf<Int, MutableMap<UniquePattern, Int>>()

    val numberToOddEvenPatterns: Map<Int, Map<UniquePattern, Int>>
        get() = _numberToOddEvenPatterns
    private val _numberToOddEvenPatterns = mutableMapOf<Int, MutableMap<UniquePattern, Int>>()

    init {
        setCorrelations()
    }

    private fun setCorrelations() {
        drawings.forEach { drawing ->
            drawing.numbers.forEach { number ->
                setNumberToGroupPatternCorrelation(number, drawing.groupPattern)
                setNumberToLowHighPatternCorrelation(number, drawing.lowHighPattern)
                setNumberToOddEvenPatternCorrelation(number, drawing.oddEvenPattern)
            }
        }
    }

    private fun setNumberToGroupPatternCorrelation(number: Int, groupPattern: IntArray) {
        if (_numberToGroupPatterns.containsKey(number).not()) {
            _numberToGroupPatterns[number] = mutableMapOf()
        }
        _numberToGroupPatterns[number]?.merge(UniquePattern(groupPattern), 1, Int::plus)
    }

    private fun setNumberToLowHighPatternCorrelation(number: Int, lowHighPattern: IntArray) {
        if (_numberToLowHighPatterns.containsKey(number).not()) {
            _numberToLowHighPatterns[number] = mutableMapOf()
        }
        _numberToLowHighPatterns[number]?.merge(UniquePattern(lowHighPattern), 1, Int::plus)
    }

    private fun setNumberToOddEvenPatternCorrelation(number: Int, oddEvenPattern: IntArray) {
        if (_numberToOddEvenPatterns.containsKey(number).not()) {
            _numberToOddEvenPatterns[number] = mutableMapOf()
        }
        _numberToOddEvenPatterns[number]?.merge(UniquePattern(oddEvenPattern), 1, Int::plus)
    }
}