package algo

import data.*
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
        val numberTable = NumberTable(totoType, filteredDrawings)
        val numberHotCold = NumberHotCold(totoType, filteredDrawings, numberTable.numbers)
        val numberDistributionPerPosition = NumberDistributionPerPosition(totoType, filteredDrawings)
        val subsequentDrawingCombinations = SubsequentDrawingCombinations(filteredDrawings, size = 2)

        val subsequentDrawingEqualNumbers: Set<Int> = subsequentDrawingCombinations.combinationsByMedian
            .filter { it.key.array[0] == it.key.array[1] }
            .map { it.key.array.first() }
            .toSet()
        val lastDrawingNumbers: List<Int> = filteredDrawings.takeLast(1).map { it.numbers.toList() }.flatten()
        val numbersListToUse: Array<MutableList<Int>> = Array(totoType.size) { mutableListOf() }

        numberDistributionPerPosition.distributionByMedianArray.forEachIndexed { index, numberSet ->
            numberSet.forEach { number ->
                if (numberHotCold.isHot(number)) {
                    if (!lastDrawingNumbers.contains(number) || subsequentDrawingEqualNumbers.contains(number)) {
                        numbersListToUse[index].add(number)
                    }
                } else {
                    numbersListToUse[index].add(number)
                }
            }
        }

        return numbersListToUse.map { it.toList() }.toTypedArray()
    }

    fun generatePredictions(
        numbersListToUse: Array<List<Int>>,
        drawingsToGenerate: Int
    ) {
        val predictionDrawings = mutableSetOf<UniqueIntArray>()
        val predictionSize = 50
        val tmpArray = IntArray(totoType.size)

        println("-------")
        val uniqueNumberToUse = numbersListToUse.map { it }.flatten().toSet().sorted()
        println("Using a total of ${uniqueNumberToUse.size} numbers.")
        println(uniqueNumberToUse)
        println("-")
        numbersListToUse.forEach { println(it.sorted().toString()) }
        println("-------")

        while (predictionDrawings.size < predictionSize) {
            tmpArray.clear()

            numbersListToUse.forEachIndexed { index, nums ->
                var tmpNums = nums
                var _num: Int? = null

                while (_num == null || tmpArray.contains(_num)) {
                    var numOfShuffles = Random(System.currentTimeMillis()).nextInt(10)

                    while (numOfShuffles > 0) {
                        tmpNums = tmpNums.shuffled()
                        numOfShuffles--
                    }

                    _num = tmpNums.first()
                }

                tmpArray[index] = _num
            }

            val uniqueIntArray = UniqueIntArray(tmpArray.copyOf().sortedArray())
            if (allUniqueDrawings.contains(uniqueIntArray)) {
                continue
            }

            predictionDrawings.add(uniqueIntArray)
        }

        predictionDrawings
            .shuffled()
            .take(drawingsToGenerate)
            .forEach {
                println(it.numbers.toList().toString().replace("[", "").replace("]", ""))
            }

        println()
    }
}