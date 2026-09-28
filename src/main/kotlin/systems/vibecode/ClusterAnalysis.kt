package systems.vibecode

import model.Draw
import model.TotoType
import kotlin.math.sqrt
import kotlin.random.Random

// ===========================================================================
// Suitcase cluster detection via co-occurrence residuals
// ===========================================================================
// Premise being tested (quota-constrained suitcases):
//   Numbers are partitioned into a small number of "suitcases". Each draw
//   pulls only a bounded number of numbers from each suitcase, so numbers
//   sharing a suitcase appear together LESS often than chance.
//
// Method:
//   1. Compute Pearson residuals for all number pairs.
//      R(a,b) = (O - E) / sqrt(E)
//      Negative R = pair appears less than independent expectation.
//   2. Greedily merge clusters whose cross-pair average residual is most
//      negative. Stop at K clusters.
//   3. Report within-cluster vs cross-cluster average residuals. A real
//      suitcase signal shows within-avg << cross-avg.
//   4. Compare against a null dataset generated from per-number frequencies
//      (kills pair structure, keeps marginals).
//
// If real data and null data give similar gaps, the suitcase premise is not
// supported by the data.
// ===========================================================================

data class ClusterStats(
    val withinAvg: Double,
    val crossAvg: Double,
    val withinStd: Double,
    val crossStd: Double,
    val gap: Double,               // crossAvg - withinAvg; higher = stronger signal
    val withinPairCount: Int,
    val crossPairCount: Int
)

/**
 * Counts: pairCounts[a][b] = number of draws where both (a+1) and (b+1) appeared.
 * counts[a] = number of draws where (a+1) appeared.
 */
fun buildOccurrenceStats(draws: List<Draw>, type: TotoType): Pair<Array<IntArray>, IntArray> {
    val n = type.totalNumbers
    val pairCounts = Array(n) { IntArray(n) }
    val counts = IntArray(n)

    for (draw in draws) {
        val nums = draw.numbers
        for (x in nums) counts[x - 1]++
        for (i in nums.indices) {
            for (j in i + 1 until nums.size) {
                val a = nums[i] - 1
                val b = nums[j] - 1
                pairCounts[a][b]++
                pairCounts[b][a]++
            }
        }
    }
    return pairCounts to counts
}

/**
 * Pearson residual matrix. R(a,b) = (O - E) / sqrt(E) where E = count[a] * count[b] / N.
 * Pairs with E < 0.5 are set to 0 (not enough data to say anything).
 */
fun computeResiduals(
    pairCounts: Array<IntArray>,
    counts: IntArray,
    totalDraws: Int
): Array<DoubleArray> {
    val n = counts.size
    val r = Array(n) { DoubleArray(n) }
    for (a in 0 until n) {
        for (b in a + 1 until n) {
            val e = counts[a].toDouble() * counts[b] / totalDraws
            if (e < 0.5) continue
            val res = (pairCounts[a][b] - e) / sqrt(e)
            r[a][b] = res
            r[b][a] = res
        }
    }
    return r
}

/**
 * Overall structure test: sum of squared residuals is chi-square distributed
 * under the null. Returns (chiSquare, degreesOfFreedom).
 */
fun overallStructureScore(residuals: Array<DoubleArray>): Pair<Double, Int> {
    var chi = 0.0
    var df = 0
    for (a in residuals.indices) {
        for (b in a + 1 until residuals.size) {
            val r = residuals[a][b]
            if (r == 0.0) continue
            chi += r * r
            df++
        }
    }
    return chi to df
}

/**
 * Greedy agglomerative clustering: at each step, merge the two clusters whose
 * average cross-pair residual is most negative. Stop when K clusters remain.
 */
fun clusterByNegativeResidual(
    residuals: Array<DoubleArray>,
    k: Int
): List<Set<Int>> {
    val n = residuals.size
    val groups = (0 until n).map { mutableSetOf(it) }.toMutableList()

    while (groups.size > k) {
        var bestI = -1
        var bestJ = -1
        var bestScore = Double.POSITIVE_INFINITY

        for (i in groups.indices) {
            for (j in i + 1 until groups.size) {
                var sum = 0.0
                var cnt = 0
                for (a in groups[i]) for (b in groups[j]) {
                    sum += residuals[a][b]
                    cnt++
                }
                val avg = if (cnt > 0) sum / cnt else 0.0
                if (avg < bestScore) {
                    bestScore = avg
                    bestI = i
                    bestJ = j
                }
            }
        }
        if (bestI < 0) break
        groups[bestI].addAll(groups[bestJ])
        groups.removeAt(bestJ)
    }
    return groups.map { g -> g.map { it + 1 }.toSet() }
}

/**
 * Within- and cross-cluster average residuals.
 */
fun evaluateClusters(residuals: Array<DoubleArray>, clusters: List<Set<Int>>): ClusterStats {
    val within = mutableListOf<Double>()
    val cross = mutableListOf<Double>()

    for (ci in clusters.indices) {
        for (cj in ci until clusters.size) {
            for (a in clusters[ci]) {
                for (b in clusters[cj]) {
                    if (a >= b) continue
                    val r = residuals[a - 1][b - 1]
                    if (r == 0.0) continue
                    if (ci == cj) within.add(r) else cross.add(r)
                }
            }
        }
    }

    fun avg(v: List<Double>) = if (v.isEmpty()) 0.0 else v.average()
    fun std(v: List<Double>): Double {
        if (v.size < 2) return 0.0
        val m = avg(v)
        return sqrt(v.sumOf { (it - m) * (it - m) } / (v.size - 1))
    }

    val wa = avg(within)
    val ca = avg(cross)
    return ClusterStats(
        withinAvg = wa,
        crossAvg = ca,
        withinStd = std(within),
        crossStd = std(cross),
        gap = ca - wa,
        withinPairCount = within.size,
        crossPairCount = cross.size
    )
}

/**
 * Null model: regenerate each draw by sampling `type.size` distinct numbers
 * with probability proportional to their observed total counts. Marginals are
 * approximately preserved; pair structure is destroyed.
 */
fun generateNullDraws(draws: List<Draw>, type: TotoType, seed: Long = 42L): List<Draw> {
    val rng = Random(seed)
    val counts = IntArray(type.totalNumbers)
    for (d in draws) for (x in d.numbers) counts[x - 1]++
    val total = counts.sum().toDouble()
    val weights = DoubleArray(counts.size) { counts[it] / total }

    return draws.map { d ->
        val chosen = mutableSetOf<Int>()
        while (chosen.size < type.size) {
            val r = rng.nextDouble()
            var acc = 0.0
            for (i in weights.indices) {
                acc += weights[i]
                if (r <= acc) { chosen.add(i + 1); break }
            }
        }
        val nums = chosen.toIntArray().also { it.sort() }
        d.copy(numbers = nums)
    }
}

/**
 * Compact visual map. Numbers are reordered so that cluster members are
 * grouped together. Symbols:
 *   '-' strong negative residual (pair appears less than chance)
 *   '+' strong positive residual (pair appears more than chance)
 *   ' ' within noise
 */
fun printResidualMap(residuals: Array<DoubleArray>, clusters: List<Set<Int>>, threshold: Double = 1.5) {
    val order = clusters.flatMap { it.sorted() }
    val clusterOf = IntArray(residuals.size)
    clusters.forEachIndexed { ci, c -> c.forEach { clusterOf[it - 1] = ci } }

    print("     ")
    order.forEach { print("%3d".format(it)) }
    println()
    order.forEach { a ->
        print("%3d |".format(a))
        order.forEach { b ->
            val r = if (a == b) 0.0 else residuals[a - 1][b - 1]
            val sym = when {
                a == b -> " . "
                r <= -threshold -> " - "
                r >= threshold -> " + "
                else -> "   "
            }
            print(sym)
        }
        println("   [c${clusterOf[a - 1] + 1}]")
    }
    println("Legend: '-'=strong negative residual, '+'=strong positive residual, ' '=noise, '.'=self")
}

fun printClusterReport(clusters: List<Set<Int>>, stats: ClusterStats, label: String) {
    println("--- $label ---")
    println("  within-cluster avg residual: %+.3f  (std %.3f, %d pairs)"
        .format(stats.withinAvg, stats.withinStd, stats.withinPairCount))
    println("  cross-cluster  avg residual: %+.3f  (std %.3f, %d pairs)"
        .format(stats.crossAvg, stats.crossStd, stats.crossPairCount))
    println("  gap (cross - within)       : %+.3f".format(stats.gap))
    clusters.forEachIndexed { i, c ->
        println("  Cluster ${i + 1} (${c.size}): ${c.sorted().joinToString()}")
    }
}

// ---------------------------------------------------------------------------
// Driver
// ---------------------------------------------------------------------------

fun runSuitcaseClusterAnalysis(
    draws: List<Draw>,
    type: TotoType,
    kValues: List<Int> = listOf(3, 4, 5, 6, 7, 8),
    printMapForBestK: Boolean = true
) {
    println("Total draws: ${draws.size}  |  Type: $type")
    println()

    // ---- Real data ----
    val (pairCounts, counts) = buildOccurrenceStats(draws, type)
    val residuals = computeResiduals(pairCounts, counts, draws.size)
    val (chi, df) = overallStructureScore(residuals)
    println("Overall structure test (real data):")
    println("  chi-square = %.1f, df = %d, chi/df = %.3f (expect ~1.0 under independence)"
        .format(chi, df, chi / df))
    println()

    // ---- Null data ----
    val nullDraws = generateNullDraws(draws, type)
    val (nPairCounts, nCounts) = buildOccurrenceStats(nullDraws, type)
    val nResiduals = computeResiduals(nPairCounts, nCounts, nullDraws.size)
    val (nChi, nDf) = overallStructureScore(nResiduals)
    println("Overall structure test (null):")
    println("  chi-square = %.1f, df = %d, chi/df = %.3f"
        .format(nChi, nDf, nChi / nDf))
    println()

    var bestK = -1
    var bestDelta = Double.NEGATIVE_INFINITY
    var bestClusters: List<Set<Int>> = emptyList()

    for (k in kValues) {
        println("========== K = $k ==========")
        val realClusters = clusterByNegativeResidual(residuals, k)
        val realStats = evaluateClusters(residuals, realClusters)
        printClusterReport(realClusters, realStats, "Real data")

        val nullClusters = clusterByNegativeResidual(nResiduals, k)
        val nullStats = evaluateClusters(nResiduals, nullClusters)
        printClusterReport(nullClusters, nullStats, "Null (frequency-preserving)")

        val delta = realStats.gap - nullStats.gap
        val verdict = when {
            delta > 0.3 -> "(strong signal)"
            delta > 0.1 -> "(weak signal)"
            delta < -0.1 -> "(worse than null)"
            else -> "(no signal)"
        }
        println("  >>> Real gap - Null gap = %+.3f  %s".format(delta, verdict))
        println()

        if (delta > bestDelta) {
            bestDelta = delta
            bestK = k
            bestClusters = realClusters
        }
    }

    if (bestK > 0) {
        println("=======================================")
        println("Best K = $bestK  (delta = %+.3f)".format(bestDelta))
        println("=======================================")
        if (printMapForBestK) {
            println()
            println("Residual map (sorted by cluster):")
            printResidualMap(residuals, bestClusters)
        }
    }
}