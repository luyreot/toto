package old.utils

import impl.utils.Const.HIGH_LOW_MIDPOINT
import old.extensions.toDrawingIntArray
import old.extensions.toDrawingString
import old.model.ArrayPattern
import old.model.Drawing
import old.service.allPossibleColorPatterns
import old.service.colorPatterns

fun getDrawingsFromFileContents(year: String, drawings: List<String>): List<Drawing> {
    val objectList = mutableListOf<Drawing>()
    drawings.forEachIndexed { index, drawing ->
        objectList.add(Drawing(year, index + 1, drawing.toDrawingIntArray()))
    }
    return objectList
}

fun convertDrawingIntArrayToColorPatternArray(drawing: IntArray): IntArray =
        drawing.map { number -> number / 10 }.toIntArray()

fun convertDrawingIntArrayToLowHighPatternArray(drawing: IntArray): IntArray =
        drawing.map { number -> if (number <= HIGH_LOW_MIDPOINT) 0 else 1 }.toIntArray()

fun convertDrawingIntArrayToOddEvenPatternArray(drawing: IntArray): IntArray =
        drawing.map { number -> if ((number and 1) == 0) 1 else 0 }.toIntArray().sortedArray()

fun getAllPossibleColorPatterns(patterns: MutableSet<String>, end: Int, array: IntArray, index: Int) {
    for (x in 0..end) {
        if (index > 0 && x < array[index - 1]) continue
        array[index] = x
        if (index == array.size - 1) {
            patterns.add(array.sortedArray().toDrawingString())
            if (x == end) return
        } else {
            getAllPossibleColorPatterns(patterns, end, array, index + 1)
        }
    }
}

// Methods below are work in progress

fun findMissingColorPatterns() {
    val missingPatterns = allPossibleColorPatterns - colorPatterns.keys
    if (missingPatterns.isEmpty()) return

    // Note that not every position (1-6) has every color code (0-4)
    val scores = calculateColorPatternCodeScores()
    // TODO calculate score for missing color patterns
}

/**
 * Model: position (1-6), codes per position (0-4) + their score
 * NOTE: Not every position (1-6) will have every color code (0-4).
 */
fun calculateColorPatternCodeScores(): MutableMap<Int, MutableMap<Int, Double>> {
    val scores = mutableMapOf<Int, MutableMap<Int, Double>>()
    // fill the map with the scores
    colorPatterns.forEach { (_, pattern) ->
        pattern as ArrayPattern
        pattern.numbers.forEachIndexed { index, number ->
            val newScore = scores.getOrPut(index) { mutableMapOf() }.getOrPut(number) { 0.0 }.plus(pattern.timesOccurred)
            scores[index]!![number] = newScore
        }
    }
    // calculate each score's possibility
    scores.forEach { (_, codeScores) ->
        val scoreSum = codeScores.map { (_, score) -> score }.sum()
        codeScores.forEach { (code, score) ->
            codeScores[code] = score.div(scoreSum)
        }
    }
    // fill any missing codes, mostly 0 and 4
    for (code in 0..4) {
        scores.forEach { (_, codeScores) ->
            codeScores.putIfAbsent(code, 0.0)
        }
    }
    return scores
}