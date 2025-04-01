package systems.gapanalysis

import model.TotoType
import util.Constants.PAGE_YEAR
import util.Draw
import util.UniqueIntArray
import util.loadDrawings
import kotlin.random.Random

fun backtest(totoType: TotoType, yearFilter: Int, fromYear: Int) {
    val draws = mutableListOf<Draw>()
    val upcomingDraws = mutableListOf<Draw>()
    loadDrawings(totoType).let { loadedDraws ->
        draws.addAll(combineDraws(totoType, loadedDraws.filter { it.year >= yearFilter - 1 && it.year < fromYear }))
        upcomingDraws.addAll(combineDraws(totoType, loadedDraws.filter { it.year >= fromYear }))
    }

    val upcomingDrawsIterator = upcomingDraws.iterator()
    while (upcomingDrawsIterator.hasNext()) {
        val nextDraw = upcomingDrawsIterator.next()

        val gaps = Gaps()
        gaps.setGaps(totoType, draws, yearFilter)
        val currentNumberGaps = gaps.getCurrentGaps(totoType, draws)

        val averageOccurrences = YearlyAverageOccurrences()
        averageOccurrences.setOccurrences(draws, yearFilter, PAGE_YEAR.toInt())
        val currentYearOccurrences = averageOccurrences.getYearlyOccurrences(draws, PAGE_YEAR.toInt())

        val numbersPerPosition = NumbersPerPosition(totoType)
        numbersPerPosition.setNumbers(draws, yearFilter)

        // region number to use

        val numbersToUse = (1..totoType.totalNumbers).toMutableList()
        val numbersNotToUse = mutableSetOf<Int>()

        // remove numbers that have occurred too many times for this year
        for (number in 1..totoType.totalNumbers) {
            val currentNumberOccurrence = currentYearOccurrences[number] ?: 0
            val averageNumberOccurrence = averageOccurrences.occurrences[number]!!
            if (currentNumberOccurrence > averageNumberOccurrence) {
                if (numbersToUse.remove(number)) {
                    numbersNotToUse.add(number)
                }
            }
        }

        // remove numbers that do not satisfy the gaps logic
        for (number in 1..totoType.totalNumbers) {
            val currentNumberGap = currentNumberGaps[number]!!
            val currentNumberGreaterThanGap = gaps.greaterThan[number]!!
            val currentNumberLessThanGap = gaps.lessThan[number]!!

            if (currentNumberGap in (currentNumberLessThanGap + 1) until currentNumberGreaterThanGap) {
                if (numbersToUse.remove(number)) {
                    numbersNotToUse.add(number)
                }
            }
        }

        // remove numbers in positions array
        numbersPerPosition.numbers.forEach { numberSet ->
            numberSet.removeIf { it !in numbersToUse }
        }

        // endregion number to use

        if (totoType == TotoType.T_5X35) {
            val draw1 = nextDraw.numbers.take(totoType.size)
            val draw2 = nextDraw.numbers.takeLast(totoType.size)

            println("Next draws:")
            println(draw1.toList())
            println(draw2.toList())
        } else {
            println("Next draws:")
            println(nextDraw.numbers.contentToString())
        }

        println("---")
        println("Numbers (${numbersToUse.size}/${totoType.totalNumbers}):")
        println(numbersToUse.toString())
        println("Skipping numbers (${numbersNotToUse.size}/${totoType.totalNumbers}):")
        println(numbersNotToUse.toString())

        if (totoType == TotoType.T_5X35) {
            val draw1 = nextDraw.numbers.take(totoType.size)
            val draw2 = nextDraw.numbers.takeLast(totoType.size)

            val correctlyPredictedNumbersAtPosition = mutableListOf<Int>()
            val incorrectlyPredictedNumbersAtPosition = mutableListOf<Int>()
            for (i in draw1.indices) {
                if (draw1[i] in numbersPerPosition.numbers[i]) {
                    correctlyPredictedNumbersAtPosition.add(draw1[i])
                } else {
                    incorrectlyPredictedNumbersAtPosition.add(draw1[i])
                }
            }

            println("-")
            println("(1) Correctly predicted numbers (${correctlyPredictedNumbersAtPosition.size}):")
            println(correctlyPredictedNumbersAtPosition.toString())
            println("(1) Incorrectly predicted numbers (${incorrectlyPredictedNumbersAtPosition.size}):")
            println(incorrectlyPredictedNumbersAtPosition.toString())

            correctlyPredictedNumbersAtPosition.clear()
            incorrectlyPredictedNumbersAtPosition.clear()

            for (i in draw2.indices) {
                if (draw2[i] in numbersPerPosition.numbers[i]) {
                    correctlyPredictedNumbersAtPosition.add(draw2[i])
                } else {
                    incorrectlyPredictedNumbersAtPosition.add(draw2[i])
                }
            }

            println("-")
            println("(2) Correctly predicted numbers (${correctlyPredictedNumbersAtPosition.size}):")
            println(correctlyPredictedNumbersAtPosition.toString())
            println("(2) Incorrectly predicted numbers (${incorrectlyPredictedNumbersAtPosition.size}):")
            println(incorrectlyPredictedNumbersAtPosition.toString())
        } else {
            val correctlyPredictedNumbersAtPosition = mutableListOf<Int>()
            val incorrectlyPredictedNumbersAtPosition = mutableListOf<Int>()
            for (i in nextDraw.numbers.indices) {
                if (nextDraw.numbers[i] in numbersPerPosition.numbers[i]) {
                    correctlyPredictedNumbersAtPosition.add(nextDraw.numbers[i])
                } else {
                    incorrectlyPredictedNumbersAtPosition.add(nextDraw.numbers[i])
                }
            }

            println("-")
            println("Correctly predicted numbers (${correctlyPredictedNumbersAtPosition.size}):")
            println(correctlyPredictedNumbersAtPosition.toString())
            println("Incorrectly predicted numbers (${incorrectlyPredictedNumbersAtPosition.size}):")
            println(incorrectlyPredictedNumbersAtPosition.toString())
        }

        println("-------")

        draws.add(nextDraw)
    }

    println()
}

fun analysePredict(totoType: TotoType, yearFilter: Int, timeToGenerate: Long = 60000) {
    val draws = combineDraws(totoType, loadDrawings(totoType).filter { it.year >= yearFilter - 1 })

    val gaps = Gaps()
    gaps.setGaps(totoType, draws, yearFilter)
    val currentNumberGaps = gaps.getCurrentGaps(totoType, draws)

    val averageOccurrences = YearlyAverageOccurrences()
    averageOccurrences.setOccurrences(draws, yearFilter, PAGE_YEAR.toInt())
    val currentYearOccurrences = averageOccurrences.getYearlyOccurrences(draws, PAGE_YEAR.toInt())

    val numbersPerPosition = NumbersPerPosition(totoType)
    numbersPerPosition.setNumbers(draws, yearFilter)

    // region number to use

    val numbersToUse = (1..totoType.totalNumbers).toMutableList()
    val numbersNotToUse = mutableSetOf<Int>()

    // remove numbers that have occurred too many times for this year
    for (number in 1..totoType.totalNumbers) {
        val currentNumberOccurrence = currentYearOccurrences[number] ?: 0
        val averageNumberOccurrence = averageOccurrences.occurrences[number]!!
        if (currentNumberOccurrence > averageNumberOccurrence) {
            if (numbersToUse.remove(number)) {
                numbersNotToUse.add(number)
            }
        }
    }

    // remove numbers that do not satisfy the gaps logic
    for (number in 1..totoType.totalNumbers) {
        val currentNumberGap = currentNumberGaps[number]!!
        val currentNumberGreaterThanGap = gaps.greaterThan[number]!!
        val currentNumberLessThanGap = gaps.lessThan[number]!!

        if (currentNumberGap in (currentNumberLessThanGap + 1) until currentNumberGreaterThanGap) {
            if (numbersToUse.remove(number)) {
                numbersNotToUse.add(number)
            }
        }
    }

    // remove numbers in positions array
    numbersPerPosition.numbers.forEach { numberSet ->
        numberSet.removeIf { it !in numbersToUse }
    }

    // endregion number to use

    // region prediction

    val random = Random(System.currentTimeMillis())
    val predictions = mutableSetOf<UniqueIntArray>()

    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < timeToGenerate) {
        val numbers = mutableSetOf<Int>()
        while (numbers.size < totoType.size) {
            numbers.add(numbersToUse[random.nextInt(numbersToUse.size)])
        }
        predictions.add(UniqueIntArray(numbers.sorted().toIntArray()))
    }

    if (totoType != TotoType.T_5X35) {
        val allDraws = loadDrawings(totoType).map { UniqueIntArray(it.numbers) }.toSet()
        predictions.removeAll(allDraws)
    }

    // remove predictions that do not satisfy the number position logic
    predictions.removeIf { uIntArr ->
        var hasIncorrectNumberAtPosition = false
        for (i in uIntArr.array.indices) {
            val number = uIntArr.array[i]
            if (number !in numbersPerPosition.numbers[i]) {
                hasIncorrectNumberAtPosition = true
                break
            }
        }
        hasIncorrectNumberAtPosition
    }

    println("---")
    println("Numbers (${numbersToUse.size}/${totoType.totalNumbers}):")
    println(numbersToUse.toString())
    println("Skipping numbers (${numbersNotToUse.size}/${totoType.totalNumbers}):")
    println(numbersNotToUse.toString())
    println("Numbers per position:")
    numbersPerPosition.numbers.forEachIndexed { index, numberSet ->
        println("$index (${numberSet.size}): ${numberSet.sorted()}")
    }

    println("---")
    println("Generated unique predictions: ${predictions.size}")

    with(predictions.toList()) {
        val indexes = indices.toMutableList()

        fisherYatesShuffle(random, indexes)
        fisherYatesShuffle(random, indexes)
        fisherYatesShuffle(random, indexes)
        fisherYatesShuffle(random, indexes)
        fisherYatesShuffle(random, indexes)
        fisherYatesShuffle(random, indexes)
        fisherYatesShuffle(random, indexes)

        var numberOfPredictions = when (totoType) {
            TotoType.T_6X49 -> 4
            TotoType.T_6X42 -> 4
            TotoType.T_5X35 -> 12
        }

        while (numberOfPredictions > 0) {
            println(get(indexes.random(random)).array.contentToString().replace("[", "").replace("]", ""))
            numberOfPredictions--
        }
    }

    // endregion prediction

    println()
}

/**
 * Combine two subsequent draws when toto type is 5x35.
 */
fun combineDraws(totoType: TotoType, draws: List<Draw>): List<Draw> {
    if (totoType != TotoType.T_5X35) return draws

    val updated = mutableListOf<Draw>()
    for (i in draws.indices) {
        if (i % 2 != 1) continue
        updated.add(
            Draw(
                year = draws[i].year,
                id = 0, // Not needed
                numbers = draws[i - 1].numbers + draws[i].numbers
            )
        )
    }

    return updated
}

fun fisherYatesShuffle(random: Random, list: MutableList<Int>) {
    for (i in list.size - 1 downTo 1) {
        val j = random.nextInt(i + 1)
        list[i] = list[j].also { list[j] = list[i] } // Swap
    }
}