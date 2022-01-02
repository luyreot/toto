package logicNew.data

import logicNew.model.DrawingType

class DrawingsStats(
    drawingType: DrawingType
) {

    val drawnNumbers: DrawnNumbers = DrawnNumbers(drawingType)
    val numberOccurrences: NumberOccurrences = NumberOccurrences(drawingType)

    fun loadNumbers(
        vararg years: Int
    ) {
        drawnNumbers.loadNumbers(*years)
    }

    suspend fun calculateNumberOccurrences() {
        numberOccurrences.calculateNumberOccurrences(drawnNumbers.numbers)
    }
}