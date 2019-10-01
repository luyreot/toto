package akotlin.service

import akotlin.model.ArrayPattern
import akotlin.model.Drawing
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
    val colorPatternsMap = TreeMap<String, ArrayPattern>()
    val lowHighPatternsMap = TreeMap<String, ArrayPattern>()
    val oddEvenPatternsMap = TreeMap<String, ArrayPattern>()

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
                processNumberPattern(numberPatternsMap, number, drawingIndex)
            }

            // Fill the color patterns map
            processArrayPattern(colorPatternsMap, convertDrawingArrayToColorPatternArray(drawing.numbers), drawingIndex)
            // Fill the odd-even patterns map
            processArrayPattern(lowHighPatternsMap, convertDrawingArrayToLowHighPatternArray(drawing.numbers), drawingIndex)
            // Fill the high-low patterns map
            processArrayPattern(oddEvenPatternsMap, convertDrawingArrayToOddEvenPatternArray(drawing.numbers), drawingIndex)
        }

        // Calculate probabilities of the number patterns
        calculateNumberPatternProbabilities(numberPatternsMap, totalDrawingsCount)
        // Calculate probabilities of the color patterns
        calculateArrayPatternProbabilities(colorPatternsMap, totalDrawingsCount)
        // Calculate probabilities of the odd-even patterns
        calculateArrayPatternProbabilities(lowHighPatternsMap, totalDrawingsCount)
        //Calculate probabilities of the high-low patterns
        calculateArrayPatternProbabilities(oddEvenPatternsMap, totalDrawingsCount)
    }

    private fun processNumberPattern(map: TreeMap<Int, NumberPattern>, number: Int, drawingIndex: Int) =
            if (map.containsKey(number)) {
                map[number]!!.incrementTimesOccurred()
                map[number]!!.addFrequency(drawingIndex)
            } else {
                map[number] = NumberPattern(number, drawingIndex)
            }

    private fun calculateNumberPatternProbabilities(map: TreeMap<Int, NumberPattern>, totalDrawingsCount: Int) =
            map.forEach { (number, numberPattern) ->
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

    private fun processArrayPattern(map: TreeMap<String, ArrayPattern>, mapValue: IntArray, drawingIndex: Int) {
        val mapKey = convertIntArrayToString(mapValue)
        if (map.containsKey(mapKey)) {
            map[mapKey]!!.incrementTimesOccurred()
            map[mapKey]!!.addFrequency(drawingIndex)
        } else {
            map[mapKey] = ArrayPattern(mapValue, drawingIndex)
        }
    }

    private fun calculateArrayPatternProbabilities(map: TreeMap<String, ArrayPattern>, totalDrawingsCount: Int) =
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