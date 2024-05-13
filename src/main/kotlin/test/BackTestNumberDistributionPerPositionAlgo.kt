package test

import algo.PredictViaNumberDistributionPerPosition
import data.Drawings
import model.TotoType

class BackTestNumberDistributionPerPositionAlgo(
    private val totoType: TotoType,
    private val allDrawings: Drawings,
    private val predictViaNumberDistributionPerPosition: PredictViaNumberDistributionPerPosition
) {

    fun backtest(
        startYearFilter: Int,
        backTestSampleYearSize: Int
    ) {
        val trainingData = allDrawings.drawings.filter { drawing ->
            drawing.year >= startYearFilter - backTestSampleYearSize
                    && drawing.year < startYearFilter
        }.toMutableList()

        val upcomingData = allDrawings.drawings.filter { drawing ->
            drawing.year >= startYearFilter
        }.toMutableList()

        val totalDrawings = upcomingData.size
        var zeros = 0
        var ones = 0
        var twos = 0
        var threes = 0
        var fours = 0
        var fives = 0
        var sixes = 0

        do {
            val numbersToUse: Array<List<Int>> = predictViaNumberDistributionPerPosition.getNumbersToUse(trainingData)
            val nextDrawing = upcomingData.removeFirst()

            var containsCount = 0
            numbersToUse.forEachIndexed { index, numbers ->
                if (numbers.contains(nextDrawing.numbers[index])) {
                    containsCount++
                }
            }

            when (containsCount) {
                0 -> zeros++
                1 -> ones++
                2 -> twos++
                3 -> threes++
                4 -> fours++
                5 -> fives++
                6 -> sixes++
                else -> {}
            }

//            trainingData.removeFirst() // Comment out for slightly better results
            trainingData.add(nextDrawing)
        } while (upcomingData.isNotEmpty())

        println("Total tested drawings ($totoType) - $totalDrawings")
        println("Results:")
        println("Zeros - $zeros, ${zeros * 100.0 / totalDrawings}%")
        println("Ones - $ones, ${ones * 100.0 / totalDrawings}%")
        println("Twos - $twos, ${twos * 100.0 / totalDrawings}%")
        println("Threes - $threes, ${threes * 100.0 / totalDrawings}%")
        println("Fours - $fours, ${fours * 100.0 / totalDrawings}%")
        println("Fives - $fives, ${fives * 100.0 / totalDrawings}%")
        println("Sixes - $sixes, ${sixes * 100.0 / totalDrawings}%")
    }
}