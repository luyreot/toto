package myalgo.test

import myalgo.Drawings
import model.TotoType

class BackTestNumberOccurrencesAgainstPreviousDrawings {

    // sample size 2 for often occurring numbers
    // sample size 1 for less often occurring numbers
    fun test(totoType: TotoType, drawings: Drawings) {
        val sampleSize = 1
        val numbers = drawings.drawings.map { it.numbers }.toMutableList()

        data class Checks(
            var totalChecks: Int,
            var occurrences: Int
        )

        val data = mutableMapOf<Int, Checks>()
        val bufferList = mutableListOf<IntArray>()

        while (numbers.size > 0) {
            while (bufferList.size < sampleSize + 1) {
                val toAdd = numbers.removeAt(numbers.size - 1)
                bufferList.add(toAdd)
            }

            val toExamine = bufferList.removeFirst()

            if (bufferList.size < sampleSize) {
                break
            }

            toExamine.forEach { number: Int ->
                data.putIfAbsent(number, Checks(totalChecks = 0, occurrences = 0))

                data[number]?.let { numberData ->
                    numberData.totalChecks++

                    bufferList.forEach { numbers ->
                        if (numbers.contains(number)) {
                            numberData.occurrences++
                        }
                    }
                }
            }
        }

        println()
    }
}