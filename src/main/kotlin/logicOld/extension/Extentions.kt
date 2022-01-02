package logicOld.extension

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