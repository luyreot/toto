package data

import model.Drawing
import model.TotoType

/**
 * Create a two-dimensional matrix to store the number of occurrences of two numbers.
 * Calculate the [midpoint] by summing all non-null occurrences and dividing by their count.
 */
class CooccurrenceMatrix(
    private val totoType: TotoType,
    private val drawings: List<Drawing>
) {

    /**
     * Note that the 0 position in both dimension is left empty.
     * This is done in order to set/get the occurrences more easily.
     */
    val matrix = Array(totoType.totalNumbers + 1) { IntArray(totoType.totalNumbers + 1) }

    val midpoint: Int
        get() = _midpoint
    private var _midpoint: Int = 0

    init {
        populateMatrix()
        calculateMedian()
    }

    private fun populateMatrix() {
        drawings
            .map { it.numbers }
            .forEach { drawing ->
                for (row in 0 until drawing.size - 1) {
                    for (column in row + 1 until drawing.size) {
                        val numberRow = drawing[row]
                        val numberColumn = drawing[column]
                        matrix[numberRow][numberColumn]++
                        matrix[numberColumn][numberRow]++
                    }
                }

            }
    }

    private fun calculateMedian() {
        var count = 0
        var sum = 0
        for (row in 1 until totoType.totalNumbers) {
            for (column in row + 1..totoType.totalNumbers) {
                val value = matrix[row][column]
                if (value == 0) continue
                sum += matrix[row][column]
                count++
            }
        }
        _midpoint = sum / count
    }
}