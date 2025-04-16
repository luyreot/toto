package systems.deeplearning.util

import model.TotoType

fun evaluateTopNModelPerformance(
    totoType: TotoType,
    draws: List<List<Int>>,
    model: (DoubleArray) -> DoubleArray,  // function that maps input -> prediction
    windowSize: Int,
    topN: Int
) {
    var total = 0
    var totalHits = 0
    val hitDistribution = mutableMapOf<Int, Int>()

    for (i in 0 until draws.size - windowSize - 1) {
        val inputDraws = draws.subList(i, i + windowSize)
        val actualDraw = draws[i + windowSize]

        val input = DoubleArray(windowSize * totoType.totalNumbers) { 0.1 }
        for ((drawIndex, draw) in inputDraws.withIndex()) {
            for (num in draw) {
                input[drawIndex * totoType.totalNumbers + (num - 1)] = 0.9
            }
        }

        val output = model(input)
        val topNPredictions = getTopNPredictions(output, topN)

        val hits = countHitsInTopN(topNPredictions, actualDraw)
        totalHits += hits
        total++
        hitDistribution[hits] = hitDistribution.getOrDefault(hits, 0) + 1
    }

    val averageHits = totalHits.toDouble() / total
    println("📊 Evaluation over $total samples")
    println("🎯 Average hits in top-$topN: %.2f".format(averageHits))
    println("📈 Hit distribution:")
    for (hitCount in hitDistribution.keys.sorted()) {
        val freq = hitDistribution[hitCount] ?: 0
        println(" - $hitCount hit(s): $freq times")
    }
}

fun evaluateRescoredTopNModelPerformance(
    totoType: TotoType,
    draws: List<List<Int>>,
    model: (DoubleArray) -> DoubleArray, // function that maps input -> prediction
    windowSize: Int,
    topN: Int,
    recentWindow: Int // 34
) {
    var total = 0
    var totalHits = 0
    val hitDistribution = mutableMapOf<Int, Int>()

    for (i in 0 until draws.size - windowSize - 1) {
        val inputDraws = draws.subList(i, i + windowSize)
        val actualDraw = draws[i + windowSize]

        val input = DoubleArray(windowSize * totoType.totalNumbers) { 0.1 }
        for ((drawIndex, draw) in inputDraws.withIndex()) {
            for (num in draw) {
                input[drawIndex * totoType.totalNumbers + (num - 1)] = 0.9
            }
        }

        val output = model(input)
        val topNPredictions = getTopNPredictions(output, topN)
        val frequencyWeights = calculateRecentFrequencyWeights(draws, totoType.totalNumbers, recentWindow)
        val poissonWeights = calculatePoissonGapWeights(draws, totoType.totalNumbers)
        val coOccurrenceWeights = calculateCoOccurrenceBoost(draws, totoType.totalNumbers, topNPredictions, recentWindow)

        val blendedWeights = blendWeights(
            frequencyWeights = frequencyWeights,
            poissonWeights = poissonWeights,
            coOccurrenceWeights = coOccurrenceWeights,
            frequencyFactor = 0.5,
            poissonFactor = 0.05,
            coOccurrenceFactor = 0.5
        )

        val rescoredTopNPredictions = getRescoredTopN(output, topN, blendedWeights)

        val hits = countHitsInTopN(rescoredTopNPredictions, actualDraw)
        totalHits += hits
        total++
        hitDistribution[hits] = hitDistribution.getOrDefault(hits, 0) + 1
    }

    val averageHits = totalHits.toDouble() / total
    println("📊 Evaluation over $total samples")
    println("🎯 Average hits in top-$topN: %.2f".format(averageHits))
    println("📈 Hit distribution:")
    for (hitCount in hitDistribution.keys.sorted()) {
        val freq = hitDistribution[hitCount] ?: 0
        println(" - $hitCount hit(s): $freq times")
    }
}

fun getTopNPredictions(
    output: DoubleArray,
    topN: Int
): List<Int> {
    return output
        .mapIndexed { index, prob -> index to prob }
        .sortedByDescending { it.second }
        .take(topN)
        .map { it.first + 1 }  // Convert index (0–48) to number (1–49)
}

fun countHitsInTopN(
    predictedTopN: List<Int>,
    actualDraw: List<Int>
): Int {
    return predictedTopN.count { it in actualDraw }
}