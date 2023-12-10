package data

import model.Drawing
import model.TotoType

/**
 * Track how often each number has been drawn.
 */
class NumberTable(
    private val totoType: TotoType,
    private val drawings: List<Drawing>
) {

    val numbers: Map<Int, Int>
        get() = _numbers
    private val _numbers = mutableMapOf<Int, Int>()

    init {
        setNumberOccurrences()
    }

    private fun setNumberOccurrences() {
        val allDrawnNumbers: List<Int> = drawings
            .map { drawing -> drawing.numbers }
            .flatMap { numbers -> numbers.map { number -> number } }

        for (i in 1..totoType.totalNumbers) {
            _numbers[i] = allDrawnNumbers.count { number -> number == i }
        }
    }

    fun getTopNumbers(ratio: Float): Map<Int, Int> {
        val min: Int = _numbers.values.minOf { it }
        val max: Int = _numbers.values.maxOf { it }
        val splitPoint: Int = (max - ((max - min) * ratio)).toInt()

        return _numbers.filter { it.value > splitPoint }
    }
}