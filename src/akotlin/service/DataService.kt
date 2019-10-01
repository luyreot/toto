package akotlin.service

import akotlin.model.Drawing
import akotlin.model.IntArrayPattern
import akotlin.model.NumberPattern
import akotlin.utils.*
import java.util.*
import kotlin.streams.toList

object DataService {

    // Using a TreeMap to sort the drawings per year.
    // The drawings themselves are sorted by the date the were released on.
    val drawingsMap = TreeMap<String, List<Drawing>>()

    // TODO maybe store the patterns in a HashMap so they are ordered by date
    val numberPatternsMap = TreeMap<Int, NumberPattern>()
    val colorPatternsMap = TreeMap<String, IntArrayPattern>()
    val lowHighPatternsMap = TreeMap<String, IntArrayPattern>()
    val oddEvenPatternsMap = TreeMap<String, IntArrayPattern>()

    fun loadDrawingsForYears(vararg years: String) = years.forEach { year ->
        drawingsMap[year] = getDrawingsFromFileContents(
                year,
                getTxtFileContents(
                        getYearTxtFilePath(year)
                )
        )
    }

    fun loadAllDrawings() = listFileNamesInInPath(PATH_TXT_FOLDER).forEach { file ->
        drawingsMap[file.name] = getDrawingsFromFileContents(
                file.name,
                getTxtFileContents(file)
        )
    }

    fun generatePatterns() {
        val drawings = drawingsMap.values.stream().flatMap { drawingList -> drawingList.stream() }.toList()
        val totalDrawingsCount = drawings.count()
        var mapKey: String
        var mapValue: IntArray

        drawings.forEachIndexed { drawingIndex, drawing ->
            drawing.numbers.forEachIndexed { numberIndex, number ->
                // Fill the number patterns map
                if (numberPatternsMap.containsKey(number)) {
                    numberPatternsMap[number]!!.incrementTimesOccurred()
                    numberPatternsMap[number]!!.addFrequency(drawingIndex)
                } else {
                    numberPatternsMap[number] = NumberPattern(number, drawingIndex)
                }
            }

            // Fill the color patterns map
            processPattern(colorPatternsMap, convertDrawingArrayToColorPatternArray(drawing.numbers), drawingIndex)
            // Fill the odd-even patterns map
            processPattern(lowHighPatternsMap, convertDrawingArrayToLowHighPatternArray(drawing.numbers), drawingIndex)
            // Fill the high-low patterns map
            processPattern(oddEvenPatternsMap, convertDrawingArrayToOddEvenPatternArray(drawing.numbers), drawingIndex)
        }

        // Calculate probabilities of the number patterns
        numberPatternsMap.forEach { (number, numberPattern) ->
            numberPattern.calculateProbability(totalDrawingsCount)
            val frequenciesTotalCount = numberPattern.frequencyMap.values.stream()
                    .map { it.timesOccurred }
                    .reduce(0, Int::plus)

            if (numberPattern.timesOccurred == frequenciesTotalCount + 1) {
                numberPattern.frequencyMap.forEach { (frequency, frequencyPattern) ->
                    frequencyPattern.calculateProbability(frequenciesTotalCount)
                }
            }
        }

        // Calculate probabilities of the color patterns
        calculatePatternProbabilities(colorPatternsMap, totalDrawingsCount)
        // Calculate probabilities of the odd-even patterns
        calculatePatternProbabilities(lowHighPatternsMap, totalDrawingsCount)
        //Calculate probabilities of the high-low patterns
        calculatePatternProbabilities(oddEvenPatternsMap, totalDrawingsCount)
    }

    private fun processPattern(map: TreeMap<String, IntArrayPattern>, mapValue: IntArray, drawingIndex: Int) {
        val mapKey = convertIntArrayToString(mapValue)
        if (map.containsKey(mapKey)) {
            map[mapKey]!!.incrementTimesOccurred()
            map[mapKey]!!.addFrequency(drawingIndex)
        } else {
            map[mapKey] = IntArrayPattern(mapValue, drawingIndex)
        }
    }

    private fun calculatePatternProbabilities(map: TreeMap<String, IntArrayPattern>, totalDrawingsCount: Int) {
        map.forEach { (key, pattern) ->
            pattern.calculateProbability(totalDrawingsCount)
            val frequenciesTotalCount = pattern.frequencyMap.values.stream()
                    .map { it.timesOccurred }
                    .reduce(0, Int::plus)

            if (pattern.timesOccurred == frequenciesTotalCount + 1) {
                pattern.frequencyMap.forEach { (frequency, frequencyPattern) ->
                    frequencyPattern.calculateProbability(frequenciesTotalCount)
                }
            }
        }
    }

}