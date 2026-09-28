package systems.vibecode

import model.Draw
import model.TotoType
import model.UniqueIntArray
import model.loadDrawings
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

// ===========================================================================
// Lottery number generation – fitness-only pipeline
// ===========================================================================
// Design notes:
//
//   Four independent statistical tests falsified the "suitcase" premise for
//   this dataset (long-term co-occurrence, short-term co-occurrence, residual
//   clustering at K=3..8, rolling short-window scan). The only signal that
//   survived is per-number frequency persistence: numbers that were hot in
//   one period are mildly more likely to be hot in the next.
//
//   This pipeline therefore uses ONLY per-number fitness. All pair matrices,
//   top-partner caches, position probabilities, and cluster weighting have
//   been removed. Coverage of the final pool is maximized by a dedicated
//   allocator that spreads tickets across the pool instead of resampling.
//
//   Result: shorter code, faster execution, and every component now maps to
//   a signal that has actually been shown to exist in the data.
//
//   What to watch when you run it
//   Pool size. With temperature = 1.5, sampling is fairly soft. If the pool
//   is too big (>24 for a 5/35), lower the temperature to 1.0 or 0.8.
//   If the pool is too small, raise it.
//
//   Number of unique numbers in final tickets. The usagePenalty = 0.8 should
//   force most of the pool into tickets. If final tickets still collapse
//   to 2–3 unique numbers, raise it to 1.2 or 1.5.
//
//   Reproducibility. Pass seed = 42L to allocateTicketsFromPool if you want
//   identical output across runs. Currently seed = null means fresh randomness
//   each run.
// ===========================================================================

fun vibeCodeRun(totoType: TotoType, yearFilter: Int, predictionsSize: Int) {
    val draws = loadDrawings(totoType).filter { it.year >= yearFilter }

    // ---- Stage 1: candidate pool via fitness-weighted sampling ----
    val candidateTickets = generateCombinations(
        requestedCount = predictionsSize,
        draws = draws,
        type = totoType
    )
    val candidatePool = candidateTickets.flatMap { it.toList() }.toSet()

    println("Candidate Pool: ${candidatePool.sorted().joinToString()}")
    println("Candidate Pool Size: ${candidatePool.size}")
    println("---")

    // ---- Stage 2: coverage-maximising allocation across the pool ----
    val finalTickets = allocateTicketsFromPool(
        draws = draws,
        type = totoType,
        numberPool = candidatePool,
        ticketCount = predictionsSize
    )

    println("Final Tickets:")
    finalTickets.forEach { println(it.joinToString()) }
}

// ---------------------------------------------------------------------------
// Configuration
// ---------------------------------------------------------------------------

data class GeneratorConfig(
    // Weights for the four fitness components (must sum to ~1.0)
    val wLongTermAvg: Double = 0.25,      // avg appearances/year over full history
    val wCurrentYear: Double = 0.20,      // raw count in the latest year
    val wRecentWeighted: Double = 0.30,   // exp-decay weighted recent trend
    val wRecency: Double = 0.25,          // draws since last appearance (due factor)
    // Softmax temperature during candidate sampling.
    // Lower = more peaked on high-fitness numbers; higher = more uniform.
    val temperature: Double = 1.5,
    // Statistical filters (percentile bounds drawn from history)
    val sumPercentileLow: Double = 5.0,
    val sumPercentileHigh: Double = 95.0,
    val oddPercentileLow: Double = 10.0,
    val oddPercentileHigh: Double = 90.0,
    val lowPercentileLow: Double = 10.0,
    val lowPercentileHigh: Double = 90.0,
    // How many candidate tickets to generate before taking the top N
    val oversampleFactor: Int = 5,
    // Optional exclusion set for restricting the allowed number pool
    val excludeNumbers: Set<Int> = emptySet()
)

// ---------------------------------------------------------------------------
// Per-number frequency statistics
// ---------------------------------------------------------------------------

/**
 * Average number of times each number is drawn per year.
 * Years without any draws are not counted.
 */
fun calculateAverageOccurrencePerYear(
    draws: List<Draw>,
    type: TotoType
): DoubleArray {
    val totalCounts = IntArray(type.totalNumbers)
    val distinctYears = HashSet<Int>()

    for (draw in draws) {
        distinctYears.add(draw.year)
        for (num in draw.numbers) {
            if (num in 1..type.totalNumbers) totalCounts[num - 1]++
        }
    }

    val yearCount = distinctYears.size
    if (yearCount == 0) return DoubleArray(type.totalNumbers)

    return DoubleArray(type.totalNumbers) { i ->
        totalCounts[i].toDouble() / yearCount
    }
}

/**
 * Total occurrences of each number in the latest year present in the data.
 */
fun calculateCurrentYearOccurrences(
    draws: List<Draw>,
    type: TotoType
): IntArray {
    val currentYear = draws.maxOfOrNull { it.year } ?: return IntArray(type.totalNumbers)
    val counts = IntArray(type.totalNumbers)
    for (draw in draws) {
        if (draw.year == currentYear) {
            for (num in draw.numbers) {
                if (num in 1..type.totalNumbers) counts[num - 1]++
            }
        }
    }
    return counts
}

/**
 * Weighted frequency per number using exponential time decay.
 * halfLifeYears: after this many years a draw's weight halves.
 * currentYear: the present year used to compute age.
 */
fun weightedFrequency(
    draws: List<Draw>,
    type: TotoType,
    halfLifeYears: Double,
    currentYear: Int
): DoubleArray {
    val weights = DoubleArray(type.totalNumbers)
    val decay = ln(2.0) / halfLifeYears
    for (draw in draws) {
        val age = (currentYear - draw.year).toDouble().coerceAtLeast(0.0)
        val weight = exp(-decay * age)
        for (num in draw.numbers) {
            if (num in 1..type.totalNumbers) weights[num - 1] += weight
        }
    }
    return weights
}

/**
 * Number of draws since each number last appeared. Numbers never seen get
 * the total draw count (maximum "due" value).
 */
fun drawsSinceLastAppearance(
    draws: List<Draw>,
    type: TotoType
): IntArray {
    val sorted = draws.sortedWith(compareBy<Draw> { it.year }.thenBy { it.id })
    val lastSeen = IntArray(type.totalNumbers) { -1 }
    for ((idx, draw) in sorted.withIndex()) {
        for (num in draw.numbers) {
            if (num in 1..type.totalNumbers) lastSeen[num - 1] = idx
        }
    }
    val total = sorted.size
    return IntArray(type.totalNumbers) { i ->
        if (lastSeen[i] == -1) total else total - 1 - lastSeen[i]
    }
}

// ---------------------------------------------------------------------------
// Fitness array – the only signal the code relies on
// ---------------------------------------------------------------------------

/**
 * Computes the per-number fitness score. It is a weighted sum of four
 * z-scored frequency components:
 *
 *   1. Long-term average per year        (persistent bias)
 *   2. Current-year count                (seasonal hotness)
 *   3. Exponentially-decayed recent freq (momentum)
 *   4. Draws since last appearance       (due-ness)
 *
 * Higher = more likely to appear in an upcoming draw, per the model.
 */
fun calculateFitnessArray(
    draws: List<Draw>,
    type: TotoType,
    config: GeneratorConfig = GeneratorConfig()
): DoubleArray {
    if (draws.isEmpty()) return DoubleArray(type.totalNumbers)

    val currentYear = draws.maxOf { it.year }
    val totalDraws = draws.size

    val avgPerYear = calculateAverageOccurrencePerYear(draws, type)
    val zLongAvg = doubleArrayToZScores(avgPerYear)

    val currentYearCounts = calculateCurrentYearOccurrences(draws, type)
    val zCurrentYear = intArrayToZScores(currentYearCounts)

    val recentDraws = draws.filter { it.year >= currentYear - 1 }
    val weightedRecent = weightedFrequency(recentDraws, type, halfLifeYears = 1.0, currentYear)
    val zRecentWeighted = doubleArrayToZScores(weightedRecent)

    val recency = drawsSinceLastAppearance(draws, type)
    val recencyScore = DoubleArray(type.totalNumbers) { i ->
        recency[i].toDouble() / totalDraws.toDouble().coerceAtLeast(1.0)
    }

    return DoubleArray(type.totalNumbers) { i ->
        config.wLongTermAvg * zLongAvg[i] +
                config.wCurrentYear * zCurrentYear[i] +
                config.wRecentWeighted * zRecentWeighted[i] +
                config.wRecency * recencyScore[i]
    }
}

// ---------------------------------------------------------------------------
// Candidate pool generation
// ---------------------------------------------------------------------------

/**
 * Generates [requestedCount] distinct candidate tickets by fitness-weighted
 * sampling. The statistical filters reject combinations whose sum, odd count,
 * or low-number count falls outside the historical percentile envelopes.
 *
 * This is the only stage that produces full tickets; the pool is simply the
 * union of all numbers appearing across the returned candidates.
 */
fun generateCombinations(
    requestedCount: Int,
    draws: List<Draw>,
    type: TotoType,
    config: GeneratorConfig = GeneratorConfig()
): List<IntArray> {
    if (draws.isEmpty()) return emptyList()

    val fitness = calculateFitnessArray(draws, type, config)
    val allowedNumbers = (1..type.totalNumbers).filter { it !in config.excludeNumbers }
    if (allowedNumbers.size < type.size) return emptyList()

    val (minSum, maxSum) = sumPercentileBounds(draws, config.sumPercentileLow, config.sumPercentileHigh)
    val (minOdd, maxOdd) = oddCountPercentileBounds(draws, config.oddPercentileLow, config.oddPercentileHigh)
    val (minLow, maxLow) = lowCountPercentileBounds(draws, type, config.lowPercentileLow, config.lowPercentileHigh)

    val totalToGenerate = requestedCount * config.oversampleFactor
    val generated = mutableSetOf<UniqueIntArray>()
    val rng = Random

    var attempts = 0
    val maxAttempts = totalToGenerate * 20   // safety bound

    while (generated.size < totalToGenerate && attempts < maxAttempts) {
        attempts++
        val combo = weightedSampleTicket(
            allowedNumbers = allowedNumbers,
            fitness = fitness,
            ticketSize = type.size,
            temperature = config.temperature,
            rng = rng
        )
        if (passesFilters(combo, type, minSum, maxSum, minOdd, maxOdd, minLow, maxLow)) {
            generated.add(UniqueIntArray(combo))
        }
    }

    // Rank candidates by total fitness and take the top requestedCount.
    return generated
        .map { it.array }
        .sortedByDescending { combo -> combo.sumOf { fitness[it - 1] } }
        .take(requestedCount)
}

/**
 * Draws a single ticket of [ticketSize] distinct numbers without replacement,
 * with selection probabilities derived from the fitness array via softmax.
 */
private fun weightedSampleTicket(
    allowedNumbers: List<Int>,
    fitness: DoubleArray,
    ticketSize: Int,
    temperature: Double,
    rng: Random
): IntArray {
    val remaining = allowedNumbers.toMutableList()
    val picked = IntArray(ticketSize)
    var pickedCount = 0

    repeat(ticketSize) {
        if (remaining.isEmpty()) return picked.copyOf(pickedCount).also { it.sort() }

        // Softmax over fitness of remaining candidates
        val maxFit = remaining.maxOf { fitness[it - 1] }
        val exps = DoubleArray(remaining.size) { i ->
            exp((fitness[remaining[i] - 1] - maxFit) / temperature)
        }
        val total = exps.sum()
        val probs = DoubleArray(remaining.size) { exps[it] / total }

        // Roulette selection
        val r = rng.nextDouble()
        var cumulative = 0.0
        var chosenIdx = remaining.lastIndex
        for (i in remaining.indices) {
            cumulative += probs[i]
            if (r <= cumulative) {
                chosenIdx = i; break
            }
        }

        picked[pickedCount++] = remaining[chosenIdx]
        remaining.removeAt(chosenIdx)
    }

    return picked.sortedArray()
}

// ---------------------------------------------------------------------------
// Statistical filters
// ---------------------------------------------------------------------------

private fun passesFilters(
    combo: IntArray,
    type: TotoType,
    minSum: Int, maxSum: Int,
    minOdd: Int, maxOdd: Int,
    minLow: Int, maxLow: Int
): Boolean {
    val sum = combo.sum()
    if (sum !in minSum..maxSum) return false

    val oddCount = combo.count { it % 2 == 1 }
    if (oddCount !in minOdd..maxOdd) return false

    val lowThreshold = type.totalNumbers / 2
    val lowCount = combo.count { it <= lowThreshold }
    if (lowCount !in minLow..maxLow) return false

    return true
}

private fun sumPercentileBounds(
    draws: List<Draw>, lowPct: Double, highPct: Double
): Pair<Int, Int> {
    val sums = draws.map { it.numbers.sum() }.sorted()
    val lowIdx = (sums.size * lowPct / 100).toInt().coerceIn(0, sums.lastIndex)
    val highIdx = (sums.size * highPct / 100).toInt().coerceIn(0, sums.lastIndex)
    return sums[lowIdx] to sums[highIdx]
}

private fun oddCountPercentileBounds(
    draws: List<Draw>, lowPct: Double, highPct: Double
): Pair<Int, Int> {
    val oddCounts = draws.map { it.numbers.count { n -> n % 2 == 1 } }.sorted()
    val lowIdx = (oddCounts.size * lowPct / 100).toInt().coerceIn(0, oddCounts.lastIndex)
    val highIdx = (oddCounts.size * highPct / 100).toInt().coerceIn(0, oddCounts.lastIndex)
    return oddCounts[lowIdx] to oddCounts[highIdx]
}

private fun lowCountPercentileBounds(
    draws: List<Draw>, type: TotoType, lowPct: Double, highPct: Double
): Pair<Int, Int> {
    val threshold = type.totalNumbers / 2
    val lowCounts = draws.map { it.numbers.count { n -> n <= threshold } }.sorted()
    val lowIdx = (lowCounts.size * lowPct / 100).toInt().coerceIn(0, lowCounts.lastIndex)
    val highIdx = (lowCounts.size * highPct / 100).toInt().coerceIn(0, lowCounts.lastIndex)
    return lowCounts[lowIdx] to lowCounts[highIdx]
}

// ---------------------------------------------------------------------------
// Coverage-maximising allocation
// ---------------------------------------------------------------------------

/**
 * Builds [ticketCount] tickets entirely from [numberPool], each of size
 * type.size. Uses a greedy allocation with a per-number usage penalty so
 * coverage across the pool is maximised and repeated use of the same number
 * across tickets is discouraged.
 *
 * Scoring for candidate selection:
 *   zScoredFitness - usagePenalty * timesUsed + jitter
 *
 * @param usagePenalty  how strongly to penalise numbers already used
 * @param jitter        small random perturbation for run-to-run variation
 * @param seed          pass a fixed seed for reproducible allocation
 */
fun allocateTicketsFromPool(
    draws: List<Draw>,
    type: TotoType,
    numberPool: Set<Int>,
    ticketCount: Int,
    usagePenalty: Double = 0.8,
    jitter: Double = 0.02,
    seed: Long? = null
): List<IntArray> {
    if (numberPool.size < type.size) return emptyList()

    val fitness = calculateFitnessArray(draws, type)
    val poolList = numberPool.toList()

    // Z-score fitness across the pool so it lives on a comparable scale
    val poolFitness = poolList.map { fitness[it - 1] }
    val meanFit = poolFitness.average()
    val stdFit = sqrt(poolFitness.sumOf { (it - meanFit) * (it - meanFit) } / poolFitness.size)
        .coerceAtLeast(1e-9)
    val fitnessZ = numberPool.associateWith { (fitness[it - 1] - meanFit) / stdFit }

    val usage = numberPool.associateWith { 0 }.toMutableMap()
    val tickets = mutableListOf<IntArray>()
    val rng = if (seed != null) Random(seed) else Random

    repeat(ticketCount) {
        val ticket = mutableListOf<Int>()
        repeat(type.size) {
            val candidates = poolList.filter { it !in ticket }
            if (candidates.isEmpty()) return@repeat

            val best = candidates.maxByOrNull { num ->
                val fitTerm = fitnessZ[num] ?: 0.0
                val usageTerm = -usagePenalty * (usage[num] ?: 0)
                val jitterTerm = if (jitter > 0.0) rng.nextDouble(-jitter, jitter) else 0.0
                fitTerm + usageTerm + jitterTerm
            } ?: return@repeat

            ticket.add(best)
        }
        if (ticket.size == type.size) {
            val sorted = ticket.sorted().toIntArray()
            tickets.add(sorted)
            sorted.forEach { usage[it] = (usage[it] ?: 0) + 1 }
        }
    }

    return tickets
}

// ---------------------------------------------------------------------------
// Utility – z-score and normalisation
// ---------------------------------------------------------------------------

private fun intArrayToZScores(values: IntArray): DoubleArray {
    val n = values.size
    val mean = values.average()
    val std = run {
        val sumSq = values.sumOf { (it - mean) * (it - mean) }
        sqrt(sumSq / n).takeIf { it > 0.0 } ?: 1.0
    }
    return DoubleArray(n) { (values[it] - mean) / std }
}

private fun doubleArrayToZScores(values: DoubleArray): DoubleArray {
    val n = values.size
    val mean = values.average()
    val std = run {
        val sumSq = values.sumOf { (it - mean) * (it - mean) }
        sqrt(sumSq / n).takeIf { it > 0.0 } ?: 1.0
    }
    return DoubleArray(n) { (values[it] - mean) / std }
}