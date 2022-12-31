package data

import model.TotoType
import util.TotoUtils.getDrawingScore
import kotlin.math.abs

class DrawingScoreStats(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val numberStats: NumberStats,
    private val fromYear: Int? = null
) {

    val averageSore: Int
        get() = score
    private var score: Int = 0

    val averageJump: Int
        get() = jump
    private var jump: Int = 0

    fun calculateStats() {
        val drawings = if (fromYear == null) drawings.drawings else drawings.drawingsSubset
        var totalScore = 0.0
        var previousScore = 0

        drawings.forEachIndexed { index, numbers ->
            getDrawingScore(
                index,
                numbers.numbers,
                numberStats.patterns,
                numberStats.frequencies,
                numberStats.averageFrequencies,
                drawings
            ).let { score ->
                // Add drawing's score to overall score count
                totalScore += score

                // Calculate the average jump between two consecutive drawings
                if (index > 1) {
                    jump += abs(score - previousScore)
                }

                // Cache the score for next round of calculations
                previousScore = score
            }
        }

        // Calculate the average score and jump between all drawings
        score = totalScore.div(drawings.size).toInt()
        jump = jump.div(drawings.size - 1)
    }
}