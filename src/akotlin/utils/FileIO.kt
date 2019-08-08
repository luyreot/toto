package akotlin.utils

import java.io.File

fun getCurrentYearTxtFilePath(): String = PATH_TXT_FOLDER.plus(CURRENT_YEAR)

fun getTxtFileContents(fileName: String): List<String> = File(fileName).readLines()

fun saveTxtFile(fileName: String, contents: String) = File(fileName).writeText(contents)