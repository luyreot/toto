package util

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
}