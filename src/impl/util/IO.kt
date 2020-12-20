package impl.util

import java.io.File

object IO {

    fun getTxtFileContents(file: File): List<String> {
        return file.readLines()
    }

    fun getTxtFileContents(fileName: String): List<String> {
        return File(fileName).readLines()
    }

    fun saveTxtFile(fileName: String, contents: String) {
        File(fileName).writeText(contents)
    }

    fun getFiles(path: String): List<File>? {
        val files = File(path).listFiles()
        return files?.toList()?.filter { it.isFile && it.name != ".DSf_Store" }
    }

}