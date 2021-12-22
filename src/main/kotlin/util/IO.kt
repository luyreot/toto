package util

import java.io.File

/**
 * Handles reading and writing to files.
 */
object IO {

    // Returns the contents of a file as a list of strings
    // Reads from the provided file
    fun getTxtFileContents(file: File): List<String> {
        return file.readLines()
    }

    // Return the contents of a file as a list of string
    // Reads the file via the file name / path
    fun getTxtFileContents(fileName: String): List<String> {
        return File(fileName).readLines()
    }

    // Saves a file to specific directory
    fun saveTxtFile(fileName: String, contents: String) {
        File(fileName).writeText(contents)
    }

    // Return a list of files for provided directory
    fun getFiles(path: String): List<File>? {
        val files = File(path).listFiles()
        return files?.toList()?.filter { it.isFile && it.name != ".DS_Store" }
    }

}