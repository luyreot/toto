package util

object GlobalConfig {

    var fetchNewDrawings: Boolean = false

    // 5x45, 6x42, 6x49
    val performAlgoForType: Array<Boolean> = arrayOf(false, false, true)

    var savePredictionsToFile: Boolean = false

    var checkPredictionScore: Boolean = false

    object PredictionScoreTester {
        var startYear: Int = 0
        var startIssue: Int = 0
        var counter: Int = 0
        var drawing: IntArray? = null
    }
}