package systems.noname

import model.TotoType
import java.io.File
import kotlin.random.Random

/**
 * Unified generator for:
 * - 6x49 ("649") using OE+LH mixes and reduced-state group constraints + role prefs.
 * - 5x35 ("535") using [1,1,1,1,1] plus rotating [2,1,1,1,0] permutations (and prints rotation label).
 * - 6x42 ("642") using baseline OE/LH mixes and reduced-state group constraints (k=4/5, m<=3).
 *
 * Option 1: neutral random within constraints (no history used).
 */
object LottoUnifiedGenerator {

    // ---------------------------
    // 535 rotation persistence
    // ---------------------------

    private const val ROTATION_FILE_535 = "files/noname/535-rotation-index.txt"
    private const val ROTATION_MOD_535 = 20

    private fun loadRotationIndex535(): Int {
        return try {
            val file = File(ROTATION_FILE_535)
            if (!file.exists()) {
                file.parentFile?.mkdirs()
                file.writeText("0")
                0
            } else {
                file.readText().trim().toInt().coerceIn(0, ROTATION_MOD_535 - 1)
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun saveRotationIndex535(index: Int) {
        try {
            val file = File(ROTATION_FILE_535)
            file.parentFile?.mkdirs()
            file.writeText(index.toString())
        } catch (_: Exception) {
            // Silent failure: generator must still work
        }
    }

    // ---------------------------
    // Public API
    // ---------------------------

    data class Combo(val numbers: List<Int>) {
        init {
            require(numbers.size == numbers.distinct().size) { "Combo must have distinct numbers." }
        }

        override fun toString(): String = numbers.sorted().joinToString(",")
    }

    /** For 535 rotation label reporting */
    data class RotationInfo(
        val doubledGroupIndex: Int,
        val missingGroupIndex: Int,
        val doubledLabel: String,
        val missingLabel: String
    ) {
        override fun toString(): String =
            "Rotation: [2,1,1,1,0] (double $doubledLabel, missing $missingLabel)"
    }

    enum class Game { G649, G535, G642 }

    /**
     * Generate N combinations for a game.
     * - For 649/642: returns only combinations.
     * - For 535: returns combinations plus the rotation label used in this batch.
     */
    fun generateCombos(game: Game, numberCombinations: Int, seed: Long? = null): Pair<List<Combo>, RotationInfo?> {
        require(numberCombinations > 0) { "n must be > 0" }
        val rng = if (seed != null) Random(seed) else Random

        return when (game) {
            Game.G649 -> {
                val combos = mutableListOf<Combo>()
                while (combos.size < numberCombinations) combos += generateTicket649(rng)
                combos.take(numberCombinations) to null
            }

            Game.G642 -> {
                val combos = mutableListOf<Combo>()
                while (combos.size < numberCombinations) combos += generateTicket642(rng)
                combos.take(numberCombinations) to null
            }

            Game.G535 -> {
                val combos = mutableListOf<Combo>()
                var lastRot: RotationInfo? = null
                while (combos.size < numberCombinations) {
                    val (ticket, rot) = generateTicket535(rng)
                    lastRot = rot
                    combos += ticket
                }
                combos.take(numberCombinations) to lastRot
            }
        }
    }

    // Convenience: generate one full ticket (4 combos) for each game
    fun generateTicket649(seed: Long? = null): List<Combo> =
        generateTicket649(if (seed != null) Random(seed) else Random)

    fun generateTicket642(seed: Long? = null): List<Combo> =
        generateTicket642(if (seed != null) Random(seed) else Random)

    fun generateTicket535(seed: Long? = null): Pair<List<Combo>, RotationInfo> =
        generateTicket535(if (seed != null) Random(seed) else Random)

    // ---------------------------
    // Shared utilities
    // ---------------------------

    private data class GroupDef(val index: Int, val range: IntRange, val label: String) {
        fun contains(n: Int): Boolean = n in range
    }

    private data class LHDef(val low: IntRange, val high: IntRange)

    private fun isEven(n: Int) = (n % 2 == 0)
    private fun isHigh(n: Int, lh: LHDef): Boolean = n in lh.high

    private fun groupIndexOf(n: Int, groups: List<GroupDef>): Int =
        groups.first { it.contains(n) }.index

    private data class SlotDomain(val candidates: IntArray, val maxEven: Int, val maxHigh: Int)

    private fun buildSlotDomains(
        groups: List<GroupDef>,
        groupCounts: IntArray,
        used: BooleanArray,
        lh: LHDef
    ): List<SlotDomain> {
        val slots = mutableListOf<SlotDomain>()
        for (g in groups) {
            val cnt = groupCounts[g.index]
            repeat(cnt) {
                val candidates = g.range.filter { !used[it] }.toIntArray()
                val maxEven = candidates.count { isEven(it) }
                val maxHigh = candidates.count { isHigh(it, lh) }
                slots += SlotDomain(candidates, maxEven, maxHigh)
            }
        }
        return slots
    }

    private fun canStillHitTargets(
        remainingSlots: List<SlotDomain>,
        needEven: Int,
        needHigh: Int
    ): Boolean {
        if (needEven < 0 || needHigh < 0) return false
        val maxEvenPossible = remainingSlots.sumOf { it.maxEven }
        val maxHighPossible = remainingSlots.sumOf { it.maxHigh }
        return needEven <= maxEvenPossible && needHigh <= maxHighPossible
    }

    /**
     * Backtracking across slots (each slot domain is a constrained range) to meet even/high targets.
     * This avoids heavy rejection bias and reduces "stuck" cases.
     */
    private fun pickNumbersWithTargets(
        rng: Random,
        slots: List<SlotDomain>,
        used: BooleanArray,
        evenTarget: Int,
        highTarget: Int,
        lh: LHDef
    ): List<Int>? {
        val picked = IntArray(slots.size)

        fun rec(i: Int, needEven: Int, needHigh: Int): Boolean {
            if (i == slots.size) return needEven == 0 && needHigh == 0

            val remaining = slots.subList(i, slots.size)
            if (!canStillHitTargets(remaining, needEven, needHigh)) return false

            val candidates = slots[i].candidates
            val order = candidates.indices.toMutableList().shuffled(rng)

            for (idx in order) {
                val n = candidates[idx]
                if (used[n]) continue

                val ne = needEven - if (isEven(n)) 1 else 0
                val nh = needHigh - if (isHigh(n, lh)) 1 else 0
                if (ne < 0 || nh < 0) continue

                used[n] = true
                picked[i] = n
                if (rec(i + 1, ne, nh)) return true
                used[n] = false
            }
            return false
        }

        return if (rec(0, evenTarget, highTarget)) picked.toList() else null
    }

    // ---------------------------
    // 6x49 ("649")
    // ---------------------------

    private val groups649 = listOf(
        GroupDef(0, 1..9, "G0: 1–9"),
        GroupDef(1, 10..19, "G1: 10–19"),
        GroupDef(2, 20..29, "G2: 20–29"),
        GroupDef(3, 30..39, "G3: 30–39"),
        GroupDef(4, 40..49, "G4: 40–49")
    )
    private val lh649 = LHDef(1..24, 25..49)

    private data class Role649(
        val evenTarget: Int,
        val highTarget: Int,
        val distinctGroups: Int,
        val maxPerGroup: Int,
        val preferDoublesIn: Set<Int>,
        val requireExactDoubles: Set<Int>? = null,
        val forbidOneDouble: Int? = null,
        val allowFallbackDistinctGroups: Int? = null
    )

    private fun generateTicket649(rng: Random): List<Combo> {
        val roles = listOf(
            Role649(3, 3, distinctGroups = 4, maxPerGroup = 2, preferDoublesIn = setOf(3, 4)),
            Role649(3, 3, distinctGroups = 4, maxPerGroup = 2, preferDoublesIn = setOf(3, 4)),
            Role649(
                4,
                4,
                distinctGroups = 4,
                maxPerGroup = 2,
                preferDoublesIn = setOf(3, 4),
                requireExactDoubles = setOf(3, 4)
            ),
            Role649(
                2,
                2,
                distinctGroups = 4,
                maxPerGroup = 2,
                preferDoublesIn = setOf(1, 2),
                allowFallbackDistinctGroups = 3
            )
        )

        val combos = mutableListOf<Combo>()
        var combo1FirstDouble: Int? = null

        for (i in roles.indices) {
            val role = if (i == 1) roles[i].copy(forbidOneDouble = combo1FirstDouble) else roles[i]
            val combo = generateCombo649(rng, role)
            if (i == 0) combo1FirstDouble = findFirstDoubledGroup(combo.numbers, groups649.size, groups649)
            combos += combo
        }
        return combos
    }

    private fun generateCombo649(rng: Random, role: Role649): Combo {
        val kOptions = buildList {
            add(role.distinctGroups)
            role.allowFallbackDistinctGroups?.let { add(it) }
        }

        for (k in kOptions) {
            repeat(20_000) {
                val used = BooleanArray(50) // 1..49
                val groupCounts = sampleGroupCounts649(
                    rng = rng,
                    k = k,
                    maxPerGroup = role.maxPerGroup,
                    preferDoublesIn = role.preferDoublesIn,
                    requireExactDoubles = role.requireExactDoubles,
                    forbidOneDouble = role.forbidOneDouble
                )
                val slots = buildSlotDomains(groups649, groupCounts, used, lh649)
                val picked = pickNumbersWithTargets(rng, slots, used, role.evenTarget, role.highTarget, lh649)
                if (picked != null) return Combo(picked.sorted())
            }
        }
        throw IllegalStateException("Failed to generate valid 649 combo for role=$role")
    }

    /**
     * 649 reduced-state with m<=2:
     * - k=5 => 2+1+1+1+1
     * - k=4 => 2+2+1+1
     * - k=3 => 2+2+2
     */
    private fun sampleGroupCounts649(
        rng: Random,
        k: Int,
        maxPerGroup: Int,
        preferDoublesIn: Set<Int>,
        requireExactDoubles: Set<Int>?,
        forbidOneDouble: Int?
    ): IntArray {
        require(k in 3..5)
        require(maxPerGroup == 2) { "649 assumes maxPerGroup=2." }
        val all = (0 until groups649.size).toList()
        val chosen = all.shuffled(rng).take(k).toMutableList()

        val doublesNeeded = when (k) {
            5 -> 1
            4 -> 2
            3 -> 3
            else -> error("unreachable")
        }

        val doubles: Set<Int> = when {
            requireExactDoubles != null -> {
                if (!chosen.containsAll(requireExactDoubles)) {
                    val forced = requireExactDoubles.toMutableSet()
                    val remaining = (all - forced).shuffled(rng).take(k - forced.size)
                    chosen.clear(); chosen.addAll((forced + remaining).toList())
                }
                if (requireExactDoubles.size != doublesNeeded) {
                    throw IllegalStateException("Invalid role: requireExactDoubles size != doublesNeeded for k=$k")
                }
                requireExactDoubles
            }

            else -> {
                val picked = mutableSetOf<Int>()
                fun pickOne(pool: List<Int>) = pool[rng.nextInt(pool.size)]

                repeat(doublesNeeded) {
                    val pref = chosen.filter { it in preferDoublesIn && it != forbidOneDouble && it !in picked }
                    val any = chosen.filter { it != forbidOneDouble && it !in picked }
                    val pool = if (pref.isNotEmpty()) pref else any
                    picked += pickOne(pool.ifEmpty { chosen.filter { it !in picked } })
                }
                picked
            }
        }

        val counts = IntArray(groups649.size)
        chosen.forEach { counts[it] = 1 }
        doubles.forEach { counts[it] = 2 }
        if (counts.sum() != 6) throw IllegalStateException("Bad 649 groupCounts: ${counts.toList()}")
        return counts
    }

    // ---------------------------
    // 6x42 ("642")
    // ---------------------------

    private val groups642 = listOf(
        GroupDef(0, 1..7, "G0: 1–7"),
        GroupDef(1, 8..14, "G1: 8–14"),
        GroupDef(2, 15..21, "G2: 15–21"),
        GroupDef(3, 22..28, "G3: 22–28"),
        GroupDef(4, 29..35, "G4: 29–35"),
        GroupDef(5, 36..42, "G5: 36–42")
    )
    private val lh642 = LHDef(1..21, 22..42)

    /**
     * 642 baseline-style ticket template (4 combos), analogous to 649:
     * OE mix: 2x(3/3), 1x(2E/4O), 1x(4E/2O)
     * LH mix: 2x(3H/3L), 1x(2H/4L), 1x(4H/2L)
     *
     * Groups: reduced-state typical region:
     * - prefer k in {4,5}
     * - allow m up to 3 (since m=3 is common in 6x42)
     */
    private data class Role642(
        val evenTarget: Int,
        val highTarget: Int,
        val k: Int,             // 4 or 5
        val maxPerGroup: Int,   // 3
        val preferShape: Shape642 // for k=4, prefer 2+2+1+1 vs 3+1+1+1
    )

    private enum class Shape642 { P2211, P3111 } // only used for k=4

    private fun generateTicket642(rng: Random): List<Combo> {
        val roles = listOf(
            Role642(evenTarget = 3, highTarget = 3, k = 4, maxPerGroup = 3, preferShape = Shape642.P2211),
            Role642(evenTarget = 3, highTarget = 3, k = 4, maxPerGroup = 3, preferShape = Shape642.P2211),
            Role642(
                evenTarget = 2,
                highTarget = 2,
                k = 5,
                maxPerGroup = 3,
                preferShape = Shape642.P2211
            ), // diversify with k=5
            Role642(evenTarget = 4, highTarget = 4, k = 4, maxPerGroup = 3, preferShape = Shape642.P2211)
        )

        return roles.map { role -> generateCombo642(rng, role) }
    }

    private fun generateCombo642(rng: Random, role: Role642): Combo {
        repeat(30_000) {
            val used = BooleanArray(43) // 1..42
            val groupCounts = sampleGroupCounts642(rng, role.k, role.maxPerGroup, role.preferShape)
            val slots = buildSlotDomains(groups642, groupCounts, used, lh642)
            val picked = pickNumbersWithTargets(rng, slots, used, role.evenTarget, role.highTarget, lh642)
            if (picked != null) return Combo(picked.sorted())
        }
        throw IllegalStateException("Failed to generate valid 642 combo for role=$role")
    }

    /**
     * 642 reduced-state with k in {4,5} and m<=3:
     * - k=5 => 2+1+1+1+1 (m=2)
     * - k=4 => either 2+2+1+1 (m=2) OR 3+1+1+1 (m=3)
     *
     * We bias toward 2+2+1+1 as the higher-density baseline region.
     */
    private fun sampleGroupCounts642(rng: Random, k: Int, maxPerGroup: Int, preferShape: Shape642): IntArray {
        require(k == 4 || k == 5) { "642 implementation uses k=4 or k=5." }
        require(maxPerGroup == 3) { "642 assumes maxPerGroup=3." }

        val gCount = groups642.size // 6
        val all = (0 until gCount).toList()
        val chosen = all.shuffled(rng).take(k).toMutableList()
        val counts = IntArray(gCount)

        chosen.forEach { counts[it] = 1 }

        if (k == 5) {
            // Add one extra to a random chosen group => 2+1+1+1+1
            val g = chosen[rng.nextInt(chosen.size)]
            counts[g] = 2
        } else {
            // k == 4: choose a shape
            val shape = if (preferShape == Shape642.P2211) {
                // 80% choose 2+2+1+1, 20% choose 3+1+1+1
                if (rng.nextInt(100) < 80) Shape642.P2211 else Shape642.P3111
            } else {
                if (rng.nextInt(100) < 80) Shape642.P3111 else Shape642.P2211
            }

            when (shape) {
                Shape642.P2211 -> {
                    // pick two groups to get +1 each => 2,2,1,1
                    val picks = chosen.shuffled(rng).take(2)
                    picks.forEach { counts[it] = 2 }
                }

                Shape642.P3111 -> {
                    // pick one group to get +2 => 3,1,1,1
                    val g = chosen[rng.nextInt(chosen.size)]
                    counts[g] = 3
                }
            }
        }

        if (counts.sum() != 6) throw IllegalStateException("Bad 642 groupCounts: ${counts.toList()}")
        if (counts.maxOrNull()!! > maxPerGroup) throw IllegalStateException("Bad 642 groupCounts (m>${maxPerGroup}): ${counts.toList()}")
        return counts
    }

    private fun findFirstDoubledGroup(nums: List<Int>, groupCount: Int, groups: List<GroupDef>): Int? {
        val counts = IntArray(groupCount)
        nums.forEach { counts[groupIndexOf(it, groups)]++ }
        return counts.indexOfFirst { it == 2 }.takeIf { it >= 0 }
    }

    // ---------------------------
    // 5x35 ("535")
    // ---------------------------

    private val groups535 = listOf(
        GroupDef(0, 1..7, "G0: 1–7"),
        GroupDef(1, 8..14, "G1: 8–14"),
        GroupDef(2, 15..21, "G2: 15–21"),
        GroupDef(3, 22..28, "G3: 22–28"),
        GroupDef(4, 29..35, "G4: 29–35")
    )
    private val lh535 = LHDef(1..17, 18..35)

    /**
     * Rotation over all permutations of [2,1,1,1,0] for 5 groups:
     * - missing group: 5 options
     * - doubled group: 4 options among remaining => 20 total
     *
     * Note: this persists only within the running process. Persist externally if you want it across restarts.
     */
    private var rotationIndex535: Int = loadRotationIndex535()

    private fun currentRotation535(): RotationInfo {
        val missing = rotationIndex535 / 4
        val doubledOffset = rotationIndex535 % 4
        val available = (0..4).filter { it != missing }
        val doubled = available[doubledOffset]
        return RotationInfo(
            doubledGroupIndex = doubled,
            missingGroupIndex = missing,
            doubledLabel = groups535[doubled].label,
            missingLabel = groups535[missing].label
        )
    }

    private fun advanceRotation535() {
        rotationIndex535 = (rotationIndex535 + 1) % ROTATION_MOD_535
        saveRotationIndex535(rotationIndex535)
    }

    private data class Role535(
        val evenTarget: Int,   // 2 or 3
        val highTarget: Int,   // 2 or 3
        val groupCounts: IntArray
    )

    private fun rotatedSecondaryPattern(rot: RotationInfo): IntArray {
        val counts = intArrayOf(1, 1, 1, 1, 1)
        counts[rot.missingGroupIndex] = 0
        counts[rot.doubledGroupIndex] = 2
        return counts
    }

    private fun feasibleHighRangeForGroupCounts535(groupCounts: IntArray): IntRange {
        // Low = 1..17, High = 18..35
        // Groups:
        // G0 1..7   => lowCap=7, highCap=0
        // G1 8..14  => lowCap=7, highCap=0
        // G2 15..21 => lowCap=3 (15..17), highCap=4 (18..21)
        // G3 22..28 => lowCap=0, highCap=7
        // G4 29..35 => lowCap=0, highCap=7

        val lowCaps = intArrayOf(7, 7, 3, 0, 0)
        val highCaps = intArrayOf(0, 0, 4, 7, 7)

        var minHigh = 0
        var maxHigh = 0

        for (g in 0..4) {
            val cnt = groupCounts[g]
            val lowCap = lowCaps[g]
            val highCap = highCaps[g]

            // If you take more than lowCap from that group, the remainder MUST be high
            val groupMinHigh = maxOf(0, cnt - lowCap)
            // You cannot take more than highCap highs from that group
            val groupMaxHigh = minOf(cnt, highCap)

            minHigh += groupMinHigh
            maxHigh += groupMaxHigh
        }

        return minHigh..maxHigh
    }


    private fun generateTicket535(rng: Random): Pair<List<Combo>, RotationInfo> {
        val rot = currentRotation535()
        advanceRotation535()

        val patterns = listOf(
            intArrayOf(1, 1, 1, 1, 1),
            rotatedSecondaryPattern(rot),
            rotatedSecondaryPattern(rot),
            rotatedSecondaryPattern(rot)
        )

        // OE targets: 2 combos with 3E, 2 combos with 2E (locked primary region)
        val evenTargets = intArrayOf(3, 2, 3, 2)

        // LH preferred targets: want two 3H and two 2H, but must be feasible per groupCounts
        val preferredHighTargets = intArrayOf(3, 2, 2, 3)

        val roles = (0 until 4).map { i ->
            val groupCounts = patterns[i]
            val feasible = feasibleHighRangeForGroupCounts535(groupCounts)

            val preferred = preferredHighTargets[i]
            val chosenHigh = when {
                preferred in feasible -> preferred
                // otherwise pick closest feasible to preferred (ties -> lower)
                else -> {
                    val candidates = feasible.toList()
                    candidates.minWith(compareBy<Int>({ kotlin.math.abs(it - preferred) }, { it }))
                }
            }

            Role535(
                evenTarget = evenTargets[i],
                highTarget = chosenHigh,
                groupCounts = groupCounts
            )
        }

        val combos = roles.map { generateCombo535(rng, it) }
        return combos to rot
    }

    private fun generateCombo535(rng: Random, role: Role535): Combo {
        repeat(20_000) {
            val used = BooleanArray(36) // 1..35
            val slots = buildSlotDomains(groups535, role.groupCounts, used, lh535)
            val picked = pickNumbersWithTargets(rng, slots, used, role.evenTarget, role.highTarget, lh535)
            if (picked != null) return Combo(picked.sorted())
        }
        throw IllegalStateException("Failed to generate valid 535 combo for role=$role")
    }
}

fun generate(totoType: TotoType, numberCombinations: Int, seed: Long? = null) {
    val game = when (totoType) {
        TotoType.T_6X49 -> LottoUnifiedGenerator.Game.G649
        TotoType.T_5X35 -> LottoUnifiedGenerator.Game.G535
        TotoType.T_6X42 -> LottoUnifiedGenerator.Game.G642
    }
    val (combos, rot) = LottoUnifiedGenerator.generateCombos(game, numberCombinations, seed)
    if (rot != null) println(rot.toString())
    combos.forEach { println(it.toString()) }
}