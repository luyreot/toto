package systems.deeplearning.util

import model.TotoType
import systems.deeplearning.model.Draw
import util.Constants.PAGE_YEAR
import util.IO
import kotlin.math.pow
import kotlin.math.sqrt

class NumberMeanOccurrences(
    totoType: TotoType,
    yearFilter: Int,
    draws: List<Draw>
) {

    val data: Map<Int, Double>
        get() = _data
    private val _data = mutableMapOf<Int, Double>()

    init {
        val filteredDraws = draws.filter { it.year >= yearFilter && it.year != PAGE_YEAR.toInt() }
        if (filteredDraws.isEmpty()) {
            throw IllegalArgumentException("There are no draws for $yearFilter.")
        }

        val years = filteredDraws.map { it.year }.distinct()
        for (number in 1..totoType.totalNumbers) {
            val numberYearlyMeans = mutableListOf<Double>()
            years.forEach { year ->
                val yearDraws = filteredDraws.filter { it.year == year }
                val numCount = yearDraws.count { it.numbers.contains(number) }
                numberYearlyMeans.add(numCount.toDouble() / yearDraws.size)
            }
            _data[number] = numberYearlyMeans.sum() / numberYearlyMeans.size
        }
    }
}

class NumberMeanGapsSinceLast(
    totoType: TotoType,
    yearFilter: Int,
    draws: List<Draw>
) {

    val dataMean: Map<Int, Double>
        get() = _dataMean
    private val _dataMean = mutableMapOf<Int, Double>()

    val dataMedian: Map<Int, Double>
        get() = _dataMedian
    private val _dataMedian = mutableMapOf<Int, Double>()

    // Gaps that indicate the number is 'hot'
    val hotGapLessThan: Map<Int, Int>
        get() = _hotGapLessThan
    private val _hotGapLessThan = mutableMapOf<Int, Int>()
    val hotGapMoreThan: Map<Int, Int>
        get() = _hotGapMoreThan
    private val _hotGapMoreThan = mutableMapOf<Int, Int>()

    init {
        val filteredDraws = draws.filter { it.year >= yearFilter && it.year != PAGE_YEAR.toInt() }.reversed()
        if (filteredDraws.isEmpty()) {
            throw IllegalArgumentException("There are no draws for $yearFilter.")
        }

        for (number in 1..totoType.totalNumbers) {
            val numberIndexes = filteredDraws.mapIndexedNotNull { index, draw ->
                if (draw.numbers.contains(number)) index else null
            }
            val gaps = mutableListOf<Int>()
            for (i in 1 until numberIndexes.size) {
                gaps.add(numberIndexes[i] - numberIndexes[i - 1])
            }
            // means
            _dataMean[number] = gaps.sum().toDouble() / gaps.size
            // medians
            gaps.sort()
            val isSizeOdd = gaps.size % 2 != 0
            val middleIndex = gaps.size / 2
            if (isSizeOdd) {
                _dataMedian[number] = gaps[middleIndex].toDouble()
            } else {
                _dataMedian[number] = gaps[middleIndex].plus(gaps[middleIndex - 1]).toDouble().div(2)
            }
            // top gaps
            val gapsCounted = gaps.groupingBy { it }.eachCount().toList().sortedBy { it.second }
            _hotGapMoreThan[number] = gapsCounted.toMap().entries.take(8).map { it.key }.max()
            _hotGapLessThan[number] = gapsCounted.reversed().toMap().entries.take(5).map { it.key }.max()
        }
    }
}

object Data {

    fun getDrawFeatures(
        number: Int,
        draws: List<Draw>,
        yearFilter: Int,
        drawIndex: Int,
        occurrences: NumberMeanOccurrences,
        gaps: NumberMeanGapsSinceLast
    ): DoubleArray {
        val gapSinceLast = calculateGapSinceLast(
            number = number,
            draws = draws,
            drawIndex = drawIndex
        )
        val inDraw = appearedInDraw(
            number = number,
            draw = draws[drawIndex]
        )
        val hotOrCold = when {
            gapSinceLast <= gaps.hotGapLessThan[number]!! -> 1.0
            gapSinceLast >= gaps.hotGapMoreThan[number]!! -> 1.0
            else -> 0.0
        }
        val currentYear = draws[drawIndex].year
        val currentYearDrawId = draws[drawIndex].id
        val sameYearDraws = draws.filter { it.year == currentYear && it.id <= currentYearDrawId }
        val numCount = sameYearDraws.count { it.numbers.contains(number) }
        val numMeanOcc = numCount.toDouble() / sameYearDraws.size

        return doubleArrayOf(
            gapSinceLast.toDouble(),
            inDraw.toDouble(),
            hotOrCold,
            gapSinceLast / gaps.dataMean[number]!!,
            numMeanOcc - occurrences.data[number]!!
        )
    }

    fun loadDrawings(totoType: TotoType): List<Draw> {
        val drawings = mutableListOf<Draw>()

        IO.getFiles(totoType.filePath)?.sorted()?.forEach { file ->
            IO.getTxtFileContents(file).forEachIndexed { index, line ->
                val numbers: IntArray = line.split(",").map { it.toInt() }.toIntArray()

                require(numbers.size == totoType.size) { "Drawing is not ${totoType.name}! Size is ${numbers.size}" }
                require(numbers.all { it in 1..totoType.totalNumbers }) { "Illegal number for ${totoType.name}! - $numbers" }

                drawings.add(
                    Draw(
                        year = file.name.toInt(),
                        id = index + 1,
                        numbers = numbers
                    )
                )
            }
        }

        return drawings
    }

    fun DoubleArray.containsNan(): Boolean {
        return this.any { it.isNaN() }
    }

    fun calculateGapSinceLast(number: Int, draws: List<Draw>, drawIndex: Int): Int {
        for (i in drawIndex - 1 downTo 0) {
            if (number in draws[i].numbers) {
                return drawIndex - i
            }
        }
        // The number has never appeared
        return -1
    }

    fun appearedInDraw(number: Int, draw: Draw): Int {
        return if (number in draw.numbers) 1 else 0
    }

    fun minMaxNormalizeBatchByColumn(features: Array<DoubleArray>): Array<DoubleArray> {
        // Perform Min-Max normalization for each column
        val numFeatures = features[0].size
        val mins = DoubleArray(numFeatures) { col -> features.minOf { it[col] } }
        val maxs = DoubleArray(numFeatures) { col -> features.maxOf { it[col] } }

        return features.map { row ->
            DoubleArray(numFeatures) { col ->
                (row[col] - mins[col]) / (maxs[col] - mins[col] + 1e-8f) // Adding epsilon to avoid divide by zero
            }
        }.toTypedArray()
    }

    fun minMaxNormalizeByColumn(arrays: List<DoubleArray>): MutableList<DoubleArray> {
        if (arrays.isEmpty()) return mutableListOf()

        val numColumns = arrays[0].size

        // Find min and max for each column
        val minValues = DoubleArray(numColumns) { col -> arrays.minOf { it[col] } }
        val maxValues = DoubleArray(numColumns) { col -> arrays.maxOf { it[col] } }

        // Normalize each column
        return arrays.map { row ->
            DoubleArray(numColumns) { col ->
                val min = minValues[col]
                val max = maxValues[col]
                if (max == min) 0.0 else (row[col] - min) / (max - min) // Avoid division by zero
            }
        }.toMutableList()
    }

    fun normalizeBasedOnMinMax(features: List<DoubleArray>) {
        features.forEach { normalizeBasedOnMinMax(it) }
    }

    fun normalizeBasedOnMinMax(feature: DoubleArray) {
        val min = feature.min()
        val max = feature.max()
        for (i in feature.indices) {
            feature[i] = 2 * ((feature[i] - min) / (max - min)) - 1
        }
    }

    fun normalizeBasedOnMinMaxPositive(features: List<DoubleArray>) {
        features.forEach { feature ->
            val min = feature.min()
            val max = feature.max()
            for (i in feature.indices) {
                feature[i] = (feature[i] - min) / (max - min) + 1e-6
            }
        }
    }

    fun normalizeBasedOnMeanVariance(features: MutableList<DoubleArray>) {
        val epsilon = 1e-5
        for (i in features.indices) {
            val mean = features[i].average()
            val variance = features[i].map { (it - mean) * (it - mean) }.average()
            features[i] = features[i].map { (it - mean) / sqrt(variance + epsilon) }.toDoubleArray()
        }
    }

    fun normalizeBasedOnMeanVariance(features: DoubleArray) {
        val epsilon = 1e-5
        val mean = features.average()
        val variance = features.map { (it - mean) * (it - mean) }.average()
        for (i in features.indices) {
            features[i] = (features[i] - mean) / sqrt(variance + epsilon)
        }
    }

    fun smoothTargets(factor: Double, targets: List<DoubleArray>) {
        targets.forEach {
            it[0] = it[0] * (1.0 - factor) + 0.5 * factor
        }
    }

    /**
     * Standardization (Z-Score Normalization)
     * Each input feature is scaled to have a mean of 0 and a standard deviation of 1.
     */
    fun normalizeBasedOnMeanStandardDeviation(input: DoubleArray, mean: DoubleArray, std: DoubleArray): DoubleArray {
        return DoubleArray(input.size) { i ->
            (input[i] - mean[i]) / (std[i] + 1e-7) // Add epsilon to avoid division by zero
        }
    }

    /**
     * Mean Calculation:
     * - Sum all values for each feature across the dataset.
     * - Divide by the number of samples to get the average (mean).
     * Variance Calculation:
     * - For each feature, compute the squared difference between each data point and the mean.
     * - Average these squared differences to get the variance.
     * Standard Deviation:
     * - Take the square root of the variance for each feature to get the standard deviation.
     */
    fun calculateMeanAndStd(data: Array<DoubleArray>): Pair<DoubleArray, DoubleArray> {
        val numFeatures = data[0].size // Number of features per input
        val mean = DoubleArray(numFeatures) { 0.0 }
        val variance = DoubleArray(numFeatures) { 0.0 }
        val numSamples = data.size // Number of data points

        // Calculate mean for each feature
        for (sample in data) {
            for (i in sample.indices) {
                mean[i] += sample[i]
            }
        }
        for (i in mean.indices) {
            mean[i] = mean[i] / numSamples
        }

        // Calculate variance for each feature
        for (sample in data) {
            for (i in sample.indices) {
                variance[i] += (sample[i] - mean[i]).pow(2)
            }
        }
        for (i in variance.indices) {
            variance[i] = variance[i] / numSamples
        }

        // Calculate standard deviation (sqrt of variance)
        val std = DoubleArray(numFeatures) { i -> kotlin.math.sqrt(variance[i]) }

        return Pair(mean, std)
    }

    fun clipGradient(value: Double, min: Double = -1.0, max: Double = 1.0): Double {
        return value.coerceIn(min, max)
    }

    /**
     * This approach to clipping gradients by the norm is effective for preventing large gradients from causing
     * instability in training. This is especially helpful when training very deep networks or using large learning rates.
     *
     * TODO: Add clipGradients method that utilizes clipping by value.
     */
    fun clipGradients(gradients: Array<DoubleArray>, maxNorm: Double = 1.0) {
        val totalNorm = sqrt(gradients.sumOf { it.sumOf { x -> x * x } })
        if (totalNorm > maxNorm) {
            val scale = maxNorm / (totalNorm + 1e-7)
            for (i in gradients.indices) {
                for (j in gradients[i].indices) {
                    gradients[i][j] *= scale
                }
            }
        }
    }

    fun DoubleArray.shiftRight(): DoubleArray {
        if (isEmpty()) return this // Handle empty array case

        val shifted = DoubleArray(size)
        shifted[0] = last() // Last element moves to the first position

        for (i in 1 until size) {
            shifted[i] = get(i - 1) // Shift others to the right
        }

        return shifted
    }

    fun DoubleArray.shiftLeft(): DoubleArray {
        if (isEmpty()) return this // Handle empty array case

        val shifted = DoubleArray(size)
        for (i in 0 until size - 1) {
            shifted[i] = get(i + 1) // Shift elements to the left
        }
        shifted[size - 1] = get(0) // First element moves to last position

        return shifted
    }
}