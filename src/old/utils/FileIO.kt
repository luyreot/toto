package old.utils

import impl.utils.Const.PATH_TXT
import old.service.drawingsList
import old.service.drawingsMap
import java.io.File

/*
fun loadDrawingsForYears(vararg years: String) {
    drawingsMap.clear()
    drawingsList.clear()
    years.forEach { year ->
        drawingsMap[year] = getDrawingsFromFileContents(
                year,
                getTxtFileContents(
                        getYearTxtFilePath(year)
                )
        )
    }
    drawingsMap.forEach { (_, drawings) -> drawingsList.addAll(drawings) }
}
*/

fun loadAllDrawings() {
    drawingsMap.clear()
    drawingsList.clear()
    listFileNamesInInPath(PATH_TXT).forEach { file ->
        drawingsMap[file.name] = getDrawingsFromFileContents(file.name, getTxtFileContents(file))
    }
    drawingsMap.forEach { (_, drawings) -> drawingsList.addAll(drawings) }
}

fun getTxtFileContents(file: File): List<String> = file.readLines()

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