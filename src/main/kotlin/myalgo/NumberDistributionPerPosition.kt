package myalgo

import myalgo.model.Drawing
import model.TotoType

/**
 * Calculate how often a number has occurred at different positions.
 */
class NumberDistributionPerPosition(
    private val totoType: TotoType,
    private val drawings: List<Drawing>
) {

    val distributionArray: Array<MutableMap<Int, Double>> = Array(totoType.size) { mutableMapOf() }

    val medianArray: DoubleArray = DoubleArray(totoType.size)
    val meanArray: DoubleArray = DoubleArray(totoType.size)

    val distributionByMedianArray: Array<MutableSet<Int>> = Array(totoType.size) { mutableSetOf() }
    val distributionByMeanArray: Array<MutableSet<Int>> = Array(totoType.size) { mutableSetOf() }

    init {
        populateDistributionArray()
        calculateMedians()
        calculateMeans()
        calculateDistributionsByMedianValues()
        calculateDistributionsByMeanValues()
    }

    private fun populateDistributionArray() {
        val numDistributionArray: Array<MutableMap<Int, Int>> = Array(totoType.size) { mutableMapOf() }

        drawings.map { it.numbers }.forEach { drawing ->
            drawing.forEachIndexed { index, number ->
                numDistributionArray[index].merge(number, 1, Int::plus)
            }
        }

        numDistributionArray.forEachIndexed { index, numberDistributionMap ->
//            val totalOccurrenceCount = numberDistributionMap.values.sum().toDouble()

            numberDistributionMap.forEach { (number, occurrence) ->
                distributionArray[index][number] = occurrence.toDouble()
            }
        }
    }

    private fun calculateMedians() {
        distributionArray.forEachIndexed { index, map ->
            val sortedMap = map.values.sortedBy { it }
            val isSizeOdd = sortedMap.size % 2 != 0
            val middleIndex = sortedMap.size / 2
            val frequency = sortedMap[middleIndex]
            if (isSizeOdd) {
                medianArray[index] = frequency
            } else {
                medianArray[index] = frequency.plus(sortedMap[middleIndex - 1]).div(2)
            }
        }
    }

    private fun calculateMeans() {
        distributionArray.forEachIndexed { index, map ->
            val sum = map.values.sumOf { it }
            meanArray[index] = sum.div(map.size)
        }
    }

    private fun calculateDistributionsByMedianValues() {
        distributionArray.forEachIndexed { index, map ->
            map.forEach { (number, numberOccurrence) ->
                if (numberOccurrence > medianArray[index]) {
                    distributionByMedianArray[index].add(number)
                }
            }
        }
    }

    private fun calculateDistributionsByMeanValues() {
        distributionArray.forEachIndexed { index, map ->
            map.forEach { (number, numberOccurrence) ->
                if (numberOccurrence > meanArray[index]) {
                    distributionByMeanArray[index].add(number)
                }
            }
        }
    }
}