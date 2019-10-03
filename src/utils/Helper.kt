package utils

import crawler.WebCrawler
import extensions.toDrawingIntArray
import model.ArrayPattern
import model.Drawing
import service.colorPatterns

fun updateYearDrawings() = WebCrawler(getTxtFileContents(getCurrentYearTxtFilePath())).crawl()

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