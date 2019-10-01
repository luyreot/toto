package akotlin.utils

import akotlin.model.Drawing
import java.io.File
import java.util.*

// Using a TreeMap to sort the drawings per year.
// The drawings themselves are sorted by the date the were released on.
val allDrawings = TreeMap<String, List<Drawing>>()

fun loadDrawingsForYears(vararg years: String) = years.forEach { year ->
    allDrawings[year] = getDrawingsFromFileContents(
            year,
            getTxtFileContents(
                    getYearTxtFilePath(year)
            )
    )
}

fun loadAllDrawings() = listFileNamesInInPath(PATH_TXT_FOLDER).forEach { file ->
    allDrawings[file.name] = getDrawingsFromFileContents(file.name, getTxtFileContents(file))
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