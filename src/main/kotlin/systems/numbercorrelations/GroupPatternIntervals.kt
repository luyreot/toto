package systems.numbercorrelations

import systems.numbercorrelations.model.Drawing
import systems.numbercorrelations.model.UniquePattern

/**
 * Track the intervals at which each group pattern has occurred.
 */
class GroupPatternIntervals(
    private val patterns: Set<UniquePattern>,
    private val drawings: List<Drawing>
) {

    val intervals: Map<UniquePattern, Map<Int, Int>>
        get() = _intervals
    private val _intervals = mutableMapOf<UniquePattern, MutableMap<Int, Int>>()

    init {
        setIntervals()
    }

    private fun setIntervals() {
        patterns.forEach { pattern ->
            val indexes = drawings.mapIndexed { index, drawing ->
                if (drawing.groupPattern.contentEquals(pattern.array)) index else null
            }.filterNotNull()

            _intervals[pattern] = mutableMapOf()
            for (i in 1 until indexes.size) {
                val interval = indexes[i] - indexes[i - 1]
                _intervals[pattern]?.merge(interval, 1, Int::plus)
            }
        }
    }
}