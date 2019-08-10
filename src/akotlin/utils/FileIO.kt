package akotlin.utils

import java.io.File

fun getCurrentYearTxtFilePath(): String = PATH_TXT_FOLDER.plus(CURRENT_YEAR)

fun getYearTxtFilePath(year: String): String = PATH_TXT_FOLDER.plus(year)

fun getTxtFileContents(fileName: String): List<String> = File(fileName).readLines()

fun saveTxtFile(fileName: String, contents: String) = File(fileName).writeText(contents)