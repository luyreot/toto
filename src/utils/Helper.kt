package utils

import crawler.WebCrawler
import model.Drawing

fun updateYearDrawings() = WebCrawler(getTxtFileContents(getCurrentYearTxtFilePath())).crawl()

fun getDrawingsFromFileContents(year: String, drawings: List<String>): List<Drawing> {
    val objectList = mutableListOf<Drawing>()
    drawings.forEachIndexed { index, drawing ->
        objectList.add(
                Drawing(
                        year,
                        index + 1,
                        convertDrawingStringToIntArray(drawing)
                )
        )
    }
    return objectList
}

fun convertDrawingStringToIntArray(drawing: String): IntArray =
        drawing.split(",").stream().mapToInt(String::toInt).toArray()

fun convertDrawingArrayToColorPatternArray(drawing: IntArray): IntArray =
        drawing.map { number -> number / 10 }.toIntArray()

fun convertDrawingArrayToLowHighPatternArray(drawing: IntArray): IntArray =
        drawing.map { number -> if (number <= HIGH_LOW_MIDPOINT) 0 else 1 }.toIntArray()

fun convertDrawingArrayToOddEvenPatternArray(drawing: IntArray): IntArray =
        drawing.map { number -> if ((number and 1) == 0) 1 else 0 }.toIntArray()

fun convertIntArrayToString(drawing: IntArray): String =
        drawing.asList().toString().replaceFirst("[", "", true).replaceFirst("]", "", true)