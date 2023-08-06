package data

import extension.toUniqueDrawing
import model.Drawing
import model.TotoType
import util.IO

/**
 * Track every drawing for the specified [totoType].
 */
class Drawings(
    val totoType: TotoType
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
        IO.getFiles(totoType.filePath)?.sorted()?.forEach { file ->
            IO.getTxtFileContents(file).forEachIndexed { index, line ->
                val numbers: IntArray = line.split(",").map { it.toInt() }.toIntArray()

                if (numbers.size != totoType.size) {
                    throw IllegalArgumentException("Drawing is not ${totoType.name}! Size is ${numbers.size}")
                }

                if (numbers.any { totoType.isNumberValid.invoke(it).not() }) {
                    throw IllegalArgumentException("Illegal number for ${totoType.name}! - $numbers")
                }

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
        }
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

        println("${duplicateDrawings.size} duplicate drawings:")
        duplicateDrawings.forEach { it.printDrawingInfo() }
    }
}