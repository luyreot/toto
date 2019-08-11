package akotlin.service

import akotlin.model.Drawing
import akotlin.model.NumberPattern
import akotlin.utils.*
import java.util.*
import kotlin.streams.toList

object DataService {

    val drawingsMap = TreeMap<String, List<Drawing>>()

    val numberPatternsMap = TreeMap<Int, NumberPattern>()

    fun loadDrawingsForYears(vararg years: String) = years.forEach { year ->
        drawingsMap[year] =
                getDrawingsFromFileContents(
                        year,
                        getTxtFileContents(
                                getYearTxtFilePath(year)
                        )
                )

    }

    fun loadAllDrawings() = listFileNamesInInPath(PATH_TXT_FOLDER).forEach { file ->
        drawingsMap[file.name] =
                getDrawingsFromFileContents(
                        file.name,
                        getTxtFileContents(file)
                )
    }

    fun calculatePatterns() {
        val drawings = drawingsMap.values.stream().flatMap { it.stream() }.toList()
        val totalDrawingsCount = drawings.count()

        drawings.forEachIndexed { drawingIndex, drawing ->
            drawing.numbers.forEachIndexed { numberIndex, number ->

                if (numberPatternsMap.containsKey(number)) {
                    numberPatternsMap[number]?.incrementTimesOccurred()
                    numberPatternsMap[number]?.addFrequency(drawingIndex)

                } else {
                    numberPatternsMap[number] = NumberPattern(number, drawingIndex)
                }

            }
        }

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

    }

}