package data

import extension.toUniqueDrawing
import model.Drawing
import model.TotoType
import org.json.JSONArray
import org.json.JSONObject
import util.IO
import util.Logg

/**
 * Track every drawing for the specified [totoType].
 */
class Drawings(
    val totoType: TotoType,
    val yearFilter: Int
) {

    val drawings: List<Drawing>
        get() = _drawings
    private val _drawings = mutableListOf<Drawing>()

    init {
        loadDrawings()

        if (_drawings.isEmpty()) {
            throw IllegalArgumentException("Drawings are empty!")
        }

        printDuplicateDrawings()
    }

    private fun loadDrawings() {
        val yearDrawings = JSONObject()

        IO.getFiles(totoType.filePath)
            ?.sorted()
            ?.filter { file ->
                val pathSplit = file.path.split("/")
                val year = pathSplit[pathSplit.size - 1].toInt()
                year >= yearFilter
            }
            ?.forEach { file ->
                val yearDrawingsArray = JSONArray()

                IO.getTxtFileContents(file).forEachIndexed { index, line ->
                    val numbers: IntArray = line.split(",").map { it.toInt() }.toIntArray()

                    if (numbers.size != totoType.size) {
                        throw IllegalArgumentException("Drawing is not ${totoType.name}! Size is ${numbers.size}")
                    }

                    if (numbers.any { totoType.isNumberValid.invoke(it).not() }) {
                        throw IllegalArgumentException("Illegal number for ${totoType.name}! - $numbers")
                    }

                    val drawing = JSONObject()
                    drawing.put("issue", index + 1)
                    drawing.put("numbers", numbers)
                    yearDrawingsArray.put(drawing)

                    Drawing(
                        year = file.name.toInt(),
                        issue = index + 1, // Skip adding 0 index issues
                        numbers = numbers,
                        groupPattern = numbers.map { totoType.getNumberGroup.invoke(it) }.toIntArray(),
                        lowHighPattern = numbers.map { totoType.getNumberLowHigh.invoke(it) }.toIntArray(),
                        oddEvenPattern = numbers.map { totoType.getNumberOddEven.invoke(it) }.toIntArray()
                    ).let {
                        _drawings.add(it)
                    }
                }

                yearDrawings.put(file.name, yearDrawingsArray)
            }

//        println(yearDrawings)
    }

    private fun printDuplicateDrawings() {
        val duplicateNumbers: Set<IntArray> = _drawings
            .map { it.toUniqueDrawing() }
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }.keys
            .map { it.numbers }
            .toSet()

        val duplicateDrawings: List<Drawing> = _drawings.filter { drawing ->
            duplicateNumbers.any { drawing.numbers.contentEquals(it) }
        }

        Logg.p("${duplicateDrawings.size} duplicate drawings:")
        duplicateDrawings.forEach { it.printDrawingInfo() }
    }
}