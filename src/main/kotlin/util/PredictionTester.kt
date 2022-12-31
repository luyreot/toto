package util

object PredictionTester {

    var isTestingPredictions: Boolean = false
    var startFromYear: Int? = null
    var startFromIssue: Int? = null
    var nextDrawing: IntArray? = null

    fun checkPredictions(predictions: MutableMap<IntArray, Int>) {
        var threes = 0
        var fours = 0
        var fives = 0
        var sixes = 0

        predictions.keys.forEach { prediction ->
            prediction.intersect(nextDrawing?.toSet()!!).let { result ->
                when (result.size) {
                    6 -> sixes++
                    5 -> fives++
                    4 -> fours++
                    3 -> threes++
                    else -> {}
                }
            }
        }

        println("$threes threes")
        println("$fours fours")
        println("$fives fives")
        println("$sixes sixes")
    }
}