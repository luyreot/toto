package systems.deeplearning.util

import model.TotoType

fun evaluateBucketizedTopN(
    totoType: TotoType,
    draws: List<List<Int>>,
    model: (DoubleArray) -> DoubleArray,
    windowSize: Int,
    numBuckets: Int = 12,
    topPerBucket: Int = 2
) {
    val hitCounts = IntArray(totoType.size + 1) { 0 }
    var totalHits = 0

    for (i in 0 until draws.size - windowSize - 1) {
        val inputDraws = draws.subList(i, i + windowSize)
        val actualDraw = draws[i + windowSize].toSet()

        val input = DoubleArray(windowSize * totoType.totalNumbers) { 0.0 }
        for ((drawIndex, draw) in inputDraws.withIndex()) {
            for (n in draw) {
                input[drawIndex * totoType.totalNumbers + (n - 1)] = 1.0
            }
        }

        val output = model(input)
        val selected = bucketizedTopN(totoType, output, numBuckets, topPerBucket).toSet()
        val hits = selected.count { it in actualDraw }

        hitCounts[hits]++
        totalHits += hits
    }

    val totalDraws = draws.size - windowSize - 1
    val avgHits = totalHits.toDouble() / totalDraws

    println("📊 Evaluation over $totalDraws samples")
    println("🎯 Average hits per draw: ${String.format("%.2f", avgHits)}")
    println("📈 Hit distribution:")
    for (i in 0..totoType.size) {
        println(" - $i hit(s): ${hitCounts[i]} times")
    }
}

fun bucketizedTopN(
    totoType: TotoType,
    output: DoubleArray,
    numBuckets: Int = 12,
    topPerBucket: Int = 2
): List<Int> {
    val ranked = output
        .mapIndexed { index, score -> index to score }
        .sortedByDescending { it.second }

    val picks = mutableListOf<Int>()
    val bucketSize = totoType.totalNumbers / numBuckets
    val extra = totoType.totalNumbers % numBuckets // distribute the remainder

    var currentIndex = 0
//    println("🧠 Bucketized TopN Selection:")
    for (bucketIndex in 0 until numBuckets) {
        val thisBucketSize = bucketSize + if (bucketIndex < extra) 1 else 0
        val bucket = ranked.subList(currentIndex, currentIndex + thisBucketSize)
        // TODO: Sometimes taking the first or last numbers makes a difference
        val topK = bucket.takeLast(topPerBucket)

        for ((rankOffset, pair) in topK.withIndex()) {
            val (numberIndex, score) = pair
            val globalRank = currentIndex + rankOffset + 1
            picks.add(numberIndex + 1)
//            println(
//                " - Picked number ${numberIndex + 1} from global rank $globalRank, bucket ${bucketIndex + 1}, score=${
//                    String.format(
//                        "%.4f",
//                        score
//                    )
//                }"
//            )
        }

        currentIndex += thisBucketSize
    }

//    println("✅ Final picked set: ${picks.sorted()}\n")
    return picks
}