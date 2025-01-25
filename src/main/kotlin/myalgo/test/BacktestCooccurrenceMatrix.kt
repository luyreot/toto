package myalgo.test

import myalgo.CooccurrenceMatrix
import myalgo.Drawings
import model.TotoType

class BacktestCooccurrenceMatrix(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val yearFilter: Int
) {

    fun test() {
        val drawingsToTest = drawings.drawings.filter { it.year >= yearFilter }
        val matrix = CooccurrenceMatrix(totoType, drawingsToTest)

        var greaterThanCnt = 0
        var equalToCnt = 0
        var lessThanCnt = 0

        drawingsToTest
            .map { it.numbers }
            .forEach { drawing ->
                val cooccurrences = mutableListOf<Int>()
                for (row in 0 until drawing.size - 1) {
                    for (column in row + 1 until drawing.size) {
                        val rowNum = drawing[row]
                        val colNum = drawing[column]
                        cooccurrences.add(matrix.matrix[rowNum][colNum])
                    }
                }

                val lessThanMidpoint = cooccurrences.count { it < matrix.midpoint }
                val equalToMidpoint = cooccurrences.count { it == matrix.midpoint }
                val greaterThanMidpoint = cooccurrences.count { it > matrix.midpoint }

                when {
                    greaterThanMidpoint > equalToMidpoint && greaterThanMidpoint > lessThanMidpoint -> greaterThanCnt++

                    greaterThanMidpoint == equalToMidpoint && greaterThanMidpoint > lessThanMidpoint -> {
                        greaterThanCnt++
                        equalToCnt++
                    }

                    greaterThanMidpoint == lessThanMidpoint && greaterThanMidpoint > equalToMidpoint -> {
                        greaterThanCnt++
                        lessThanCnt++
                    }

                    lessThanMidpoint > greaterThanMidpoint && lessThanMidpoint > equalToMidpoint -> lessThanCnt++

                    lessThanMidpoint == equalToMidpoint && lessThanMidpoint > greaterThanCnt -> {
                        lessThanCnt++
                        equalToCnt++
                    }

                    else -> equalToCnt++
                }
            }

        println("-------")
        println("Testing CooccurrenceMatrix")
        println("Toto type - $totoType")
        println("Year filter - $yearFilter")
        println("Above midpoint count - $greaterThanCnt")
        println("Equal to midpoint count - $equalToCnt")
        println("Below midpoint count - $lessThanCnt")
        println("-------")
    }
}