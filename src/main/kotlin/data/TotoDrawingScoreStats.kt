package data

import model.TotoType
import util.TotoUtils.getDrawingScore
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
        val drawings = if (fromYear == null) totoNumbers.allDrawings else totoNumbers.drawingsSubset
        var totalScore = 0.0
        var previousScore = 0

        drawings.forEachIndexed { index, totoNumbers ->
            getDrawingScore(
                index,
                totoNumbers.numbers,
                totoNumberStats.occurrences,
                totoNumberStats.frequencies,
                totoNumberStats.averageFrequencies,
                drawings
            ).let { score ->
                // Add drawing's score to overall score count
                totalScore += score

                // Calculate the average jump between two consecutive drawings
                if (index > 1) {
                    jump = abs(score - previousScore)
                }

                // Cache the score for next round of calculations
                previousScore = score
            }
        }

        // Calculate the average score between all drawings
        score = totalScore.div(drawings.size).toInt()
    }
}