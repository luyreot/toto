package extensions

import model.ArrayPattern
import model.NumberPattern
import model.SubPattern
import utils.convertDrawingIntArrayToString

fun MutableMap<String, SubPattern>.addPattern(content: Any, drawingIndex: Int) {
    require(!(content !is Int && content !is IntArray)) { "Content type must be either Int or IntArray." }

    val key = if (content is Int) content.toString() else convertDrawingIntArrayToString(content as IntArray)
    if (containsKey(key)) {
        get(key)!!.incrementTimesOccurred()
        get(key)!!.addFrequency(drawingIndex)
    } else {
        if (content is Int) set(key, NumberPattern(content, drawingIndex))
        else set(key, ArrayPattern(content as IntArray, drawingIndex))
    }
}

fun MutableMap<String, SubPattern>.calculatePatternProbabilities(totalDrawingsCount: Int) {
    forEach { (_, pattern) ->
        pattern.calculateProbability(totalDrawingsCount)
        // The frequency count represents how many spaces are between each timeOccurred
        val frequenciesTotalCount = pattern.frequencies.values.stream()
                .map { it.timesOccurred }
                .reduce(0, Int::plus)
        if (pattern.timesOccurred == frequenciesTotalCount + 1) {
            pattern.frequencies.forEach { (_, frequencyPattern) ->
                frequencyPattern.calculateProbability(frequenciesTotalCount)
            }
        }
    }
}

fun MutableMap<String, SubPattern>.sortPatternsByProbability(): MutableMap<String, SubPattern> {
    return toList().sortedBy { (_, value) -> value }.toMap().toMutableMap()
}

fun MutableMap<String, SubPattern>.sortFrequenciesByProbability() {
    forEach { (_, pattern) ->
        pattern.frequencies = pattern.frequencies.toList().sortedBy { (_, value) -> value }.toMap().toMutableMap()
    }
}

fun MutableMap<String, MutableMap<String, Int>>.addChain(drawing: IntArray) {
    var firstKey: String
    var secondKey: String
    drawing.forEachIndexed { firstIndex, firstNumber ->
        firstKey = firstNumber.toString()
        drawing.forEachIndexed { secondIndex, secondNumber ->
            secondKey = secondNumber.toString()
            if (firstIndex != secondIndex) {
                val count = getOrPut(firstKey) { mutableMapOf() }.getOrPut(secondKey) { 0 }.inc()
                get(firstKey)!![secondKey] = count
            }
        }
    }
}

fun MutableMap<String, MutableMap<String, Int>>.addChain(prevDrawing: IntArray, currDrawing: IntArray) {
    var prevKey: String
    var currKey: String
    prevDrawing.forEach { prevNumber ->
        prevKey = prevNumber.toString()
        currDrawing.forEach { currNumber ->
            currKey = currNumber.toString()
            val count = getOrPut(prevKey) { mutableMapOf() }.getOrPut(currKey) { 0 }.inc()
            get(prevKey)!![currKey] = count
        }
    }
}

fun MutableMap<String, MutableMap<String, Int>>.addChain(prevColorPattern: String, currColorPattern: String) {
    val count = getOrPut(prevColorPattern) { mutableMapOf() }.getOrPut(currColorPattern) { 0 }.inc()
    get(prevColorPattern)!![currColorPattern] = count
}