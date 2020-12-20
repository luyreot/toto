package old.extensions

fun IntArray.toDrawingString(): String =
        toList().toString().replaceFirst("[", "", true).replaceFirst("]", "", true)