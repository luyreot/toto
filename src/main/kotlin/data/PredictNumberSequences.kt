package data

import model.Drawing
import model.TotoType
import model.UniquePattern

class PredictNumberSequences(
    private val totoType: TotoType,
    private val drawings: List<Drawing>,
    private val numberSequences: NumberSequences,
    private val takeRatio: Float
) {

    val predictionNumbers: Set<Int>
        get() = _predictionNumbers
    private val _predictionNumbers = mutableSetOf<Int>()

    init {
        predict()
    }

    private fun predict() {
        val sequenceSize: Int = numberSequences.sequenceSize
        val sequences: Map<UniquePattern, Int> = numberSequences.numberSequences

        // Filter the sequences based on how often each of them has occurred.
        // Take the lowest and highest occurrences and using the ratio calculate the split point.
        val lowestOccurrence: Int = sequences.values.minOf { it }
        val highestOccurrence: Int = sequences.values.maxOf { it }
        val splitPoint: Int = (highestOccurrence - ((highestOccurrence - lowestOccurrence) * takeRatio)).toInt()
        val filteredSequences = sequences.filter { it.value > splitPoint }

        // Get the last drawings and store them in an array of arrays.
        val predictionArraySize = sequenceSize - 1
        val predictionArray = Array(predictionArraySize) { intArrayOf() }
        for (i in 1 until sequenceSize) {
            predictionArray[i - 1] = drawings[drawings.size - i].numbers
        }
        // Reverse the array so the drawings have the same order as they have been drawn.
        predictionArray.reverse()

        // Generate all possible sequences for the selected drawings.
        val predictionSequences = numberSequences.generateSequences(predictionArray)
        // Filter the already created/filtered sequences and take only the ones that have the same numbers as the ones in the newly generated sequences.
        // This creates a map of sequences that contain the next possible numbers to be drawn.
        // The next possible numbers are always in the last index of the array.
        val filteredPredictionSequences = filteredSequences.filter { entry ->
            val array = entry.key.array
            predictionSequences.any { list ->
                list.take(list.size) == array.take(list.size).toList()
            }
        }

        // Extract the next numbers to a set.
        _predictionNumbers.addAll(filteredPredictionSequences.keys.map { it.array[it.array.size - 1] })
    }

    fun printPredictionResults(
        nextDrawing: Drawing,
        predictedNumbers: Set<Int>,
        totalNumbers: Int
    ): Int {
        println("-- PREDICTION --")

        println("Next Drawing - ${nextDrawing.getNumbersAsString()}")
        println("Predicted Numbers Size - ${predictedNumbers.size}")
        if (predictedNumbers.size == totalNumbers) println("# ALL NUMBERS!!!")

        val result = nextDrawing.numbers.count { num ->
            num in predictedNumbers
        }
        println("Result - $result")
        println("-- --")

        return result
    }
}