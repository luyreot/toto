package algo

import data.Drawings
import data.NumberDistributionPerPosition
import data.SubsequentDrawingCombinations
import extension.clear
import model.Drawing
import model.TotoType
import model.UniqueIntArray
import kotlin.random.Random

class PredictViaNumberDistributionPerPosition(
    private val totoType: TotoType,
    private val allDrawings: Drawings
) {

    private val allUniqueDrawings = mutableSetOf<UniqueIntArray>()

    init {
        prepareData()
    }

    private fun prepareData() {
        allUniqueDrawings.addAll(
            allDrawings.drawings
                .map { UniqueIntArray(it.numbers) }
                .toSet()
        )
    }

    fun getNumbersToUse(filteredDrawings: List<Drawing>): Array<List<Int>> {
        val numberDistributionPerPosition = NumberDistributionPerPosition(totoType, filteredDrawings)
        val subsequentDrawingCombinations = SubsequentDrawingCombinations(filteredDrawings, size = 2)
        val lastDrawingNumbers: List<Int> = filteredDrawings
            .takeLast(if (totoType == TotoType.T_5X35) 2 else 1)
            .map { it.numbers.toList() }
            .flatten()
        val numbersListToUse: Array<MutableList<Int>> = Array(totoType.size) { mutableListOf() }

        val subsequentDrawingCombinationNumbers = subsequentDrawingCombinations.combinationsByMean
            .filter { entry -> lastDrawingNumbers.contains(entry.key.array[0]) }
            .map { entry -> entry.key.array[1] }
            .toSet()

        numberDistributionPerPosition.distributionByMeanArray.forEachIndexed { index, numberSet ->
            numberSet.forEach { number ->
                // Skip number it does not exist as the 2nd number in a two number pair of subsequent drawing number combinations
                if (!subsequentDrawingCombinationNumbers.contains(number)) {
                    return@forEach
                }

                numbersListToUse[index].add(number)
            }
        }

        return numbersListToUse.map { it.toList() }.toTypedArray()
    }

    fun generatePredictions(
        numbersListToUse: Array<List<Int>>,
        drawingsToGenerate: Int
    ): Set<UniqueIntArray> {
        val tmpArray = IntArray(totoType.size)
        if (tmpArray.size != numbersListToUse.size) {
            throw IllegalArgumentException("Array sizes are different!")
        }

        println("-------")
        val uniqueNumberToUse = numbersListToUse.map { it }.flatten().toSet().sorted()
        println("Using a total of ${uniqueNumberToUse.size} numbers.")
        println(uniqueNumberToUse)
        println("-")
        numbersListToUse.forEach { println(it.sorted().toString()) }
        println("-------")

        val predictions = mutableSetOf<UniqueIntArray>()
        val discarded = mutableSetOf<UniqueIntArray>()
        val predictionSize = 6000000
        val random = Random(System.currentTimeMillis())

        val startTime = System.currentTimeMillis()
        while (predictions.size < predictionSize) {
            if (System.currentTimeMillis() - startTime > 30000) {
                break
            }

            tmpArray.clear()

            numbersListToUse.forEachIndexed { index, numbers ->
                var nextNumber = numbers[random.nextInt(numbers.size)]
                while (tmpArray.contains(nextNumber)) {
                    nextNumber = numbers[random.nextInt(numbers.size)]
                }
                tmpArray[index] = nextNumber
            }

            val uniqueIntArray = UniqueIntArray(tmpArray.copyOf().sortedArray())

            if (discarded.contains(uniqueIntArray)) {
                continue
            }

            // 5x35 can have drawing predictions that have been drawn previously
            if (totoType != TotoType.T_5X35 && allUniqueDrawings.contains(uniqueIntArray)) {
                discarded.add(uniqueIntArray)
                continue
            }

            predictions.add(uniqueIntArray)
        }

        println("Total number of generated predictions - ${predictions.size}.")

        val predictionsList = predictions.toList()
        val output = mutableSetOf<UniqueIntArray>()
        val chosenIndexes = mutableSetOf<Int>()

        while (output.size < drawingsToGenerate) {
            val randomIndex = random.nextInt(predictionsList.size)
            if (chosenIndexes.contains(randomIndex)) {
                continue
            }
            output.add(predictionsList[randomIndex])
            chosenIndexes.add(randomIndex)
        }

        return output
    }
}