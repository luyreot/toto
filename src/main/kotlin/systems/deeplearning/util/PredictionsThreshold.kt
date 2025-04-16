package systems.deeplearning.util

import model.TotoType

fun evaluateThresholdPredictionPerformance(
    totoType: TotoType,
    draws: List<List<Int>>,
    model: (DoubleArray) -> DoubleArray,
    windowSize: Int = 10,
    threshold: Double = 0.1
) {
    var totalSamples = 0
    var totalHits = 0
    val hitDistribution = mutableMapOf<Int, Int>()
    val predictionCounts = mutableListOf<Int>()

    for (i in 0 until draws.size - windowSize - 1) {
        val inputDraws = draws.subList(i, i + windowSize)
        val actualDraw = draws[i + windowSize]

        // Flatten input
        val input = DoubleArray(windowSize * totoType.totalNumbers) { 0.1 }
        for ((drawIndex, draw) in inputDraws.withIndex()) {
            for (num in draw) {
                input[drawIndex * totoType.totalNumbers + (num - 1)] = 0.9
            }
        }

        val output = model(input)
        val predicted = getNumbersAboveThreshold(output, threshold)

        val hits = predicted.count { it in actualDraw }

        // Stats tracking
        totalSamples++
        totalHits += hits
        hitDistribution[hits] = hitDistribution.getOrDefault(hits, 0) + 1
        predictionCounts += predicted.size
    }

    // Summary
    val averageHits = totalHits.toDouble() / totalSamples
    val averagePredictionCount = predictionCounts.average()

    println("📊 Evaluation over $totalSamples samples")
    println("🔎 Threshold = $threshold")
    println("🎯 Average hits per draw: %.2f".format(averageHits))
    println("📌 Avg numbers predicted per draw: %.2f".format(averagePredictionCount))
    println("📈 Hit distribution:")
    hitDistribution.toSortedMap().forEach { (hit, count) ->
        println(" - $hit hit(s): $count times")
    }
}

fun getNumbersAboveThreshold(
    output: DoubleArray,
    threshold: Double = 0.1
): List<Int> {
    return output.mapIndexed { index, prob ->
        if (prob > threshold) index + 1 else null
    }.filterNotNull()
}