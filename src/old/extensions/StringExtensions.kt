package old.extensions

fun String.toDrawingIntArray(): IntArray = split(",").stream().map { it.trim() }.mapToInt(String::toInt).toArray()