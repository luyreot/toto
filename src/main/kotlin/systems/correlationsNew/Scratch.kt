package systems.correlationsNew

import model.Draw
import model.TotoType
import model.UniqueIntArray
import model.loadDrawings
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

fun dodo(totoType: TotoType, yearFilter: Int, predictionsSize: Int) {
    val draws = loadDrawings(totoType)
        .filter { it.year >= yearFilter }

    val tickets = generateCombinations(
        requestedCount = predictionsSize,
        draws = draws,
        type = totoType,
        config = GeneratorConfig() // use defaults or adjust
    )

    tickets.forEachIndexed { index, ticket ->
        println("Ticket ${index + 1}: ${ticket.joinToString()}")
    }
}

/**
 * Returns a 2D array where:
 * - rows = drawing positions (0 to size-1)
 * - columns = numbers (index = number - 1, covering 1..totalNumbers)
 * - value = how many times that number appeared in that position.
 * I.e. How many times has number 42 appeared as the sixth number - [5][42-1]
 */
fun calculatePositionFrequencies(
    draws: List<Draw>,
    type: TotoType
): Array<IntArray> {
    // Create a [size] x [totalNumbers] matrix filled with 0
    val frequencies = Array(type.size) { IntArray(type.totalNumbers) }

    for (draw in draws) {
        // Make sure we only process draws that match the expected size
        if (draw.numbers.size != type.size) continue

        for ((position, number) in draw.numbers.withIndex()) {
            // Validate number range (optional safety)
            if (number in 1..type.totalNumbers) {
                frequencies[position][number - 1]++
            }
        }
    }
    return frequencies
}

/**
 * Returns a DoubleArray of size [TotoType.totalNumbers] where:
 * - index i corresponds to number (i + 1)
 * - value = average number of times that number was drawn per year.
 *
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
            if (num in 1..type.totalNumbers) {
                totalCounts[num - 1]++
            }
        }
    }

    val yearCount = distinctYears.size
    if (yearCount == 0) {
        return DoubleArray(type.totalNumbers) // all zeros
    }

    return DoubleArray(type.totalNumbers) { index ->
        totalCounts[index].toDouble() / yearCount
    }
}

/**
 * Returns an IntArray of size [TotoType.totalNumbers] where:
 * - index i corresponds to number (i + 1)
 * - value = total occurrences of that number in draws from the maximum year.
 * If there are no draws, returns an array of zeros.
 */
fun calculateCurrentYearOccurrences(
    draws: List<Draw>,
    type: TotoType
): IntArray {
    // Find the highest year (current year) in the data
    val currentYear = draws.maxOfOrNull { it.year } ?: return IntArray(type.totalNumbers)

    val counts = IntArray(type.totalNumbers)
    for (draw in draws) {
        if (draw.year == currentYear) {
            for (num in draw.numbers) {
                if (num in 1..type.totalNumbers) {
                    counts[num - 1]++
                }
            }
        }
    }
    return counts
}

/**
 * Creates a symmetric co‑occurrence matrix from a list of draws.
 * matrix[a][b] = how many times numbers a+1 and b+1 appeared together.
 */
fun buildUniformCoOccurrenceMatrix(
    draws: List<Draw>,
    type: TotoType
): Array<IntArray> {
    val n = type.totalNumbers
    val matrix = Array(n) { IntArray(n) }

    for (draw in draws) {
        val nums = draw.numbers
        val k = nums.size
        for (i in 0 until k) {
            for (j in i + 1 until k) {
                val a = nums[i] - 1
                val b = nums[j] - 1
                matrix[a][b]++
                matrix[b][a]++
            }
        }
    }
    return matrix
}

/**
 * Weighted co‑occurrence matrix with exponential time decay.
 *
 * @param draws all draws (should be filtered if you want a time window).
 * @param type lotto type.
 * @param halfLifeYears after this many years a draw’s weight halves.
 * @param currentYear the “present” year for age calculation.
 * @return symmetric matrix of Double (weights sum, not counts).
 */
fun buildWeightedCoOccurrenceMatrix(
    draws: List<Draw>,
    type: TotoType,
    halfLifeYears: Double,
    currentYear: Int
): Array<DoubleArray> {
    val n = type.totalNumbers
    val matrix = Array(n) { DoubleArray(n) }
    val decay = ln(2.0) / halfLifeYears   // λ such that exp(-λ * halfLife) = 0.5

    for (draw in draws) {
        val age = (currentYear - draw.year).toDouble().coerceAtLeast(0.0)
        val weight = exp(-decay * age)
        val nums = draw.numbers
        val k = nums.size
        for (i in 0 until k) {
            for (j in i + 1 until k) {
                val a = nums[i] - 1
                val b = nums[j] - 1
                matrix[a][b] += weight
                matrix[b][a] += weight
            }
        }
    }
    return matrix
}

/**
 * Blends two matrices (must be same size) element‑wise:
 * blended[a][b] = alpha * matrix1[a][b] + (1 - alpha) * matrix2[a][b].
 *
 * Use this to combine, e.g., a 10‑year uniform matrix and a 2‑year weighted matrix.
 */
fun blendMatrices(
    matrix1: Array<DoubleArray>,
    matrix2: Array<DoubleArray>,
    alpha: Double
): Array<DoubleArray> {
    require(matrix1.size == matrix2.size)
    val n = matrix1.size
    return Array(n) { r ->
        DoubleArray(n) { c ->
            alpha * matrix1[r][c] + (1 - alpha) * matrix2[r][c]
        }
    }
}

/**
 * Converts an integer co‑occurrence matrix to Double for blending.
 */
fun intArrayMatrixToDouble(matrix: Array<IntArray>): Array<DoubleArray> {
    return Array(matrix.size) { r -> DoubleArray(matrix[r].size) { c -> matrix[r][c].toDouble() } }
}

/**
 * Returns the top‑k numbers that most frequently appear together with the given number.
 *
 * @param number the lotto number (1‑based).
 * @param matrix symmetric Double matrix (e.g., blended).
 * @param k how many partners to return.
 * @return list of pairs (partnerNumber, weight) sorted descending.
 */
fun topPartners(
    number: Int,
    matrix: Array<DoubleArray>,
    k: Int
): List<Pair<Int, Double>> {
    val idx = number - 1
    val row = matrix[idx]
    // Create a list of (partner, weight), exclude the number itself
    return row.indices
        .filter { it != idx }
        .map { Pair(it + 1, row[it]) }
        .sortedByDescending { it.second }
        .take(k)
}

/**
 * Returns an IntArray of size totalNumbers where value = number of draws in the
 * specified year interval [minYear, maxYear] in which the number appeared.
 */
fun frequencyInYearWindow(
    draws: List<Draw>,
    type: TotoType,
    minYear: Int,
    maxYear: Int
): IntArray {
    val counts = IntArray(type.totalNumbers)
    for (draw in draws) {
        if (draw.year in minYear..maxYear) {
            for (num in draw.numbers) {
                if (num in 1..type.totalNumbers) {
                    counts[num - 1]++
                }
            }
        }
    }
    return counts
}

/**
 * Converts raw counts to z‑scores (standard deviations from the mean).
 * Uses population standard deviation for simplicity.
 */
fun toZScores(values: IntArray): DoubleArray {
    val n = values.size
    val mean = values.average()
    val std = run {
        val sumSq = values.sumOf { (it - mean) * (it - mean) }
        kotlin.math.sqrt(sumSq / n).takeIf { it > 0.0 } ?: 1.0   // avoid division by 0
    }
    return DoubleArray(n) { i -> (values[i] - mean) / std }
}

/**
 * Weighted frequency per number using exponential time decay.
 * halfLifeYears: after this many years a draw's weight halves.
 * currentYear: the present year (used to compute age).
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
            if (num in 1..type.totalNumbers) {
                weights[num - 1] += weight
            }
        }
    }
    return weights
}

/**
 * Returns an IntArray where index = number-1 and value = number of draws
 * that have occurred since that number last appeared (current draw is “in the future”).
 * If a number has never appeared, the value equals totalDraws (maximum).
 */
fun drawsSinceLastAppearance(
    draws: List<Draw>,
    type: TotoType
): IntArray {
    // Sort draws chronologically – assume (year, id) unique and id increments over time
    val sorted = draws.sortedWith(compareBy<Draw> { it.year }.thenBy { it.id })
    val lastSeen = IntArray(type.totalNumbers) { -1 }  // draw index of last appearance
    for ((idx, draw) in sorted.withIndex()) {
        for (num in draw.numbers) {
            if (num in 1..type.totalNumbers) {
                lastSeen[num - 1] = idx
            }
        }
    }

    val total = sorted.size
    return IntArray(type.totalNumbers) { i ->
        if (lastSeen[i] == -1) total else total - 1 - lastSeen[i]
    }
}

/**
 * Combines multiple components into a single per‑number fitness score.
 *
 * @param zLongTerm   z‑scores from a long‑term window (e.g., 10 years)
 * @param zRecent     z‑scores from a recent window (e.g., 2 years, uniform or weighted)
 * @param recencyRaw  raw “draws since last appearance” values
 * @param totalDraws  total number of draws used (for normalising recency)
 * @param weights     double[3] = { wLongTerm, wRecent, wRecency }
 * @return fitness array, higher = better candidate.
 */
fun aggregateFitness(
    zLongTerm: DoubleArray,
    zRecent: DoubleArray,
    recencyRaw: IntArray,
    totalDraws: Int,
    weights: DoubleArray   // size 3
): DoubleArray {
    val n = zLongTerm.size
    val maxRecency = totalDraws.toDouble().coerceAtLeast(1.0)
    // Convert recency to a 0–1 "due" factor where 1 = never appeared
    val recencyScore = DoubleArray(n) { i -> recencyRaw[i] / maxRecency }

    return DoubleArray(n) { i ->
        weights[0] * zLongTerm[i] +
                weights[1] * zRecent[i] +
                weights[2] * recencyScore[i]
    }
}

data class GeneratorConfig(
    // Weights for the four fitness components
    val wLongTermAvg: Double = 0.25,
    val wCurrentYear: Double = 0.20,
    val wRecentWeighted: Double = 0.30,
    val wRecency: Double = 0.25,
    // Weights in candidate scoring during combination building
    val weightFitness: Double = 0.50,
    val weightPosition: Double = 0.25,
    val weightPairBonus: Double = 0.25,   // from topPartners
    // Temperature for softmax selection
    val temperature: Double = 2.0,
    // Pair top‑partner parameters
    val topPartnerCount: Int = 10,        // how many top partners to pre‑compute per number
    // Filter percentiles
    val sumPercentileLow: Double = 5.0,
    val sumPercentileHigh: Double = 95.0,
    val oddPercentileLow: Double = 10.0,
    val oddPercentileHigh: Double = 90.0,
    val lowPercentileLow: Double = 10.0,
    val lowPercentileHigh: Double = 90.0,
    // Oversampling factor
    val oversampleFactor: Int = 5
)

// ---------------------------------------------------------------------------
// Main generation function (revised)
// ---------------------------------------------------------------------------
fun generateCombinations(
    requestedCount: Int,
    draws: List<Draw>,
    type: TotoType,
    config: GeneratorConfig = GeneratorConfig()
): List<IntArray> {
    if (draws.isEmpty()) return emptyList()

    val currentYear = draws.maxOf { it.year }
    val totalDrawsAll = draws.size

    // ---- 1. Per‑number fitness components ----
    // a) Long‑term average per year (over the whole dataset, or you can filter)
    val avgPerYearAll = calculateAverageOccurrencePerYear(draws, type) // already returns DoubleArray
    val zLongAvg = doubleArrayToZScores(avgPerYearAll)

    // b) Current year hotness
    val currentYearCounts = calculateCurrentYearOccurrences(draws, type)
    val zCurrentYear = intArrayToZScores(currentYearCounts)

    // c) Recent weighted trend (last 2 years, half‑life 1 year)
    val recentDraws = draws.filter { it.year >= currentYear - 1 } // 2 calendar years
    val weightedRecent = weightedFrequency(recentDraws, type, halfLifeYears = 1.0, currentYear)
    val zRecentWeighted = doubleArrayToZScores(weightedRecent)   // z‑score the weighted values

    // d) Recency (draws since last appearance)
    val recency = drawsSinceLastAppearance(draws, type)
    val recencyScore = DoubleArray(type.totalNumbers) { i ->
        recency[i].toDouble() / totalDrawsAll.toDouble().coerceAtLeast(1.0)
    }

    // Combine into single fitness array (higher = better)
    val fitness = DoubleArray(type.totalNumbers) { i ->
        config.wLongTermAvg * zLongAvg[i] +
                config.wCurrentYear * zCurrentYear[i] +
                config.wRecentWeighted * zRecentWeighted[i] +
                config.wRecency * recencyScore[i]
    }

    // ---- 2. Position probabilities ----
    val posFreq = calculatePositionFrequencies(draws, type)
    val positionProb = normalizePositionFrequencies(posFreq)

    // ---- 3. Pair matrix & pre‑compute top partners for every number ----
    val draws10y = draws.filter { it.year >= currentYear - 9 }
    val uniform10y = buildUniformCoOccurrenceMatrix(draws10y, type)
    val draws2y = draws.filter { it.year >= currentYear - 1 }
    val weighted2y = buildWeightedCoOccurrenceMatrix(draws2y, type, halfLifeYears = 1.0, currentYear)
    val pairMatrix = blendMatrices(
        intArrayMatrixToDouble(uniform10y),
        weighted2y,
        alpha = 0.5
    )

    // Pre‑compute top partners for every number from the blended matrix
    val topPartnersCache: Map<Int, List<Int>> = (1..type.totalNumbers).associateWith { number ->
        topPartners(number, pairMatrix, config.topPartnerCount).map { it.first }
    }

    // ---- 4. Filters (sum, odd, low counts) ----
    val (minSum, maxSum) = sumPercentileBounds(draws, type, config.sumPercentileLow, config.sumPercentileHigh)
    val (minOdd, maxOdd) = oddCountPercentileBounds(draws, type, config.oddPercentileLow, config.oddPercentileHigh)
    val (minLow, maxLow) = lowCountPercentileBounds(draws, type, config.lowPercentileLow, config.lowPercentileHigh)

    // Convert fitness to positive sampling weights
    val baseWeight = normalizeFitnessToPositiveWeights(fitness)

    // ---- 5. Generate combinations ----
    val totalToGenerate = requestedCount * config.oversampleFactor
    val generated = mutableSetOf<UniqueIntArray>()
    val rng = Random

    while (generated.size < totalToGenerate) {
        val combo = generateOneCombination(
            type, baseWeight, positionProb, pairMatrix, topPartnersCache,
            config.weightFitness, config.weightPosition, config.weightPairBonus,
            config.temperature, rng
        )
        if (passesFilters(combo, type, minSum, maxSum, minOdd, maxOdd, minLow, maxLow)) {
            generated.add(UniqueIntArray(combo))
        }
        // Safety break to avoid infinite loop
        if (generated.isEmpty() && generated.size >= totalToGenerate * 0.5) break
    }

    // ---- 6. Score and rank final combinations ----
    val scored = generated.map { combo ->
        combo to scoreCombination(combo.array, fitness, pairMatrix, topPartnersCache)
    }.sortedByDescending { it.second }

    return scored.take(requestedCount).map { it.first.array }
}

// ---------------------------------------------------------------------------
// Generate one combination with top‑partner bonus
// ---------------------------------------------------------------------------
private fun generateOneCombination(
    type: TotoType,
    baseWeight: DoubleArray,
    positionProb: Array<DoubleArray>,
    pairMatrix: Array<DoubleArray>,
    topPartnersCache: Map<Int, List<Int>>,
    wFitness: Double,
    wPosition: Double,
    wPairBonus: Double,
    temperature: Double,
    rng: Random
): IntArray {
    val nTotal = type.totalNumbers
    val size = type.size
    val picked = mutableListOf<Int>()

    for (pos in 0 until size) {
        val last = picked.lastOrNull() ?: 0
        val minNum = last + 1
        val maxNum = nTotal - (size - pos - 1)
        val candidates = (minNum..maxNum).filter { it !in picked }
        if (candidates.isEmpty()) break

        val scores = DoubleArray(candidates.size) { idx ->
            val n = candidates[idx]
            val idxN = n - 1

            val fitScore = baseWeight[idxN]
            val posScore = positionProb[pos][idxN]

            // Pair bonus: how many already picked numbers list 'n' as a top partner
            val partnerBonus = if (picked.isEmpty()) 0.0 else {
                var count = 0.0
                for (p in picked) {
                    if (n in (topPartnersCache[p] ?: emptyList())) count++
                }
                count / picked.size   // normalise to 0–1
            }

            wFitness * fitScore + wPosition * posScore + wPairBonus * partnerBonus
        }

        // Softmax selection
        val maxScore = scores.maxOrNull() ?: 0.0
        val expScores = DoubleArray(scores.size) { exp((scores[it] - maxScore) / temperature) }
        val sumExp = expScores.sum()
        val probs = expScores.map { it / sumExp }

        val chosen = rouletteSelect(candidates, probs, rng)
        picked.add(chosen)
    }

    return picked.sorted().toIntArray()
}

// ---------------------------------------------------------------------------
// Scoring a full combination (uses top‑partner consistency too)
// ---------------------------------------------------------------------------
private fun scoreCombination(
    combo: IntArray,
    fitness: DoubleArray,
    pairMatrix: Array<DoubleArray>,
    topPartnersCache: Map<Int, List<Int>>
): Double {
    var fitnessSum = 0.0
    for (num in combo) fitnessSum += fitness[num - 1]

    var partnerConsistency = 0.0
    var pairCount = 0
    for (i in combo.indices) {
        for (j in i + 1 until combo.size) {
            pairCount++
            val a = combo[i]
            val b = combo[j]
            // 1 if b is a top partner of a AND a is a top partner of b
            if (b in (topPartnersCache[a] ?: emptyList()) && a in (topPartnersCache[b] ?: emptyList())) {
                partnerConsistency++
            }
        }
    }
    val pairScore = if (pairCount > 0) partnerConsistency / pairCount else 0.0

    return fitnessSum + pairScore * combo.size // scale by size so pair consistency matters
}

// ---------------------------------------------------------------------------
// Filters (unchanged)
// ---------------------------------------------------------------------------
private fun passesFilters(
    combo: IntArray, type: TotoType,
    minSum: Int, maxSum: Int, minOdd: Int, maxOdd: Int, minLow: Int, maxLow: Int
): Boolean {
    val sum = combo.sum()
    val oddCount = combo.count { it % 2 == 1 }
    val lowThreshold = type.totalNumbers / 2
    val lowCount = combo.count { it <= lowThreshold }
    return sum in minSum..maxSum && oddCount in minOdd..maxOdd && lowCount in minLow..maxLow
}

// ---------------------------------------------------------------------------
// Statistical bounds (unchanged)
// ---------------------------------------------------------------------------
private fun sumPercentileBounds(draws: List<Draw>, type: TotoType, lowPct: Double, highPct: Double): Pair<Int, Int> {
    val sums = draws.map { it.numbers.sum() }.sorted()
    val lowIdx = (sums.size * lowPct / 100).toInt().coerceIn(0, sums.lastIndex)
    val highIdx = (sums.size * highPct / 100).toInt().coerceIn(0, sums.lastIndex)
    return sums[lowIdx] to sums[highIdx]
}

private fun oddCountPercentileBounds(
    draws: List<Draw>,
    type: TotoType,
    lowPct: Double,
    highPct: Double
): Pair<Int, Int> {
    val oddCounts = draws.map { it.numbers.count { n -> n % 2 == 1 } }.sorted()
    val lowIdx = (oddCounts.size * lowPct / 100).toInt().coerceIn(0, oddCounts.lastIndex)
    val highIdx = (oddCounts.size * highPct / 100).toInt().coerceIn(0, oddCounts.lastIndex)
    return oddCounts[lowIdx] to oddCounts[highIdx]
}

private fun lowCountPercentileBounds(
    draws: List<Draw>,
    type: TotoType,
    lowPct: Double,
    highPct: Double
): Pair<Int, Int> {
    val threshold = type.totalNumbers / 2
    val lowCounts = draws.map { it.numbers.count { n -> n <= threshold } }.sorted()
    val lowIdx = (lowCounts.size * lowPct / 100).toInt().coerceIn(0, lowCounts.lastIndex)
    val highIdx = (lowCounts.size * highPct / 100).toInt().coerceIn(0, lowCounts.lastIndex)
    return lowCounts[lowIdx] to lowCounts[highIdx]
}

// ---------------------------------------------------------------------------
// Utility: z‑score conversion for IntArray / DoubleArray
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

// ---------------------------------------------------------------------------
// Utility: normalize position frequencies and fitness to positive weights
// ---------------------------------------------------------------------------
private fun normalizePositionFrequencies(freq: Array<IntArray>): Array<DoubleArray> {
    return Array(freq.size) { pos ->
        val total = freq[pos].sum()
        if (total > 0) {
            DoubleArray(freq[pos].size) { freq[pos][it].toDouble() / total }
        } else {
            DoubleArray(freq[pos].size) { 1.0 / freq[pos].size }
        }
    }
}

private fun normalizeFitnessToPositiveWeights(fitness: DoubleArray): DoubleArray {
    val minVal = fitness.minOrNull() ?: 0.0
    val shifted = DoubleArray(fitness.size) { fitness[it] - minVal + 0.01 }
    val total = shifted.sum()
    return if (total > 0) DoubleArray(shifted.size) { shifted[it] / total } else {
        DoubleArray(shifted.size) { 1.0 / shifted.size }
    }
}

// ---------------------------------------------------------------------------
// Roulette selection
// ---------------------------------------------------------------------------
private fun rouletteSelect(candidates: List<Int>, probs: List<Double>, rng: Random): Int {
    val p = rng.nextDouble()
    var cumulative = 0.0
    for (i in candidates.indices) {
        cumulative += probs[i]
        if (p <= cumulative) return candidates[i]
    }
    return candidates.last()
}