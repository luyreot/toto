package data

import extensions.clear
import model.TotoNumber
import model.TotoType
import util.Helper.getDrawingScore
import kotlin.math.abs

class TotoDrawingScoreStats(
    private val totoType: TotoType,
    private val totoNumbers: TotoDrawnNumbers,
    private val totoNumberStats: TotoNumberStats,
    private val fromYear: Int? = null
) {

    val averageSore: Int
        get() = score

    private var score: Int = 0

    val averageJump: Int
        get() = jump

    private var jump: Int = 0

    fun calculateTotoDrawingScoreStats() {
        totoNumbers.numbers
            .sortedWith(compareBy<TotoNumber> { it.year }.thenBy { it.issue }.thenBy { it.position })
            .let { sortedTotoNumbers ->
                val currentDrawing = IntArray(totoType.drawingSize)
                var currentDrawingIndex = 0
                var totalScore = 0.0
                var previousScore = 0

                sortedTotoNumbers.forEach { totoNumber ->
                    if (fromYear != null && totoNumber.year < fromYear) {
                        return@forEach
                    }

                    currentDrawing[totoNumber.position] = totoNumber.number

                    if (totoNumber.position == totoType.drawingSize - 1) {
                        currentDrawingIndex += 1

                        currentDrawing.copyOf()

                        getDrawingScore(totoNumberStats.occurrences, currentDrawing).let { score ->
                            totalScore += score

                            if (currentDrawingIndex > 1) {
                                jump = abs(score - previousScore)
                            }

                            previousScore = score
                        }

                        currentDrawing.clear()
                    }
                }

                score = totalScore.div(currentDrawingIndex).toInt()
            }
    }
}