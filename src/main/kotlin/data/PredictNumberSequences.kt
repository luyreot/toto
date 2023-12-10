package data

import model.Drawing
import model.UniquePattern

class PredictNumberSequences(
    private val drawings: List<Drawing>,
    private val numberSequences: SubsequentDrawingNumberCombinations,
    private val takeSize: Int
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
            .toList()
            .sortedBy { (_, value) -> value }
            .reversed()
            .toMap()
        val takeSize: Int = sequences.size * this.takeSize / 100
        val filteredSequences: MutableSet<UniquePattern> = sequences
            .toList()
            .take(takeSize)
            .toMap()
            .toMutableMap().keys

        // Get the last drawings and store them in an array of arrays.
        val lastDrawingsArraySize = sequenceSize - 1
        val lastDrawings = Array(lastDrawingsArraySize) { intArrayOf() }
        for (i in 1 until sequenceSize) {
            lastDrawings[i - 1] = drawings[drawings.size - i].numbers
        }
        // Reverse the array so the drawings have the same order as they have been drawn.
        lastDrawings.reverse()

        // Generate all possible sequences for the selected drawings.
        val predictionSequences = numberSequences.generateSequences(lastDrawings)
        // Filter the already filtered sequences and take only the ones that have the same numbers as the ones in `predictionSequences`.
        // This creates a map of sequences that contain the next possible numbers to be drawn.
        // The next possible numbers are always in the last index of the array.
        val filteredPredictionSequences = filteredSequences.filter { filteredSequence ->
            predictionSequences.any { predictionSequence ->
                predictionSequence.take(predictionSequence.size) == filteredSequence.array.take(predictionSequence.size).toList()
            }
        }

        // Extract the next numbers to a set.
        _predictionNumbers.addAll(filteredPredictionSequences.map { it.array[it.array.size - 1] })
        println("Prediction numbers size - ${_predictionNumbers.size}")
    }
}