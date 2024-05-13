package data

import model.Drawing
import model.TotoType
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Calculate mean and median values.
 * Create "hot" and "cold" groups.
 */
class NumberHotCold(
    private val totoType: TotoType,
    private val drawings: List<Drawing>,
    private val numbers: Map<Int, Int>
) {

    val hotNumbers: Set<Int>
        get() = _hotNumbers
    private val _hotNumbers: MutableSet<Int> = mutableSetOf()

    val coldNumbers: Set<Int>
        get() = _coldNumbers
    private val _coldNumbers: MutableSet<Int> = mutableSetOf()

    val moreHotPerDrawing: Int
        get() = _moreHotPerDrawing
    private var _moreHotPerDrawing: Int = 0

    val moreColdPerDrawing: Int
        get() = _moreColdPerDrawing
    private var _moreColdPerDrawing: Int = 0

    val equalHotColdPerDrawing: Int
        get() = _equalHotColdPerDrawing
    private var _equalHotColdPerDrawing: Int = 0

    /**
     * Working with a standard 6/49, a mean frequency of 6 per number per drawing could indicate that,
     * on average, each number is drawn about once in every 6 draws.
     */
    private var mean: Double = 0.0

    /**
     * Working with a standard 6/49, a mean frequency of 101 would imply, roughly speaking,
     * that half of the numbers in the dataset have frequencies equal to or below 101,
     * while the other half have frequencies equal to or above 101.
     */
    private var median: Double = 0.0

    /**
     * Represent how each original frequency compares to the mean in terms of standard deviations.
     * Positive values indicate frequencies above the mean, and values around 0 indicate frequencies close to the mean.
     */
    val normalisedFrequencies: Map<Int, Double>
        get() = _normalisedFrequencies
    private val _normalisedFrequencies: MutableMap<Int, Double> = mutableMapOf()

    init {
        calculateMedian()
        calculateMean()
        calculateNormalisedFrequencies()
        createHotColdNumbersByMedian()
        calculateTotalCountOfHotColdNumbers()
    }

    fun isHot(number: Int): Boolean = hotNumbers.contains(number)

    fun isCold(number: Int): Boolean = coldNumbers.contains(number)

    /**
     * Arrange the frequencies in ascending order. The median is the middle value.
     * If there's an even number of frequencies, the median is the average of the two middle values.
     * The median represents the middle value in the frequency distribution. It's less sensitive to extreme values than the mean.
     */
    private fun calculateMedian() {
        val sortedFrequencies = numbers.values.sorted()
        val isSizeOdd = sortedFrequencies.size % 2 != 0
        val middleIndex = sortedFrequencies.size / 2
        if (isSizeOdd) {
            median = sortedFrequencies[middleIndex].toDouble()
        } else {
            median = sortedFrequencies[middleIndex].plus(sortedFrequencies[middleIndex - 1]).toDouble().div(2)
        }
    }

    /**
     * Add up all the frequencies of the numbers and divide by the total number of draws.
     * The mean represents the average frequency of a number over the specified period.
     */
    private fun calculateMean() {
        val frequenciesSum = numbers.values.sum().toDouble()
        mean = frequenciesSum.div(drawings.size)
    }

    /**
     * 1. Calculate Squared Differences
     * 2. Calculate Mean of Squared Differences
     * 3. Calculate Standard Deviation
     * 4. Calculate Normalised Frequencies
     */
    private fun calculateNormalisedFrequencies() {
        numbers.forEach { (number, frequency) ->
            _normalisedFrequencies[number] = frequency.toDouble()
        }

        // Calculate Squared Differences
        // For each frequency, subtract the mean and square the result
        _normalisedFrequencies.forEach { (number, frequency) ->
            val squaredFrequency = (frequency - mean).pow(2.0)
            _normalisedFrequencies[number] = squaredFrequency
        }

        val meanSquaredDifference: Double = _normalisedFrequencies.values.sum().div(drawings.size)
        val standardDeviation: Double = sqrt(meanSquaredDifference)

        // Original Frequency − Mean / Standard Deviation
        numbers.forEach { (number, originalFrequency) ->
            _normalisedFrequencies[number] = (originalFrequency - mean).div(standardDeviation)
        }
    }

    /**
     * Numbers with frequencies above both the mean and median can be considered "hot."
     * These are the numbers that occur more frequently than the average and are representative of the central tendency.
     *
     * Numbers with frequencies below both the mean and median can be considered "cold."
     * These are the numbers that occur less frequently than the average.
     */
    private fun createHotColdNumbersByMedian() {
        numbers.forEach { (number, frequency) ->
            if (frequency > median) {
                _hotNumbers.add(number)
            } else {
                _coldNumbers.add(number)
            }
        }
    }

    private fun calculateTotalCountOfHotColdNumbers() {
        drawings.map { it.numbers }.forEach { drawing ->
            val hotCount = drawing.count { isHot(it) }
            val coldCount = drawing.count { isCold(it) }
            when {
                hotCount > coldCount -> _moreHotPerDrawing++
                coldCount > hotCount -> _moreColdPerDrawing++
                else -> _equalHotColdPerDrawing++
            }
        }
    }
}