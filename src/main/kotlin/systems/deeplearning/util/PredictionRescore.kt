package systems.deeplearning.util

import kotlin.math.exp

fun getRescoredTopN(
    output: DoubleArray,
    topN: Int,
    weights: DoubleArray // Length must be 49 or 35 depending on lotto type
): List<Int> {
    return output.mapIndexed { index, prob ->
        val adjusted = prob * weights[index]
        index to adjusted
    }.sortedByDescending { it.second }
        .take(topN)
        .map { it.first + 1 } // Convert from index (0-based) to number (1-based)
}

fun blendWeights(
    frequencyWeights: DoubleArray = doubleArrayOf(),
    poissonWeights: DoubleArray = doubleArrayOf(),
    coOccurrenceWeights: DoubleArray = doubleArrayOf(),
    frequencyFactor: Double = 0.0,
    poissonFactor: Double = 0.0,
    coOccurrenceFactor: Double = 0.0
): DoubleArray {
    val numBalls = listOf(frequencyWeights, poissonWeights, coOccurrenceWeights).first().size

    val totalFactor = frequencyFactor + poissonFactor + coOccurrenceFactor

    if (totalFactor == 0.0) {
        throw IllegalArgumentException("At least one factor must be non-zero.")
    }

    return DoubleArray(numBalls) { i ->
        val f = frequencyWeights[i].times(frequencyFactor)
        val p = poissonWeights[i].times(poissonFactor)
        val c = coOccurrenceWeights[i].times(coOccurrenceFactor)
        (f + p + c) / totalFactor
    }
}

fun calculateRecentFrequencyWeights(
    draws: List<List<Int>>,
    numBalls: Int,
    recentWindow: Int
): DoubleArray {
    val freq = IntArray(numBalls) { 0 }

    val recentDraws = draws.takeLast(recentWindow)
    for (draw in recentDraws) {
        for (n in draw) {
            freq[n - 1] += 1
        }
    }

    val maxFreq = freq.max().coerceAtLeast(1)
    return DoubleArray(numBalls) { i ->
        // Normalize: map to 0.8 – 1.2 range for soft influence
        0.8 + 0.4 * (freq[i].toDouble() / maxFreq)
    }
}

fun calculatePoissonGapWeights(
    draws: List<List<Int>>,
    numBalls: Int,
    avgGap: Double = 5.0, // average reappearance gap (tune this)
    minWeight: Double = 0.8,
    maxWeight: Double = 1.2
): DoubleArray {
    val lastSeen = IntArray(numBalls) { -1 }

    for (i in draws.indices.reversed()) {
        val draw = draws[i]
        for (n in draw) {
            if (lastSeen[n - 1] == -1) {
                lastSeen[n - 1] = draws.size - 1 - i
            }
        }
    }

    return DoubleArray(numBalls) { i ->
        val gap = if (lastSeen[i] == -1) draws.size else lastSeen[i]
        val probability = 1 - exp(-gap.toDouble() / avgGap)  // Poisson CDF
        minWeight + (maxWeight - minWeight) * probability
    }
}

fun calculateCoOccurrenceBoost(
    draws: List<List<Int>>,
    numBalls: Int,
    topPredictions: List<Int>, // model's TopN predictions
    recentWindow: Int = 100,
    minBoost: Double = 0.9,
    maxBoost: Double = 1.2
): DoubleArray {
    val recentDraws = draws.takeLast(recentWindow)
    val coOccurCounts = IntArray(numBalls) { 0 }

    for (draw in recentDraws) {
        if (draw.any { it in topPredictions }) {
            for (n in draw) {
                coOccurCounts[n - 1]++
            }
        }
    }

    val maxCount = coOccurCounts.max().coerceAtLeast(1)

    return DoubleArray(numBalls) { i ->
        minBoost + (maxBoost - minBoost) * (coOccurCounts[i].toDouble() / maxCount)
    }
}