package deeplearning.util

import deeplearning.model.Draw
import deeplearning.model.Features
import deeplearning.util.Math.calculatePoissonProbability
import model.TotoType
import util.IO
import kotlin.math.pow
import kotlin.math.sqrt

object Data {

    fun getDrawFeatures(
        number: Int,
        draws: List<Draw>,
        drawIndex: Int
    ): Features {
        val frequency = calculateFrequency(
            number = number,
            draws = draws,
            drawIndex = drawIndex
        )
        val gapSinceLast = calculateGapSinceLast(
            number = number,
            draws = draws,
            drawIndex = drawIndex
        )
        val poissonProbability = calculatePoissonProbability(
            frequency = frequency,
            k = drawIndex
        )
        val inDraw = appearedInDraw(
            number = number,
            draw = draws[drawIndex]
        )
        val features = Features(
            number = number,
            frequency = frequency,
            gapSinceLast = gapSinceLast,
            poissonProbability = poissonProbability,
            inDraw = inDraw
        )

        return features
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

    fun calculateFrequency(number: Int, draws: List<Draw>, drawIndex: Int): Double {
        val recentDraws = draws.subList(0, drawIndex)
        val count = recentDraws.count { it.numbers.contains(number) }.toDouble()
        val frequency = count / recentDraws.size
        return frequency
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

    fun normalizeBatchByColumn(features: Array<DoubleArray>): Array<DoubleArray> {
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

    fun normalizeBasedOnMinMax(features: List<DoubleArray>) {
        features.forEach { feature ->
            val min = feature.min()
            val max = feature.max()
            for (i in feature.indices) {
                feature[i] = 2 * ((feature[i] - min) / (max - min)) - 1
            }
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
}