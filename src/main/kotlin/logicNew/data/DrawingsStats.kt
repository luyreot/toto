package logicNew.data

import logicNew.model.drawing.DrawingType

class DrawingsStats(
    drawingType: DrawingType
) {

    val drawnNumbers: DrawnNumbers = DrawnNumbers(drawingType)

    fun loadNumbers(
        vararg years: Int
    ) {
        drawnNumbers.loadNumbers(*years)
        drawnNumbers.checkDrawings()
        drawnNumbers.validateNumbers()
    }
}