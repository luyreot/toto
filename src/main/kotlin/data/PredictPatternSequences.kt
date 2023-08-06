package data

import model.Drawing
import model.PatternType
import model.TotoType
import model.UniquePattern

class PredictPatternSequences(
    val totoType: TotoType,
    val drawings: List<Drawing>,
    val patternType: PatternType,
    val sequences: Map<UniquePattern, Int>,
    val sequenceSize: Int,
    val takeRatio: Float
) {

    val predictionPatterns: Set<UniquePattern>
        get() = _predictionPatterns
    private val _predictionPatterns = mutableSetOf<UniquePattern>()

    init {
        predict()
    }

    private fun predict() {
        // Filter the sequences based on how often each of them has occurred.
        // Take the lowest and highest occurrences and using the ratio calculate the split point.
        val lowestOccurrence: Int = sequences.values.minOf { it }
        val highestOccurrence: Int = sequences.values.maxOf { it }
        val splitPoint: Int = (highestOccurrence - ((highestOccurrence - lowestOccurrence) * takeRatio)).toInt()
        val filteredSequences = sequences.filter { it.value > splitPoint }

        // Get the last patterns and store them in an array of arrays.
        val predictionArraySize = sequenceSize - 1
        val predictionArray = Array(predictionArraySize) { intArrayOf() }
        for (i in 1 until sequenceSize) {
            predictionArray[i - 1] = getPredictionArray(i)
        }
        // Reverse the array so the patterns have the same order as they have been drawn.
        predictionArray.reverse()

        // Filter the already created/filtered sequences and take only the ones that have the same numbers as the ones in the newly generated sequences.
        // This creates a map of sequences that contain the next possible patterns to be drawn.
        // The next possible patterns are always in the last index of the array.
        val takeSize = totoType.size * predictionArraySize
        val reducedPredictionArray = predictionArray.reduce { acc, arr -> acc.plus(arr) }
        val filteredPredictionSequences = filteredSequences.filter { entry ->
            val array = entry.key.array
            reducedPredictionArray.contentEquals(array.take(takeSize).toIntArray())
        }

        // Extract the next patterns to a set.
        _predictionPatterns.addAll(filteredPredictionSequences.keys.map { UniquePattern(it.array.takeLast(totoType.size).toIntArray()) })
    }

    /**
     * Return a drawings to be used in the prediction algorithm.
     * Usually the returned drawings will be the last ones in the database.
     * When testing we would skip the last N number of drawings simulating a previous state of the database.
     */
    private fun getPredictionArray(i: Int): IntArray = when (patternType) {
        PatternType.GROUP -> drawings[drawings.size - i].groupPattern

        PatternType.LOW_HIGH -> drawings[drawings.size - i].lowHighPattern

        PatternType.ODD_EVEN -> drawings[drawings.size - i].oddEvenPattern
    }

    /**
     * Only for testing a previous state of the database.
     * Print how many of the predicted patterns actually did occur in the next drawings.
     */
    fun printPredictionResults() {
        val nextPattern: IntArray = when (patternType) {
            PatternType.GROUP -> drawings.last().groupPattern
            PatternType.LOW_HIGH -> drawings.last().lowHighPattern
            PatternType.ODD_EVEN -> drawings.last().oddEvenPattern
        }
        val didPredictionsContainedNextPattern = predictionPatterns.any {
            it.array.contentEquals(nextPattern)
        }

        println("Predicted $patternType pattern size - ${predictionPatterns.size}")

        println("Did predictions for $patternType pattern contained next pattern - $didPredictionsContainedNextPattern")
        println("-------")
    }
}