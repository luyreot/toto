package impl.extension

// Appends a string to a string builder and a new line at the end
fun StringBuilder.appendWithNewLine(text: String): StringBuilder = append(text + "\n")

// Converts a read drawing string from a txt file to an int array by splitting with ','
fun String.toDrawingArray(): IntArray = split(",").stream().mapToInt(String::toInt).toArray()