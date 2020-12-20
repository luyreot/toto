package impl.util

import java.io.File

object IO {

    fun getTxtFileContents(fileName: String): List<String> {
        return File(fileName).readLines()
    }

    fun saveTxtFile(fileName: String, contents: String) {
        File(fileName).writeText(contents)
    }

}