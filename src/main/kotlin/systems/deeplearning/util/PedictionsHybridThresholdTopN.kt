package systems.deeplearning.util

import model.TotoType

fun evaluateHybridPredictionPerformance(
    totoType: TotoType,
    draws: List<List<Int>>,
    model: (DoubleArray) -> DoubleArray,
    windowSize: Int = 10,
    topN: Int = 15,
    threshold: Double = 0.1
) {
    var totalSamples = 0
    var totalHits = 0
    val hitDistribution = mutableMapOf<Int, Int>()
    val predictionCounts = mutableListOf<Int>()

    for (i in 0 until draws.size - windowSize - 1) {
        val inputDraws = draws.subList(i, i + windowSize)
        val actualDraw = draws[i + windowSize]

        // Create input vector
        val input = DoubleArray(windowSize * totoType.totalNumbers) { 0.1 }
        for ((drawIndex, draw) in inputDraws.withIndex()) {
            for (num in draw) {
                input[drawIndex * totoType.totalNumbers + (num - 1)] = 0.9
            }
        }

        val output = model(input)
        val topNPredictions = getTopNPredictions(output, topN)
        val thresholdPredictions = getNumbersAboveThreshold(output, threshold)

        val hybridShortlist = (topNPredictions + thresholdPredictions).distinct().sorted()

        val hits = hybridShortlist.count { it in actualDraw }

        // Tracking stats
        totalSamples++
        totalHits += hits
        hitDistribution[hits] = hitDistribution.getOrDefault(hits, 0) + 1
        predictionCounts += hybridShortlist.size
    }

    val averageHits = totalHits.toDouble() / totalSamples
    val averagePredictionCount = predictionCounts.average()

    println("📊 Evaluation over $totalSamples samples")
    println("🔀 Hybrid Strategy: Top-$topN + Threshold > $threshold")
    println("🎯 Average hits per draw: %.2f".format(averageHits))
    println("📌 Avg shortlist size per draw: %.2f".format(averagePredictionCount))
    println("📈 Hit distribution:")
    hitDistribution.toSortedMap().forEach { (hit, count) ->
        println(" - $hit hit(s): $count times")
    }
}