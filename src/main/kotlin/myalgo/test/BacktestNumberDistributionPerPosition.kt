package myalgo.test

import myalgo.PredictViaNumberDistributionPerPosition
import myalgo.Drawings
import model.TotoType
import util.Constants

class BacktestNumberDistributionPerPosition(
    private val totoType: TotoType,
    private val allDrawings: Drawings,
    private val predictViaNumberDistributionPerPosition: PredictViaNumberDistributionPerPosition
) {

    fun backtest(trainingDataYearFilter: Int, backtestSampleSizeYears: Int) {
        val trainingData = allDrawings.drawings
            .filter { drawing ->
                drawing.year > Constants.PAGE_YEAR.toInt() - trainingDataYearFilter - backtestSampleSizeYears &&
                        drawing.year < Constants.PAGE_YEAR.toInt() - (backtestSampleSizeYears - 1)
            }
            .toMutableList()

        val upcomingData = allDrawings.drawings
            .filter { drawing -> drawing.year >= Constants.PAGE_YEAR.toInt() - (backtestSampleSizeYears - 1) }
            .toMutableList()

        var totalTestedDrawings = 0
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

            trainingData.removeFirst() // Comment out for slightly better results
            trainingData.add(nextDrawing)

            totalTestedDrawings++
        } while (upcomingData.isNotEmpty())

        println("Total tested drawings ($totoType) - $totalTestedDrawings")
        println("Results:")
        println("Zeros - $zeros, ${zeros * 100.0 / totalTestedDrawings}%")
        println("Ones - $ones, ${ones * 100.0 / totalTestedDrawings}%")
        println("Twos - $twos, ${twos * 100.0 / totalTestedDrawings}%")
        println("Threes - $threes, ${threes * 100.0 / totalTestedDrawings}%")
        println("Fours - $fours, ${fours * 100.0 / totalTestedDrawings}%")
        println("Fives - $fives, ${fives * 100.0 / totalTestedDrawings}%")
        println("Sixes - $sixes, ${sixes * 100.0 / totalTestedDrawings}%")
    }
}