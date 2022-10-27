package util

import model.TotoNumber
import model.TotoType

object Helper {

    fun getDrawingScore(
        totoNumberOccurrences: Map<Int, Int>, drawing: IntArray
    ): Int {
        var score = 0
        drawing.forEach { number ->
            score += totoNumberOccurrences[number] ?: 0
        }
        return score
    }

    fun doesDrawingExists(
        totoType: TotoType,
        sortedTotoNumbers: List<TotoNumber>,
        drawing: IntArray
    ): Boolean {
        val currentDrawing = IntArray(totoType.drawingSize)

        sortedTotoNumbers.forEach { totoNumber ->
            currentDrawing[totoNumber.position] = totoNumber.number
            if (totoNumber.position == totoType.drawingSize - 1) {
                currentDrawing.forEachIndexed { index, number ->
                    if (drawing[index] != number) {
                        return@forEach
                    }
                }
                return true
            }
        }

        return false
    }
}