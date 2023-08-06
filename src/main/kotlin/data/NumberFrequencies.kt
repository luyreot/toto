package data

import model.Drawing
import model.TotoType

/**
 * Track the intervals at which each number has occurred.
 */
class NumberFrequencies(
    private val totoType: TotoType,
    private val drawings: List<Drawing>
) {

    val frequencies: Map<Int, Map<Int, Int>>
        get() = _frequencies
    private val _frequencies = mutableMapOf<Int, MutableMap<Int, Int>>()

    init {
        setNumberFrequencies()
    }

    private fun setNumberFrequencies() {
        val allDrawings: List<IntArray> = drawings
            .map { drawing -> drawing.numbers }

        for (number in 1..totoType.totalNumbers) {
            val indexes = allDrawings.mapIndexed { index, drawing ->
                if (drawing.contains(number)) index else null
            }.filterNotNull()

            _frequencies[number] = mutableMapOf()
            for (i in 1 until indexes.size) {
                val frequency: Int = indexes[i] - indexes[i - 1]
                _frequencies[number]?.merge(frequency, 1, Int::plus)
            }
        }
    }
}