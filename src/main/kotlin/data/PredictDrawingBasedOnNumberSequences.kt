package data

import model.TotoType
import model.UniquePattern
import kotlin.random.Random

class PredictDrawingBasedOnNumberSequences(
    val totoType: TotoType,
    val drawings: Drawings,
    val predictNumberSequences: PredictNumberSequences
) {

    init {
        predict()
    }

    private fun predict() {
        val drawingSize = totoType.size
        val allDrawings = drawings.drawings.map { UniquePattern(it.numbers) }
        val random = Random(System.currentTimeMillis())
        val numbers = predictNumberSequences.predictionNumbers.toList()
        var allPossibleCombinationsSize = allPossibleCombinations(
            totalNumbersSize = numbers.size,
            combinationSize = totoType.size
        )
        val predictions = mutableSetOf<UniquePattern>()
        val predictionSize = 4

        val predictionNumbers = mutableSetOf<Int>()
        while (predictions.size < predictionSize) {
            predictionNumbers.clear()
            while (predictionNumbers.size < drawingSize) {
                predictionNumbers.add(numbers[random.nextInt(numbers.size)])
            }
            allPossibleCombinationsSize--
            if (allPossibleCombinationsSize > 0L) {
                continue
            }
            val drawing = UniquePattern(predictionNumbers.sorted().toIntArray())
            if (allDrawings.contains(drawing)) {
                continue
            }
            predictions.add(drawing)
        }
        predictions.forEach {
            println(it.array.toList().toString().replace("[", "").replace("]", ""))
        }
    }

    private fun allPossibleCombinations(totalNumbersSize: Int, combinationSize: Int): Long = calculateCombinations(
        totalNumbersSize = totalNumbersSize,
        combinationSize = combinationSize
    ).toLong()

    private fun factorial(num: Int): Double {
        var result = 1.0
        for (i in 1..num) {
            result *= i.toLong()
        }
        return result
    }

    private fun calculateCombinations(totalNumbersSize: Int, combinationSize: Int): Double {
        if (combinationSize < 0 || combinationSize > totalNumbersSize) {
            return 0.0
        }

        return factorial(totalNumbersSize) /
                (factorial(combinationSize) * factorial(totalNumbersSize - combinationSize))
    }
}