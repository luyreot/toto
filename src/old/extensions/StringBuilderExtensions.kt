package old.extensions

fun StringBuilder.appendDrawingsStringList(list: List<String>) = list.forEach { append(it.plus("\n")) }

fun StringBuilder.appendDrawingString(drawing: String) = append(drawing.plus("\n"))!!