package akotlin.extensions

fun StringBuilder.appendDrawingsList(list: List<String>) = list.forEach { append(it.plus("\n")) }

fun StringBuilder.appendDrawing(drawing: String) = append(drawing.plus("\n"))!!