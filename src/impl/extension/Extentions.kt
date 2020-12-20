package impl.extension

fun StringBuilder.appendWithNewLine(text: String): StringBuilder = append(text + "\n")

fun String.toDrawingArray(): IntArray = split(",").stream().mapToInt(String::toInt).toArray()