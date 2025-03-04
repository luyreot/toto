package systems.gapanalysis

import model.TotoType
import util.Draw

class NumbersPerPosition(
    private val totoType: TotoType,
    val numbers: Array<MutableSet<Int>> = Array(totoType.size) { mutableSetOf() }
) {

    fun setNumbers(
        draws: List<Draw>,
        yearFilter: Int
    ) {
        this.numbers.forEach { if (it.isNotEmpty()) it.clear() }

        val numbers = Array(totoType.size) { mutableMapOf<Int, Int>() }
        numbers.forEach { map -> (1..totoType.totalNumbers).forEach { map[it] = 0 } }

        for (i in draws.indices) {
            if (draws[i].year < yearFilter) continue

            if (totoType == TotoType.T_5X35) {
                for (numberIndex in draws[i].numbers.indices.toList().take(totoType.size)) {
                    numbers[numberIndex].merge(draws[i].numbers[numberIndex], 1, Int::plus)
                }
                for (numberIndex in draws[i].numbers.indices.toList().takeLast(totoType.size)) {
                    numbers[numberIndex - totoType.size].merge(draws[i].numbers[numberIndex], 1, Int::plus)
                }
            } else {
                for (numberIndex in draws[i].numbers.indices) {
                    numbers[numberIndex].merge(draws[i].numbers[numberIndex], 1, Int::plus)
                }
            }
        }

        numbers.forEachIndexed { index, nums ->
            this.numbers[index].addAll(nums.filter { it.value > 1 }.keys)
        }
    }
}