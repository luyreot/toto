package logicNew.data

import logicNew.model.drawing.DrawingType

class DrawingsStats(
    drawingType: DrawingType
) {

    val drawnNumbers: DrawnNumbers = DrawnNumbers(drawingType)
    val occurredNumbers: OccurredNumbers = OccurredNumbers(drawingType)

    fun loadNumbers(
        vararg years: Int
    ) {
        drawnNumbers.loadNumbers(*years)
        drawnNumbers.checkDrawings()
        drawnNumbers.validateNumbers()
    }

    suspend fun calculateNumberOccurrences() {
        occurredNumbers.calculateNumberOccurrences(drawnNumbers.numbers)
    }
}