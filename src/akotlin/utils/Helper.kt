package akotlin.utils

import akotlin.model.Drawing

fun getDrawingsFromFileContents(year: String, drawings: List<String>): List<Drawing> {
    val objectList = mutableListOf<Drawing>()
    drawings.forEachIndexed { index, drawing ->
        objectList.add(
                Drawing(
                        year,
                        index + 1,
                        convertDrawingStringToIntArray(drawing)
                )
        )
    }
    return objectList
}

fun convertDrawingStringToIntArray(drawing: String): IntArray =
        drawing.split(",").stream().mapToInt(String::toInt).toArray()