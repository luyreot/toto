package systems.numbercorrelations

import systems.numbercorrelations.model.Drawing
import model.TotoType

/**
 * Track the intervals at which each number has occurred.
 */
class NumberIntervals(
    private val totoType: TotoType,
    private val drawings: List<Drawing>
) {

    val medians: Map<Int, Double>
        get() = _medians
    private var _medians: MutableMap<Int, Double> = mutableMapOf()

    val means: Map<Int, Double>
        get() = _means
    private var _means: MutableMap<Int, Double> = mutableMapOf()

    val intervalsByMedian: Map<Int, Set<Int>>
        get() = _intervalsByMedian
    private val _intervalsByMedian = mutableMapOf<Int, MutableSet<Int>>()

    val intervalsByMean: Map<Int, Set<Int>>
        get() = _intervalsByMean
    private val _intervalsByMean = mutableMapOf<Int, MutableSet<Int>>()

    private val intervals = mutableMapOf<Int, MutableMap<Int, Int>>()

    init {
        calculateIntervals()
        calculateMedianValues()
        calculateMeanValues()
        calculateIntervalsByMedian()
        calculateIntervalsByMean()
    }

    private fun calculateIntervals() {
        val allDrawings: List<IntArray> = drawings
            .map { drawing -> drawing.numbers }

        for (number in 1..totoType.totalNumbers) {
            val indexes = allDrawings.mapIndexed { index, drawing ->
                if (drawing.contains(number)) index else null
            }.filterNotNull()

            intervals[number] = mutableMapOf()
            for (i in 1 until indexes.size) {
                val interval: Int = indexes[i] - indexes[i - 1]
                intervals[number]?.merge(interval, 1, Int::plus)
            }
        }
    }

    private fun calculateMedianValues() {
        intervals.forEach { (number, intervals) ->
            val sortedIntervals = intervals.values.sorted()
            val isSizeOdd = sortedIntervals.size % 2 != 0
            val middleIndex = sortedIntervals.size / 2
            if (isSizeOdd) {
                _medians[number] = sortedIntervals[middleIndex].toDouble()
            } else {
                _medians[number] = sortedIntervals[middleIndex].plus(sortedIntervals[middleIndex - 1]).toDouble().div(2)
            }
        }
    }

    private fun calculateMeanValues() {
        intervals.forEach { (number, intervals) ->
            val sum = intervals.values.sum().toDouble()
            _means[number] = sum.div(intervals.size)
        }
    }

    private fun calculateIntervalsByMedian() {
        intervals.forEach { (number, intervals) ->
            val median = medians[number] ?: return@forEach
            val aboveMedian = intervals.filter { it.value > median }
            _intervalsByMedian[number] = aboveMedian.keys.toMutableSet()
        }
    }

    private fun calculateIntervalsByMean() {
        intervals.forEach { (number, intervals) ->
            val mean = means[number] ?: return@forEach
            val aboveMean = intervals.filter { it.value > mean }
            _intervalsByMean[number] = aboveMean.keys.toMutableSet()
        }
    }
}