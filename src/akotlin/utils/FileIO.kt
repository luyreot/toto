package akotlin.utils

import akotlin.model.Drawing
import java.io.File
import java.util.*
import kotlin.streams.toList

// Using a TreeMap to sort the drawings per year.
// The drawings themselves are sorted by the date the were released on.
val drawingsMap = TreeMap<String, List<Drawing>>()
val drawingsList = mutableListOf<Drawing>()
var totalDrawingsCount: Int = 0

fun loadDrawingsForYears(vararg years: String) = years.forEach { year ->
    drawingsMap[year] = getDrawingsFromFileContents(
            year,
            getTxtFileContents(
                    getYearTxtFilePath(year)
            )
    )
    drawingsList.addAll(drawingsMap.values.stream().flatMap { drawingList -> drawingList.stream() }.toList())
    totalDrawingsCount = drawingsList.count()
}

fun loadAllDrawings() = listFileNamesInInPath(PATH_TXT_FOLDER).forEach { file ->
    drawingsMap[file.name] = getDrawingsFromFileContents(file.name, getTxtFileContents(file))
    drawingsList.addAll(drawingsMap.values.stream().flatMap { drawingList -> drawingList.stream() }.toList())
    totalDrawingsCount = drawingsList.count()
}

fun getCurrentYearTxtFilePath(): String = PATH_TXT_FOLDER.plus(CURRENT_YEAR)

fun getYearTxtFilePath(year: String): String = PATH_TXT_FOLDER.plus(year)

fun getTxtFileContents(fileName: String): List<String> = File(fileName).readLines()

fun getTxtFileContents(file: File): List<String> = file.readLines()

fun saveTxtFile(fileName: String, contents: String) = File(fileName).writeText(contents)

fun listFileNamesInInPath(path: String): List<File> {
    val actualFiles = mutableListOf<File>()
    val listedFiles = File(path).listFiles()
    listedFiles?.forEach { file ->
        if (file.isFile && file.name != ".DS_Store") {
            actualFiles.add(file)
        }
    }
    return actualFiles
}