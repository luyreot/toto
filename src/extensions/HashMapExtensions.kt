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