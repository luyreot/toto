package akotlin.utils

import java.io.File

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