package systems.vibecode

import model.Draw
import model.TotoType
import kotlin.math.sqrt

// ===========================================================================
// Option B – rolling short-window structural scan
// ===========================================================================
// Tests whether suitcase-like structure appears in short, localized windows
// rather than being stable over years. If suitcases rotate on monthly or
// quarterly timescales, this is where we'd see them.
// ===========================================================================

data class WindowReport(
    val startIndex: Int,
    val endIndex: Int,
    val chiSquare: Double,
    val df: Int,
    val chiPerDf: Double,
    val hotNumbers: List<Int>,                          // top-K by frequency
    val topPosPairs: List<Pair<Pair<Int, Int>, Double>> // positive residual pairs
)

fun computeWindowReport(
    draws: List<Draw>,
    type: TotoType,
    startIdx: Int,
    windowSize: Int,
    topK: Int = 10
): WindowReport {
    val endIdx = (startIdx + windowSize).coerceAtMost(draws.size)
    val window = draws.subList(startIdx, endIdx)
    val (pairs, counts) = buildOccurrenceStats(window, type)
    val residuals = computeResiduals(pairs, counts, window.size)

    var chi = 0.0
    var df = 0
    for (a in residuals.indices) {
        for (b in a + 1 until residuals.size) {
            val r = residuals[a][b]
            if (r != 0.0) {
                chi += r * r
                df++
            }
        }
    }

    val hot = counts.mapIndexed { i, c -> (i + 1) to c }
        .sortedByDescending { it.second }
        .take(topK)
        .map { it.first }

    val posPairs = mutableListOf<Pair<Pair<Int, Int>, Double>>()
    for (a in residuals.indices) {
        for (b in a + 1 until residuals.size) {
            if (residuals[a][b] > 1.5) {
                posPairs.add(Pair(Pair(a + 1, b + 1), residuals[a][b]))
            }
        }
    }
    val topPos = posPairs.sortedByDescending { it.second }.take(topK)

    return WindowReport(
        startIndex = startIdx,
        endIndex = endIdx - 1,
        chiSquare = chi,
        df = df,
        chiPerDf = if (df > 0) chi / df else 0.0,
        hotNumbers = hot,
        topPosPairs = topPos
    )
}

fun jaccard(a: Set<Int>, b: Set<Int>): Double {
    if (a.isEmpty() && b.isEmpty()) return 1.0
    val inter = a.intersect(b).size
    val union = a.union(b).size
    return inter.toDouble() / union
}

fun rollingWindowScan(
    draws: List<Draw>,
    type: TotoType,
    windowSize: Int = 50,
    step: Int = 5,
    topK: Int = 10
) {
    val sorted = draws.sortedWith(compareBy<Draw> { it.year }.thenBy { it.id })
    println("Rolling window scan: windowSize=$windowSize, step=$step, totalDraws=${sorted.size}")

    // ---- Real ----
    val realWindows = mutableListOf<WindowReport>()
    var start = 0
    while (start + windowSize <= sorted.size) {
        realWindows.add(computeWindowReport(sorted, type, start, windowSize, topK))
        start += step
    }

    // ---- Null (frequency-preserving, pair structure destroyed) ----
    val nullDraws = generateNullDraws(sorted, type, seed = 42L)
    val nullWindows = mutableListOf<WindowReport>()
    start = 0
    while (start + windowSize <= nullDraws.size) {
        nullWindows.add(computeWindowReport(nullDraws, type, start, windowSize, topK))
        start += step
    }

    println("Windows: ${realWindows.size} real, ${nullWindows.size} null")
    println()

    // ---- Signal 1: chi/df per window ----
    println("=== Signal 1: chi/df per window ===")
    val realChi = realWindows.map { it.chiPerDf }
    val nullChi = nullWindows.map { it.chiPerDf }
    println(
        "  Real chi/df: mean=%.3f  std=%.3f  max=%.3f  min=%.3f"
            .format(realChi.average(), std(realChi), realChi.max(), realChi.min())
    )
    println(
        "  Null chi/df: mean=%.3f  std=%.3f  max=%.3f  min=%.3f"
            .format(nullChi.average(), std(nullChi), nullChi.max(), nullChi.min())
    )
    val realMax = realChi.max()
    val nullMax = nullChi.max()
    println(
        "  Max comparison: real=%.3f vs null=%.3f  (delta=%+.3f)"
            .format(realMax, nullMax, realMax - nullMax)
    )
    println()

    // Print the top 5 real windows
    val topReal = realWindows.sortedByDescending { it.chiPerDf }.take(5)
    println("  Top-5 most structured real windows:")
    for (w in topReal) {
        println(
            "    draws ${w.startIndex}..${w.endIndex}  chi/df=%.3f  hot=${w.hotNumbers.take(8).joinToString()}"
                .format(w.chiPerDf)
        )
    }
    println()

    // ---- Signal 2: hot-set stability ----
    println("=== Signal 2: hot-set stability (Jaccard between adjacent windows) ===")
    val realJ = mutableListOf<Double>()
    val nullJ = mutableListOf<Double>()
    for (i in 1 until realWindows.size) {
        realJ.add(jaccard(realWindows[i - 1].hotNumbers.toSet(), realWindows[i].hotNumbers.toSet()))
    }
    for (i in 1 until nullWindows.size) {
        nullJ.add(jaccard(nullWindows[i - 1].hotNumbers.toSet(), nullWindows[i].hotNumbers.toSet()))
    }
    println("  Real avg Jaccard: %.3f  (std %.3f)".format(realJ.average(), std(realJ)))
    println("  Null avg Jaccard: %.3f  (std %.3f)".format(nullJ.average(), std(nullJ)))
    println("  Delta (real - null): %+.3f".format(realJ.average() - nullJ.average()))
    println()

    // ---- Signal 3: positive pair bursts ----
    println("=== Signal 3: positive-pair count per window (residual > 1.5) ===")
    val realPos = realWindows.map { it.topPosPairs.size }
    val nullPos = nullWindows.map { it.topPosPairs.size }
    println("  Real: mean=%.2f  max=%d  min=%d".format(realPos.average(), realPos.max(), realPos.min()))
    println("  Null: mean=%.2f  max=%d  min=%d".format(nullPos.average(), nullPos.max(), nullPos.min()))

    // Print top 3 windows by positive pair count
    val topPosWindows = realWindows.sortedByDescending { it.topPosPairs.size }.take(3)
    println()
    println("  Top-3 real windows with most positive pairs:")
    for (w in topPosWindows) {
        println(
            "    draws ${w.startIndex}..${w.endIndex}  posPairs=${w.topPosPairs.size}  chi/df=%.3f"
                .format(w.chiPerDf)
        )
        println(
            "      top: ${
                w.topPosPairs.take(6).joinToString { "(${it.first.first}-${it.first.second}:%.2f)".format(it.second) }
            }"
        )
    }
}

private fun std(v: List<Double>): Double {
    if (v.size < 2) return 0.0
    val m = v.average()
    return sqrt(v.sumOf { (it - m) * (it - m) } / (v.size - 1))
}