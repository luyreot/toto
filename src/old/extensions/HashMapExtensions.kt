package old.extensions

fun MutableMap<String, MutableMap<String, Int>>.addChain(drawing: IntArray) {
    var firstKey: String
    var secondKey: String
    drawing.forEachIndexed { firstIndex, firstNumber ->
        firstKey = firstNumber.toString()
        drawing.forEachIndexed { secondIndex, secondNumber ->
            secondKey = secondNumber.toString()
            if (firstIndex != secondIndex) {
                val count = getOrPut(firstKey) { mutableMapOf() }.getOrPut(secondKey) { 0 }.inc()
                get(firstKey)!![secondKey] = count
            }
        }
    }
}

fun MutableMap<String, MutableMap<String, Int>>.addChain(prevDrawing: IntArray, currDrawing: IntArray) {
    var prevKey: String
    var currKey: String
    prevDrawing.forEach { prevNumber ->
        prevKey = prevNumber.toString()
        currDrawing.forEach { currNumber ->
            currKey = currNumber.toString()
            val count = getOrPut(prevKey) { mutableMapOf() }.getOrPut(currKey) { 0 }.inc()
            get(prevKey)!![currKey] = count
        }
    }
}

fun MutableMap<String, MutableMap<String, Int>>.addChain(prevColorPattern: String, currColorPattern: String) {
    val count = getOrPut(prevColorPattern) { mutableMapOf() }.getOrPut(currColorPattern) { 0 }.inc()
    get(prevColorPattern)!![currColorPattern] = count
}

fun MutableMap<String, MutableMap<String, Int>>.sortChain() {
    forEach { (key, value) ->
        set(key, value.toList().sortedBy { (_, _value) -> _value }.toMap().toMutableMap())
    }
}