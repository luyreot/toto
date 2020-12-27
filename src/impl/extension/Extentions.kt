package impl.extension

// Appends a string to a string builder and a new line at the end
fun StringBuilder.appendLine(text: String): StringBuilder = append(text + "\n")

// Converts a read drawing string from a txt file to an int array by splitting with ','
fun String.toArrayDrawing(): IntArray = split(",")
        .stream()
        .mapToInt(String::toInt)
        .toArray()

// Converts an int array to a string used as a key in a map
fun IntArray.toStringDrawing(): String = toList()
        .toString()
        .replaceFirst("[", "", true)
        .replaceFirst("]", "", true)