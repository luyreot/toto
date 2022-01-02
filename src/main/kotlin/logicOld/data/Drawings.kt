package logicOld.data

import logicOld.extension.toArrayDrawing
import logicOld.model.drawing.Drawing
import util.IO
import util.PATH_TXT
import java.util.*

/**
 * Holds a list of [Drawing], read from the txt files.
 */
object Drawings {

    val drawings = mutableListOf<Drawing>()

    /**
     * Using a TreeMap to sort the drawings per year.
     * The drawings themselves are sorted by the date the were released on.
     */
    fun loadDrawings(vararg years: String) {
        if (drawings.isEmpty().not()) drawings.clear()

        val map = if (years.isEmpty()) loadAll() else loadForYears(*years)
        map.forEach { (_, yearlyDrawings) -> drawings.addAll(yearlyDrawings) }
    }

    /**
     * Loads drawings for all years.
     */
    private fun loadAll(): TreeMap<String, List<Drawing>> {
        val map = TreeMap<String, List<Drawing>>()

        val files = IO.getFiles(PATH_TXT)
        if (files.isNullOrEmpty()) throw IllegalArgumentException("Did not load any files!")

        files.forEach { file ->
            val year = file.name
            val fileContents = IO.getTxtFileContents(file)
            val drawings = extractDrawings(year, fileContents)
            map[year] = drawings
        }

        return map
    }

    /**
     * Loads drawings for specified [years].
     */
    private fun loadForYears(vararg years: String): TreeMap<String, List<Drawing>> {
        val map = TreeMap<String, List<Drawing>>()

        years.forEach { year ->
            val fileContents = IO.getTxtFileContents(PATH_TXT + year)
            val drawings = extractDrawings(year, fileContents)
            map[year] = drawings
        }

        return map
    }

    /**
     * Converts [fileContents] to a list of [Drawing] for the provided [year].
     */
    private fun extractDrawings(year: String, fileContents: List<String>): List<Drawing> {
        val list = mutableListOf<Drawing>()

        // index starts at 0, but drawing issue/number should start at 1
        fileContents.forEachIndexed { index, line ->
            list.add(Drawing(year, index + 1, line.toArrayDrawing()))
        }

        return list
    }

    fun checkDrawings() {
        if (drawings.isEmpty()) {
            throw IllegalArgumentException("Drawings are empty!")
        }
    }

}